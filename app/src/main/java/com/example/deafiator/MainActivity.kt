package com.example.deafiator

import DeafiatorTheme
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        val notificationChannel = NotificationChannel(
            "notification_channel_id",
            "Notification name",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        UserManager.initialize(this)
        database = FirebaseDatabase.getInstance().reference


        if(isUserSignedIn()){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            this.finish()
        }else{
            setContent {
                DeafiatorTheme{
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ){
                        NavigateMain()
                    }
                }
            }
        }
    }

    private fun isUserSignedIn(): Boolean {
        val userData = UserManager.getUserData()
        return userData.isNotEmpty() && userData["username"] != null && userData["password"] != null
    }

}

@Composable
fun NavigateMain(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "startPage"){
        composable("startPage"){
            MainPage(navController)
        }
        composable("SignupPage"){
            SignupPageMain(navController)
        }
        composable("LoginPage"){
            LoginPageMain(navController)
        }
        composable("CongratsPage"){
            CongratsScreen(navController)
        }
    }
}