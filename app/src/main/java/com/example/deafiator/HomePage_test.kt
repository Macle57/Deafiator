package com.example.deafiator

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.FirebaseDatabase

@Composable
fun HomePageMain(navController: NavHostController){

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var lastChangedKey by remember { mutableStateOf<String?>(null) }
    var value by remember { mutableStateOf<String>("")}

//    DisposableEffect(Unit) {
//        val databaseRef = FirebaseDatabase.getInstance().getReference("devices")
//        val listener = object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                lastChangedKey = snapshot.key
//                val dataMap = snapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
//                value = dataMap?.get("clicked")?.toString() ?: ""
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//                // Handle child removed if necessary
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                // Handle child moved if necessary
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle cancelled
//            }
//        }
//
//        databaseRef.addChildEventListener(listener)
//
//        onDispose {
//            databaseRef.removeEventListener(listener)
//        }
//    }

    fun updateLastChangedValue() {
        Log.e("@@@@ 2",lastChangedKey.toString())
        if (lastChangedKey != null) {
            // Reference to Firebase Realtime Database node
            val databaseRef = FirebaseDatabase.getInstance().getReference("devices")

            // Update the last changed key with a new value
            val newValue = "false"
            databaseRef.child(lastChangedKey!!).child("clicked").setValue(newValue).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Emergency Attended by $lastChangedKey!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Database update failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "No changes detected to update.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(color = Color(0xFF131621)),
            ){
                Spacer(modifier = Modifier.height(0.07 * screenHeight))
                Column(
                    modifier = Modifier.padding(start = 0.08 * screenWidth, end = 0.08 * screenWidth),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.03 * screenWidth),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ankur",
                                fontSize = 24.sp,
                                color = Color(0xFF0078e0),
                            )
                            Spacer(modifier = Modifier.height(0.02 * screenHeight))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                FloatingActionButton(
                                    containerColor = if (lastChangedKey == "ankur" && value == "true") Color.Red else Color.White,
                                    onClick = { updateLastChangedValue() },
                                    modifier = Modifier
                                        .width(100.dp)
                                ) {
                                    Text(text = if (lastChangedKey == "ankur" && value == "true") "Emergency" else "No Alert")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(0.07 * screenHeight))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.03 * screenWidth)
                        ) {
                            Text(
                                text = "Id",
                                fontSize = 24.sp,
                                color = Color(0xFF0078e0),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(0.02 * screenHeight))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                FloatingActionButton(
                                    containerColor = if (lastChangedKey == "id" && value == "true") Color.Red else Color.White,
                                    onClick = { updateLastChangedValue() },
                                    modifier = Modifier
                                        .width(100.dp)
                                ) {
                                    Text(text = if (lastChangedKey == "id" && value == "true") "Emergency" else "No Alert")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(0.07 * screenHeight))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.03 * screenWidth)
                        ) {
                            Text(
                                text = "Jayam",
                                fontSize = 24.sp,
                                color = Color(0xFF0078e0),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(0.02 * screenHeight))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                FloatingActionButton(
                                    containerColor = if (lastChangedKey == "jayam" && value == "true") Color.Red else Color.White,
                                    onClick = { updateLastChangedValue() },
                                    modifier = Modifier
                                        .width(100.dp)
                                ) {
                                    Text(text = if (lastChangedKey == "jayam" && value == "true") "Emergency" else "No Alert")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(0.07 * screenHeight))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.03 * screenWidth)
                        ) {
                            Text(
                                text = "Robogyan",
                                fontSize = 24.sp,
                                color = Color(0xFF0078e0),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(0.02 * screenHeight))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                FloatingActionButton(
                                    containerColor = if (lastChangedKey == "rg" && value == "true") Color.Red else Color.White,
                                    onClick = { updateLastChangedValue() },
                                    modifier = Modifier
                                        .width(100.dp)
                                ) {
                                    Text(text = if (lastChangedKey == "rg" && value == "true") "Emergency" else "No Alert")
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = Color(0,0,0),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.width(24.dp))
                    FloatingActionButton(
                        containerColor = Color(255, 255, 255),
//                        contentColor = Color(54, 1, 1),
                        onClick = { navController.navigate("home") },
                        modifier = Modifier
                            .width(100.dp)
                            .padding(end = 16.dp)
                    ) {
                        Icon(Icons.Filled.Home, contentDescription = "Home", Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    FloatingActionButton(
                        containerColor = Color(255, 255, 255),
//                        contentColor = Color(54, 1, 1),
                        onClick = { navController.navigate("alerts") },
                        modifier = Modifier
                            .width(100.dp)
                            .padding(end = 16.dp)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.icon), contentDescription = "Search", Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    FloatingActionButton(
                        containerColor = Color(255, 255, 255),
//                        contentColor = Color(54, 1, 1),
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier
                            .width(100.dp)
                            .padding(end = 16.dp)
                    ) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", Modifier.size(28.dp))
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun HomePageePreview(){
    HomePageMain( rememberNavController())
}