package com.example.deafiator

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

data class LocateData(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapPage(navController: NavHostController) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current
    val data = UserManager.getUserData()
    var devicesList by remember { mutableStateOf<List<LocateData>>(emptyList()) }
    val latitude = data["latitude"] as Double
    val longitude = data["longitude"] as Double
    val location = LatLng(latitude, longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 17.5f)
    }
    val customMarkerBitmap = remember {
        val drawable = ContextCompat.getDrawable(context, R.drawable.profile) as BitmapDrawable
        Bitmap.createScaledBitmap(drawable.bitmap, 100, 100, false)
    }
    val database = FirebaseDatabase.getInstance().getReference("groups").child("Robogyan")

    fun fetchAllUsers() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newDevicesList = mutableListOf<LocateData>()

                for (deviceSnapshot in dataSnapshot.children) {
                    if(deviceSnapshot.key == "Notifications") continue
                    else {
                        val name = deviceSnapshot.key ?: ""
                        val latitude =
                            deviceSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                        val longitude =
                            deviceSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0

                        // Add the device to the new list
                        newDevicesList.add(LocateData(name, latitude, longitude))
                    }
                }

                // Update the devices list state after data is fetched
                devicesList = newDevicesList
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Toast.makeText(context, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fetchAllUsers()
    Scaffold(
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding) //innerPadding
                    .fillMaxSize()
                    .background(color = Color(0xFFededed)),
                contentAlignment = Alignment.TopStart
            ){
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ){
                    devicesList.forEach { device ->
                        Marker(
                            state = rememberMarkerState(position = LatLng(device.latitude, device.longitude)),
                            title = device.name,
                            snippet = "This is the location of ${device.name}.",
                            icon = BitmapDescriptorFactory.fromBitmap(customMarkerBitmap)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 0.1.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp)
                        )
                        .background(
                            color = Color.Transparent, //0xFF908bc3
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
                        IconButton(onClick = { navController.navigate("ProfilePage") }) {
                            Image(painter = painterResource(id = R.drawable.profile), contentDescription = "")
                        }
                    }
                    Spacer(modifier = Modifier.size(0.025 * screenHeight))
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
                        popUpTo("MapPage"){
                            inclusive = true
                        }
                    } }) {
                        Icon(
                            painterResource(id = R.drawable.home_outlined),
                            tint = Color(0xFF818183),
                            contentDescription = "Home")
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.location),
                            tint = Color(0xFF908bc3),
                            contentDescription = "Add")
                    }
                    IconButton(onClick = { navController.navigate("NotificationPage"){
                        popUpTo("MapPage"){
                            inclusive = true
                        }
                    } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.notification_outline),
                            tint = Color(0xFF818183),
                            contentDescription = "Profile")
                    }
                    IconButton(onClick = {
                        if (data["admin"] == true) {
                            navController.navigate("ProfilePage"){
                                popUpTo("MapPage"){
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
fun MapPagePreview() {
    MapPage(rememberNavController())
}