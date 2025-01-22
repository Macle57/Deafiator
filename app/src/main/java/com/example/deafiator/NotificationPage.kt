package com.example.deafiator

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class Notification(val message: String, val sender: String, val receiver: String, val time: String)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotificationPage(navController: NavHostController) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val data = UserManager.getUserData()
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var devicesList by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var searchName by remember { mutableStateOf("") }

    fun getNotifications() {
        val userRef = database.child("groups").child("Robogyan").child("Notifications")

        userRef.get().addOnSuccessListener { dataSnapshot ->
            val newDevicesList = mutableListOf<Notification>()

            if (dataSnapshot.exists()) {
                for(notification in dataSnapshot.children){
                    val message = notification.child("message").getValue(String::class.java) ?: ""
                    val sender = notification.child("sender").getValue(String::class.java) ?: ""
                    val receiver = notification.child("reciever").getValue(String::class.java) ?: ""
                    val time = notification.child("time").getValue(String::class.java) ?: ""
                    newDevicesList.add(Notification(message, sender, receiver, time))
                }
                devicesList = newDevicesList
                Log.e("Firebase$$", "Notifications: $devicesList")
            } else {
                Log.d("Firebase@@", "No notifications found")
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase@@", "Failed to fetch notifications", exception)
        }
    }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding) //innerPadding
                    .fillMaxSize()
                    .background(color = Color(0xFFededed)),
            ){
                Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 0.1.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp)
                    )
                    .background(
                        color = Color(0xFF908bc3), //0xFF908bc3
                        shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp)
                    ),
                    )
                {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 0.015 * screenHeight,
                            end = 0.015 * screenWidth,
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IconButton(onClick = { navController.navigate("") }) {
                        Icon(Icons.Default.Menu,tint = Color.Black, contentDescription = "")
                    }
                    Text(
                        text = "Notifications",
                        color = Color.Black,
                        fontWeight = FontWeight.W800,
                        fontSize = 0.05 * screenWidth.value.sp
                    )
                    IconButton(onClick = { navController.navigate("ProfilePage") }) {
                        Image(painter = painterResource(id = R.drawable.profile), contentDescription = "")
                    }
                }
                Spacer(modifier = Modifier.size(0.03 * screenHeight))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 0.05 * screenWidth, end = 0.05 * screenWidth),
                    shape = RoundedCornerShape(10.dp),
                    value = searchName,
                    onValueChange = {searchName = it},
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color.Black,
                        //unfocusedBorderColor = Color(0xFF818183),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(0.08 * screenWidth),
                            tint = Color.Black
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Search",
                            color = Color.Black,
                            fontSize = 0.02 * screenHeight.value.sp
                        )
                    },
                )
                Spacer(modifier = Modifier.size(0.04 * screenHeight))
            }
                Spacer(modifier = Modifier.size(0.02 * screenHeight))
                Column(
                    modifier = Modifier
                        .padding(
                            start = 0.04 * screenWidth,
                            end = 0.04 * screenWidth
                        )
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    getNotifications()
                    if (searchName.isEmpty()) {
                        Column {
                            repeat(devicesList.size) { index ->
                                //val (key, value) = notification.keys.first() to notification.values.first()
                                //Log.d("$@@$", "Key: $key, Value: $value")
                                if (devicesList[index].receiver == data["username"]) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.AccountCircle,
                                            contentDescription = "Notification",
                                            tint = Color(0xFF908bc3),
                                            modifier = Modifier.size(0.15 * screenWidth)
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 0.04 * screenWidth),
                                        ) {
                                            Text(
                                                text = "${devicesList[index].sender}: " + devicesList[index].message,
                                                fontSize = 18.sp,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Sent at: ${devicesList[index].time}",
                                                fontSize = 14.sp,
                                                color = Color(0xFF818183)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(0.1 * screenHeight))
                                    }
                                }
                            }
                        }
                    } else {
                        Column {
                            repeat(devicesList.size) { index ->
                                if ((devicesList[index].message.contains(searchName) || devicesList[index].sender.contains(searchName)) && devicesList[index].receiver == data["username"]) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.AccountCircle,
                                            contentDescription = "Notification",
                                            tint = Color(0xFF908bc3),
                                            modifier = Modifier.size(0.15 * screenWidth)
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 0.04 * screenWidth),
                                        ) {
                                            Text(
                                                text = "${devicesList[index].sender}: " + devicesList[index].message,
                                                fontSize = 18.sp,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Sent at: ${devicesList[index].time}",
                                                fontSize = 14.sp,
                                                color = Color(0xFF818183)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(0.1 * screenHeight))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentColor = Color.Transparent,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    IconButton(onClick = { navController.navigate("HomePage"){
                        popUpTo("NotificationPage") {
                            inclusive = true
                        }
                    } }) {
                        Icon(
                            painterResource(id = R.drawable.home_outlined),
                            tint = Color(0xFF818183),
                            contentDescription = "Home")
                    }
                    IconButton(onClick = { navController.navigate("MapPage"){
                        popUpTo("NotificationPage") {
                            inclusive = true
                        }
                    } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.location_outline),
                            tint = Color(0xFF818183),
                            contentDescription = "Add")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.notification),
                            tint = Color(0xFF908bc3),
                            contentDescription = "Profile")
                    }
                    IconButton(onClick = {
                        if (data["admin"] == true) {
                            navController.navigate("ProfilePage"){
                                popUpTo("NotificationPage") {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.navigate("UserProfile")
                        }}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.user_outline),
                            tint = Color(0xFF818183),
                            contentDescription = "Profile")
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NotificationPagePreview() {
    NotificationPage(rememberNavController())
}