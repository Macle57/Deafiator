#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

// Define Firebase API details
#define WIFI_SSID "Airtel_Abhi kumar"
#define WIFI_PASSWORD ""                     //password
#define API_KEY ""                           // api key 
#define DATABASE_URL "https://deafiatorsih-default-rtdb.firebaseio.com/"

// Firebase objects
FirebaseData fbdo; 
FirebaseAuth auth;
FirebaseConfig config;

bool signupSuccess = false; // Track Firebase sign-in status

// GPIO Pin Definitions
#define sw 19 // Push button connected to pin 33
#define red 27 // Red LED
#define green 26 // Green LED
#define blue 2 // Blue LED
#define yellow 25 // Yellow LED
#define buzzer 32  // Buzzer
#define vb 13  // vibrationMotor

// Variables for button debounce and state tracking
int lastButtonState = HIGH;   
unsigned long lastDebounceTime = 0;  
unsigned long debounceDelay = 50;    
bool buttonState = HIGH; 
byte tapCounter; 
int timediff; 
bool flag1, flag2; 
long double presstime, releasetime; 
bool buzzerActive = false; // Flag to track if buzzer is active
bool redState = false;
bool greenState = false;
bool blueState = false;
bool yellowState = false;
bool vbState = false;

// Timing variables
unsigned long previousMillis = 0; // Store last time data was fetched
const long interval = 2000; // Interval to fetch data (2 seconds)

void setup() {
  // Initialize Serial Monitor
  Serial.begin(115200); 
  
  // Configure GPIO pins
  pinMode(sw, INPUT_PULLUP); 
  pinMode(red, OUTPUT); digitalWrite(red, LOW);
  pinMode(green, OUTPUT); digitalWrite(green, LOW);
  pinMode(blue, OUTPUT); digitalWrite(blue, HIGH);
  pinMode(yellow, OUTPUT); digitalWrite(yellow, LOW);
  pinMode(buzzer, OUTPUT); digitalWrite(buzzer, LOW); // Initialize buzzer pin
  pinMode(vb, OUTPUT); digitalWrite(vb, LOW);

  // Connect to Wi-Fi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(200);
  }
  Serial.println();
  Serial.print("Connected to Wi-Fi, IP: ");
  Serial.println(WiFi.localIP());

  // Set Firebase project details
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  // Assign the token status callback
  config.token_status_callback = tokenStatusCallback; 

  // Begin Firebase and sign up anonymously
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("Firebase Sign-Up Success");
    signupSuccess = true;
  } else {
    Serial.printf("Firebase Sign-Up Failed: %s\n", config.signer.signupError.message.c_str());
  }

  // Initialize Firebase
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  // Fetch Firebase data every 2 seconds
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis; 
    fetchFirebaseData("/devices"); 
  }

  // Read the button state
  int reading = digitalRead(sw);

  if (reading != lastButtonState) {
    lastDebounceTime = millis(); // Reset the debouncing timer
  }

  if ((millis() - lastDebounceTime) > debounceDelay) {
    if (reading != buttonState) {
      buttonState = reading;
    }
  }

  // Check button press and release logic
  if (buttonState == 0 && flag2 == 0) {
    presstime = millis(); // Save press time
    flag1 = 0;
    flag2 = 1;
    tapCounter++; // Increment tap counter
  }

  if (buttonState == 1 && flag1 == 0) {
    releasetime = millis(); // Save release time
    flag1 = 1;
    flag2 = 0;
    timediff = releasetime - presstime; // Calculate time difference
  }

  // Detect and handle tap events
  if ((millis() - presstime) > 400 && buttonState == 1) {
    if (tapCounter == 1) {
      if (timediff >= 400) {
        Serial.println("Hold : Emergency"); 
        updateFirebase("/devices/ankur/emergency", true);
        hold();
      } else {
        Serial.println("Single Tap");
        // singleTap(); 
        updateFirebase("/devices/ankur/clicked", true);
      }
    } else if (tapCounter == 2) {
      if (timediff >= 400) {
        Serial.println("Single Tap and Hold : Device is in Reset Mode");
        tapAndHold(); 
      } else {
        Serial.println("Double Tap");
        // doubleTap();
        updateFirebase("/devices/jayam/clicked", true);
      }
    } else if (tapCounter == 3) {
      Serial.println("Triple Tap");
      // tripleTap();
      updateFirebase("/devices/rg/clicked", true);
    } else if (tapCounter == 4) {
      Serial.println("Four Tap");
      updateFirebase("/devices/id/clicked", true);
      fourTap();  
    }
    tapCounter = 0;
  }
  
  lastButtonState = reading;
}

// Function to update Firebase and then fetch data
void updateFirebase(const String &path, bool newValue) {
  if (Firebase.ready() && signupSuccess) {
    if (Firebase.RTDB.setBool(&fbdo, path, newValue)) {
      Serial.printf("Firebase Update Success: %s = %s\n", path.c_str(), newValue ? "true" : "false");
      
      // Fetch the updated data from Firebase after the update
      fetchFirebaseData("/devices");
    } else {
      Serial.printf("Firebase Update Failed: %s\n", fbdo.errorReason().c_str());
    }
  }
}

// Function to read data from Firebase
void fetchFirebaseData(const String &path) {
  if (Firebase.ready() && signupSuccess) {
    if (Firebase.RTDB.get(&fbdo, path)) {
      Serial.printf("Firebase Read Success: %s\n", path.c_str());
      
      // Process the specific data under "/devices"
      if (fbdo.dataType() == "json") {
        FirebaseJson &json = fbdo.jsonObject();
        FirebaseJsonData jsonData;

        // Check if "ankur/clicked" exists and update LED state
        if (json.get(jsonData, "ankur/clicked")) {
          bool ankurClicked = jsonData.boolValue;
          Serial.printf("ankur/clicked: %s\n", ankurClicked ? "true" : "false");
          if (ankurClicked != redState) { // Update red LED only if state changed
            redState = ankurClicked;
            digitalWrite(red, redState ? HIGH : LOW);
          }
        }

        // Check if "jayam/clicked" exists and update LED state
        if (json.get(jsonData, "jayam/clicked")) {
          bool jayamClicked = jsonData.boolValue;
          Serial.printf("jayam/clicked: %s\n", jayamClicked ? "true" : "false");
          if (jayamClicked != greenState) { // Update green LED only if state changed
            greenState = jayamClicked;
            digitalWrite(green, greenState ? HIGH : LOW);
          }
        }

        // Check if "rg/clicked" exists and update LED state
        if (json.get(jsonData, "rg/clicked")) {
          bool rgClicked = jsonData.boolValue;
          Serial.printf("rg/clicked: %s\n", rgClicked ? "true" : "false");
          if (rgClicked != blueState) { // Update blue LED only if state changed
            blueState = rgClicked;
            digitalWrite(yellow, rgClicked ? HIGH : LOW); // Turn on/off yellow LED
            // digitalWrite(green, rgClicked ? HIGH : LOW); // Turn on/off green LED
          }
        }

        // Check if "id/clicked" exists and update LED state
        if (json.get(jsonData, "id/clicked")) {
          bool idClicked = jsonData.boolValue;
          Serial.printf("id/clicked: %s\n", idClicked ? "true" : "false");
          if (idClicked != yellowState) { // Update yellow LED only if state changed
            yellowState = idClicked;
            digitalWrite(yellow, yellowState ? HIGH : LOW);
            // if (yellowState) {
            //   blinkYellowLed(); // Call blink function if yellow LED should be on
            // }
          }
        }

       if (json.get(jsonData, "dinner/clicked")) {
          bool dinnerClicked = jsonData.boolValue;
          Serial.printf("dinner/clicked: %s\n", dinnerClicked ? "true" : "false");
          if (dinnerClicked != vbState) { // Update vb state only if state changed
            vbState = dinnerClicked;
            // checking the vb state
            if (vbState) {
              vibration(); // Call vibration function if vb should be on
            }
          }
        }
      }
    } else {
      Serial.printf("Firebase Read Failed: %s\n", fbdo.errorReason().c_str());
    }
  }
}

// Function for vibration
void vibration() {
  for (int i = 0; i < 10; i++) { // vibrate 10 times
    digitalWrite(vb, HIGH);
    delay(200);
    digitalWrite(vb, LOW);
    delay(200);
    digitalWrite(vb, HIGH);
    delay(200);
    digitalWrite(vb, LOW);

    delay(500);
  }
}

// Functions for LED control based on different tap events
// void singleTap() {
//   digitalWrite(red, HIGH);
//   digitalWrite(green, LOW);
//   digitalWrite(blue, LOW);
//   digitalWrite(yellow, LOW);
// }

// void doubleTap() {
//   digitalWrite(red, LOW);
//   digitalWrite(green, HIGH);
//   digitalWrite(blue, LOW);
//   digitalWrite(yellow, LOW);
// }

// void tripleTap() {
//   digitalWrite(red, LOW);
//   digitalWrite(green, LOW);
//   digitalWrite(blue, HIGH);
//   digitalWrite(yellow, LOW);
// }

void fourTap() {
  digitalWrite(red, LOW);
  digitalWrite(green, LOW);
  digitalWrite(blue, HIGH);
  digitalWrite(yellow, LOW);
}

void hold() {
  redState = true;
  yellowState = true;
  greenState = true;
  buzzerActive = true; // Set buzzer flag to true
  while (buzzerActive) {
    digitalWrite(buzzer, HIGH); // Turn on buzzer
    digitalWrite(red, HIGH);
    digitalWrite(yellow, LOW);
    digitalWrite(green, LOW);
    delay(200); // Wait for 200 ms
    digitalWrite(buzzer, LOW); // Turn off buzzer
    digitalWrite(red, LOW);
    digitalWrite(yellow, HIGH);
    digitalWrite(green, LOW);
    delay(200);
    digitalWrite(buzzer, HIGH); // Turn on buzzer
    digitalWrite(red, LOW);
    digitalWrite(yellow, LOW);
    digitalWrite(green, HIGH);
    delay(200);
    digitalWrite(buzzer, LOW); // Wait for 200 ms
    if (digitalRead(sw) == 0) { // Check if the button is pressed again
      buzzerActive = false; // Stop the buzzer if button is pressed
    }
  }
}

void tapAndHold() {
  nolight();
  digitalWrite(green, LOW);
  digitalWrite(blue, LOW);
}

void nolight() {
  digitalWrite(red, LOW);
  digitalWrite(green, LOW);
  digitalWrite(blue, LOW);
}
