import cv2
import mediapipe as mp
import matplotlib.pyplot as plt
import numpy as np

# Initialize Mediapipe for palm detection
mp_hands = mp.solutions.hands
mp_drawing = mp.solutions.drawing_utils

# Initialize video capture
cap = cv2.VideoCapture(0)

# Define the gestures to be recognized (for simplicity)
GESTURES = {
    'palm_open': 'Open Palm',
    'fist': 'Fist'
}

# Simple gesture recognition based on landmarks
def recognize_gesture(landmarks):
    if not landmarks:
        return None

    # Palm open detection logic (very simple, can be improved with more advanced logic)
    if landmarks[0].y < landmarks[9].y:  # Thumb tip is higher than wrist
        return 'palm_open'
    
    # Fist detection (again, very basic)
    if landmarks[12].y > landmarks[9].y:  # Middle finger tip is lower than wrist
        return 'fist'
    
    return None

# Function to plot the text using Matplotlib and display it on OpenCV stream
def plot_text(frame, text, pos=(50, 400)):
    # Create a blank canvas for text
    fig, ax = plt.subplots(figsize=(6, 1))
    ax.text(0.5, 0.5, text, fontsize=20, ha='center', va='center')
    ax.axis('off')
    fig.canvas.draw()
    
    # Convert the Matplotlib plot to a numpy array (RGB format)
    text_image = np.frombuffer(fig.canvas.tostring_rgb(), dtype=np.uint8)
    text_image = text_image.reshape(fig.canvas.get_width_height()[::-1] + (3,))
    
    # Resize the plot and place it on the OpenCV frame
    text_image_resized = cv2.resize(text_image, (frame.shape[1], 50))
    frame[pos[1]:pos[1]+50, :] = text_image_resized
    
    plt.close(fig)  # Close the figure after plotting

with mp_hands.Hands(static_image_mode=False, max_num_hands=1, min_detection_confidence=0.7) as hands:
    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break

        # Convert the frame to RGB for Mediapipe
        image_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        result = hands.process(image_rgb)

        gesture_text = 'No Gesture Detected'
        
        # Process the detected hand landmarks
        if result.multi_hand_landmarks:
            for hand_landmarks in result.multi_hand_landmarks:
                # Draw hand landmarks on the frame
                mp_drawing.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)

                # Recognize gesture based on landmarks
                gesture = recognize_gesture(hand_landmarks.landmark)
                if gesture in GESTURES:
                    gesture_text = GESTURES[gesture]

        # Plot the recognized gesture as text below the video stream
        plot_text(frame, gesture_text)

        # Show the frame
        cv2.imshow('Palm Gesture Detection', frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

# Release the video capture and close OpenCV windows
cap.release()
cv2.destroyAllWindows()
