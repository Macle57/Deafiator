package com.example.deafiator

import DeafiatorTheme
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

//data class Location(val latitude: Double, val longitude: Double)

class HomeActivity : ComponentActivity(){

    private lateinit var database: DatabaseReference
    private lateinit var userId: String
    val data = UserManager.getUserData()
    val userList = mutableListOf<String>()
    var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance().reference
        UserManager.initialize(this)

        userId = data["username"] as String
        super.onCreate(savedInstanceState)
        listenForEmergencyUpdates()
        updateFromApp()
        //6623416
        setContent {
            DeafiatorTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    NavigateHome()
                }
            }
        }
    }


    fun fetchAllUsers() {
        val robogyanRef = database.child("groups").child("Robogyan")

        robogyanRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterate through all users under "Robogyan" and store their usernames
                for (userSnapshot in dataSnapshot.children) {
                    if(userSnapshot.child("username").getValue(String::class.java) != "Notifications") {

                        val username = userSnapshot.child("username").getValue(String::class.java)
                        //val emergency = userSnapshot.child("emergency").getValue(String::class.java)
                        username?.let { userList.add(it) }
                    }
                }

                // Do something with the user list, like logging or storing it locally
                Log.d("$$#@@@", "All Users: $userList")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle potential errors
            }
        })
    }

    private fun updateFromApp() {
        val userRef = database.child("groups").child("Robogyan").child("Notifications")
        var sender = ""
        var message = ""
        var receiver = ""
        val notificationManager = this.getSystemService(NotificationManager::class.java)
        val notificationChannelID = "notification_channel_id"

        fun showSimpleNotification(snapshot: DataSnapshot, message: String, sender: String) {
            val notification = NotificationCompat.Builder(this@HomeActivity, notificationChannelID)
                .setContentTitle("Message from $sender")
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_outline)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .build()  // finalizes the creation

            notificationManager.notify(Random.nextInt(), notification)
        }

        fun showEmergencyAlert() {
            val dialog = AlertDialog.Builder(this@HomeActivity)
                .setTitle("Message from $sender.")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }

        var lastProcessedKey: String? = null

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return

                // Get the last child (most recent notification)
                val lastChild = snapshot.children.lastOrNull() ?: return
                val key = lastChild.key ?: return
                receiver = lastChild.child("reciever").getValue(String::class.java) ?: "Unknown Receiver"

                // Check if this is a new notification
                if (key != lastProcessedKey && receiver == userId) {
                    sender = lastChild.child("sender").getValue(String::class.java) ?: "Unknown Sender"
                    message = lastChild.child("message").getValue(String::class.java) ?: "No Message"
                    val time = lastChild.child("time").getValue(String::class.java) ?: "No Time"

                    Log.d("$$@@", "New Notification - Sender: $sender, Receiver: $receiver, Message: $message, Time: $time")

                    showEmergencyAlert()
                    showSimpleNotification(lastChild, message, sender)
                    lastProcessedKey = key
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Failed to read notifications: ${databaseError.message}")
            }
        })
    }

    private fun listenForEmergencyUpdates() {
        val userRef = database.child("groups").child("Robogyan").child(userId).child("emergency")
        val userRef2 = database.child("devices").child("Emergency").child("clicked")

        val databaseRef = FirebaseDatabase.getInstance().getReference("groups").child("Robogyan")
        val databaseRef2 = FirebaseDatabase.getInstance().getReference("devices").child("Emergency")


        fun updateEmergencyStatus(){
            val newValue = false
            databaseRef.child(userId).child("emergency").setValue(newValue).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@HomeActivity, "", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@HomeActivity, "Database update failed!", Toast.LENGTH_SHORT).show()
                }
            }

            databaseRef2.child("clicked").setValue(newValue).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@HomeActivity, "", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@HomeActivity, "Database update failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fun transferAlert(userList: MutableList<String>){
            val newValue = true
            userList.remove(userId)
            val currentUser = userList[0]
            status = "Emergency Alert sent to $currentUser!"
            databaseRef.child(currentUser).child("emergency").setValue(newValue)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                    } else {
                        Toast.makeText(this@HomeActivity, "Database update failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        fun showEmergencyAlert() {
            // Create an AlertDialog to show the alert
            val dialog = AlertDialog.Builder(this@HomeActivity)
                .setTitle("Emergency Alert")
                .setMessage("Ankur could not attend. Can you attend this emergency?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    updateEmergencyStatus()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            // Show the alert dialog
            dialog.show()

            val handler = Handler(Looper.getMainLooper())

            // Post a delayed task to call updateEmergencyStatus() after 10 seconds
            handler.postDelayed({
                if (dialog.isShowing) {
                    // Call the function if the dialog is still open (user hasn't clicked OK)
                    transferAlert(userList)
                    updateEmergencyStatus()
                    // Optionally, you can dismiss the dialog after the 10 seconds action
                    dialog.dismiss()
                }
            }, 4000)
        }

        // Set up a ValueEventListener
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Read the "emergency" value
                val isEmergency = dataSnapshot.getValue(Boolean::class.java) ?: false

                // Handle the updated value
                if (isEmergency) {
                    fetchAllUsers()
                    UserManager.editEmergency(false)
                    showEmergencyAlert()
                    Toast.makeText(this@HomeActivity, status, Toast.LENGTH_SHORT).show()
                    Log.e("$$@@", "Emergency status: $userList")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }
}

@Composable
fun NavigateHome(){

    val context = LocalContext.current

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "HomePage"){
        composable("HomePage"){
            HomePage(navController)
        }
        composable("NotificationPage"){
            NotificationPage(navController)
        }
        composable("MapPage"){
            MapPage(navController)
        }
        composable("ProfilePage"){
            ProfilePage(navController)
        }
        composable("AddUser"){
            AddUsersPage(navController)
        }
        composable("UserProfile"){
            UserProfilePage(navController)
        }
        composable("ViewGroup"){
            ViewMembersPage(navController)
        }
    }

}