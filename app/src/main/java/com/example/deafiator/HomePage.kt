package com.example.deafiator

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.app.NotificationCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deafiator.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class Device(val name: String, val clicked: Boolean, val distance: Int)
data class Distance(val name: String, val latitude: Double, val longitude: Double)

data class UserLocation(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Radius of the Earth in kilometers

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c// Distance in kilometers
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun HomePage(navController: NavHostController) {

    val data = UserManager.getUserData()
    val context = LocalContext.current
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    val notificationChannelID = "notification_channel_id"
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var devicesList by remember { mutableStateOf<List<Device>>(emptyList()) }
    var distanceList by remember { mutableStateOf<List<Distance>>(emptyList()) }
    var searchName by remember { mutableStateOf("") }
    val database = FirebaseDatabase.getInstance().getReference("devices")
    val database2 = FirebaseDatabase.getInstance().getReference("groups").child("Robogyan")
    var lastChangedKey by remember { mutableStateOf<String?>(null) }
    var value = false
    var userLocation by remember { mutableStateOf(UserLocation()) }
    val fusedLocationClient: FusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var messageState by remember {mutableStateOf(false)}
    var hardwareState by remember {mutableStateOf(false)}
    var device by remember { mutableStateOf("") }
    

    @SuppressLint("MissingPermission")
    fun fetchUserLocation() {
        val database = FirebaseDatabase.getInstance()
        val LocationRef = database.getReference("groups").child("Robogyan").child(data["username"].toString())

        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    userLocation = UserLocation(it.latitude, it.longitude)
                    Log.d("@@Location", "Latitude: ${userLocation.latitude}, Longitude: ${userLocation.longitude}")
                    val locationUpdates = mapOf(
                        "latitude" to userLocation.latitude,
                        "longitude" to userLocation.longitude
                    )

                    UserManager.editLocation(userLocation.latitude, userLocation.longitude)

                    LocationRef.updateChildren(locationUpdates)
                        .addOnSuccessListener {
                            Log.d("@@Firebase", "Location updated successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("@@Firebase", "Failed to update location: ${exception.message}")
                        }
                    // Here you can update the location in your Firebase database or perform any other action
                } ?: Log.d("@@Location", "Location is null")
            }
            .addOnFailureListener { exception ->
                Log.e("@@Location", "Failed to fetch location: ${exception.message}")
            }
    }

    fun getDevicesData() {
        database2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newDistanceList = mutableListOf<Distance>()

                for (deviceSnapshot in dataSnapshot.children) {
                    if(deviceSnapshot.key != "Notifications" && deviceSnapshot.key != "Emergency" && deviceSnapshot.key != "emergency") {
                        val name = deviceSnapshot.key ?: "username"
                        val latitude = deviceSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                        val longitude = deviceSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0

                        // Add the device to the new list
                        newDistanceList.add(Distance(name, latitude, longitude))
                    }
                }

                // Update the devices list state after data is fetched
                distanceList = newDistanceList
                Log.d("@@@@", "Devices: $distanceList")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Toast.makeText(context, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newDevicesList = mutableListOf<Device>()

                for (deviceSnapshot in dataSnapshot.children) {
                    if(deviceSnapshot.key != "VideoCall" && deviceSnapshot.key != "Emergency") {
                        val name = deviceSnapshot.key ?: ""
                        val clicked2 =
                            deviceSnapshot.child("clicked").getValue(Boolean::class.java) ?: false
                        val distance =
                            deviceSnapshot.child("distance").getValue(Int::class.java) ?: 0

                        // Add the device to the new list
                        newDevicesList.add(Device(name, clicked2, distance))
                    }
                }

                // Update the devices list state after data is fetched
                devicesList = newDevicesList
                Log.d("@@@@", "Devices: $devicesList")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Toast.makeText(context, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        Log.d("5@@@@", "Devices: $devicesList")
        Log.d("5@@@@", "Distance: $distanceList")
    }
    getDevicesData()

    fun updateDistance(){
        val newDevicesList = mutableListOf<Device>()
        val locationData = UserManager.getLocation()
        for (device in devicesList){
            for (distance in distanceList) {
                if (device.name == distance.name) {
                    val newDistance = calculateDistance(
                        distance.latitude,
                        distance.longitude,
                        locationData.first,
                        locationData.second
                    ).toInt()
                    val newDevice = Device(device.name, device.clicked, newDistance)
                    newDevicesList.add(newDevice)
                } else if (device.name == "Hardware") {

                }
            }
        }
        val newDevice = Device("Hardware", false,0)
        newDevicesList.add((newDevice))

        devicesList = newDevicesList
        Log.d("@@Distance", "Devices: $devicesList")
    }

    updateDistance()

    fun updateLastChangedValue() {
        Log.e("@@@@ 2",lastChangedKey.toString())
        if (lastChangedKey != null) {
            // Reference to Firebase Realtime Database node
            val databaseRef = FirebaseDatabase.getInstance().getReference("devices")

            // Update the last changed key with a new value
            val newValue = false
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

    fun updateVideoValue() {
        Log.e("@@@@ 2",lastChangedKey.toString())
        if (lastChangedKey != null) {
            val databaseRef = FirebaseDatabase.getInstance().getReference("devices")

            val newValue = false
            databaseRef.child("VideoCall").child("clicked").setValue(newValue).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Toast.makeText(context, "Emergency Attended by $lastChangedKey!", Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(context, "Database update failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "No changes detected to update.", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("devices")
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                lastChangedKey = data["username"].toString()
                if(snapshot.key == data["username"].toString()){
                    val dataMap = snapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    value = dataMap?.get("clicked") as Boolean
                    Log.d("@@@@@", "Devices: $value")

                    fun showSimpleNotification() {
                        val notification = NotificationCompat.Builder(context, notificationChannelID)
                            .setContentTitle("Ankur Needs Your Help!")
                            .setContentText("Alert sent to ${snapshot.key}")
                            .setSmallIcon(R.drawable.notification_outline)
                            .setPriority(NotificationManager.IMPORTANCE_HIGH)
                            .setAutoCancel(true)
                            .build()

                        notificationManager.notify(Random.nextInt(), notification)
                    }

                    if(value) {
                        showSimpleNotification()
                        AlertDialog.Builder(context)
                            .setTitle("New Alert for you!")
                            .setMessage("Ankur wants to see you. Can you attend?")
                            .setPositiveButton("Attend") { _, _ ->
                                updateLastChangedValue()
                            }
                            .setNegativeButton("Cancel") { _, _ ->
                                Toast.makeText(context, "Emergency Alert dismissed!", Toast.LENGTH_SHORT).show()
                            }
                            .show()
                    }
                }

                else if(snapshot.key == "VideoCall"){
                    val dataMap = snapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    value = dataMap?.get("clicked") as Boolean
                    Log.d("@@@@@", "Devices: $value")

                    fun showSimpleNotification() {
                        val notification = NotificationCompat.Builder(context, notificationChannelID)
                            .setContentTitle("Ankur Needs Your Help!")
                            .setContentText("Video Call requested.")
                            .setSmallIcon(R.drawable.notification_outline)
                            .setPriority(NotificationManager.IMPORTANCE_HIGH)
                            .setAutoCancel(true)
                            .build()

                        notificationManager.notify(Random.nextInt(), notification)
                    }

                    if(value) {
                        showSimpleNotification()
                        AlertDialog.Builder(context)
                            .setTitle("New Alert for you!")
                            .setMessage("Ankur wants to make a video call. Can you attend?")
                            .setPositiveButton("Attend") { _, _ ->
                                updateVideoValue()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://66f3ca71405934f75e1c1d50--zippy-paletas-fd9cd3.netlify.app/"))
                                context.startActivity(intent)
                            }
                            .setNegativeButton("Cancel") { _, _ ->
                                Toast.makeText(context, "Emergency Alert dismissed!", Toast.LENGTH_SHORT).show()
                            }
                            .show()
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle child removed if necessary
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle child moved if necessary
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancelled
            }
        }

        databaseRef.addChildEventListener(listener)

        onDispose {
            databaseRef.removeEventListener(listener)
        }
    }

    fetchUserLocation()
    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding) //innerPadding
                    .fillMaxSize()
                    .background(color = Color(0xFFededed)),
            ) {
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
                ){
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
                            text = "Home",
                            color = Color.Black,
                            fontWeight = FontWeight.W800,
                            fontSize = 0.05 * screenWidth.value.sp
                        )
                        IconButton(onClick = { navController.navigate("ProfilePage") }) {
                            Image(painter = painterResource(id = R.drawable.profile), contentDescription = "")
                        }
                    }
                    Spacer(modifier = Modifier.size(0.03 * screenHeight))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 0.06 * screenWidth
                            )
                    ){
                        Text(text = "Welcome to Deafiator!",
                            color = Color.Black,
                            fontSize = 0.08 * screenWidth.value.sp
                        )
                        Text(text = data["username"].toString(),
                            color = Color.Black,
                            fontSize = 0.08 * screenWidth.value.sp
                        )
                    }
                    Spacer(modifier = Modifier.size(0.04 * screenHeight))
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            //top = 0.02 * screenWidth,
                            start = 0.05 * screenWidth,
                            end = 0.05 * screenWidth,
                            bottom = 0.05 * screenWidth
                        )
                ) {
                    if (searchName.isEmpty()) {
                        repeat(devicesList.size) { index ->
                            Spacer(modifier = Modifier.size(0.015 * screenHeight))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 6.dp,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                            ) {
                                val name = devicesList[index].name
                                val distance = devicesList[index].distance
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 0.035 * screenWidth,
                                            end = 0.035 * screenWidth,
                                            //top = 0.005 * screenHeight,
                                            //bottom = 0.02 * screenHeight
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Image(
                                        modifier = Modifier.size(0.18 * screenWidth),
                                        painter = painterResource(id = R.drawable.profile1),
                                        contentDescription = ""
                                    )
                                    Spacer(modifier = Modifier.size(0.057 * screenWidth))
                                    Row(
                                        //modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Column(
                                            modifier = Modifier
                                                .padding(0.02 * screenWidth),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            if(name == "Hardware"){
                                                Text(
                                                    text = "Send Alert",
                                                    color = Color.Black,
                                                    fontSize = 0.05 * screenWidth.value.sp
                                                )
                                                Text(
                                                    text = "Hardware",
                                                    color = Color(0xFF818183),
                                                    fontSize = 0.035 * screenWidth.value.sp
                                                )
                                            }
                                            else{
                                                Text(
                                                    text = name,
                                                    color = Color.Black,
                                                    fontSize = 0.06 * screenWidth.value.sp
                                                )
                                                Text(
                                                    text = "Friend",
                                                    color = Color(0xFF818183),
                                                    fontSize = 0.035 * screenWidth.value.sp
                                                )
                                                Text(
                                                    text = "$distance km away",
                                                    color = Color(0xFF818183),
                                                    fontSize = 0.027 * screenWidth.value.sp
                                                )
                                                Spacer(modifier = Modifier.size(0.02 * screenHeight))
                                            }
                                        }
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(end = 0.04 * screenWidth),
                                            horizontalAlignment = Alignment.End
                                        ){
                                            FloatingActionButton(
                                                modifier = Modifier
                                                    .size(height = 0.04 * screenHeight, width = 0.2 * screenWidth),
                                                onClick = {
                                                    device = devicesList[index].name
                                                    messageState = true
                                                },
                                                //containerColor = Color(0xFF908bc3),
                                            ) {
                                                Text(
                                                    //modifier = Modifier.padding(start = 0.06 * screenWidth, end = 0.06 * screenWidth),
                                                    text = "Send")
                                            }
                                            if (messageState){
                                                AlertDialogExample(
                                                    onDismissRequest = { messageState = false },
                                                    onConfirmation = { messageState = false },
                                                    device = device,
                                                    data = data,
                                                    context = context
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (deviceX in devicesList) {
                            if (deviceX.name.contains(searchName)) {
                                Spacer(modifier = Modifier.size(0.015 * screenHeight))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 6.dp,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                ) {
                                    val name = deviceX.name
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = 0.035 * screenWidth,
                                                end = 0.035 * screenWidth,
                                                //top = 0.005 * screenHeight,
                                                //bottom = 0.02 * screenHeight
                                            ),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Image(
                                            modifier = Modifier.size(0.18 * screenWidth),
                                            painter = painterResource(id = R.drawable.profile1),
                                            contentDescription = ""
                                        )
                                        Spacer(modifier = Modifier.size(0.057 * screenWidth))
                                        Row(
                                            //modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Column(
                                                modifier = Modifier
                                                    .padding(0.02 * screenWidth),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text(
                                                    text = name,
                                                    color = Color.Black,
                                                    fontSize = 0.06 * screenWidth.value.sp
                                                )
                                                Text(
                                                    text = "Friend",
                                                    color = Color(0xFF818183),
                                                    fontSize = 0.035 * screenWidth.value.sp
                                                )
                                                Spacer(modifier = Modifier.size(0.02 * screenHeight))
                                            }
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(end = 0.04 * screenWidth),
                                                horizontalAlignment = Alignment.End
                                            ){
                                                FloatingActionButton(
                                                    modifier = Modifier
                                                        .size(height = 0.04 * screenHeight, width = 0.2 * screenWidth),
                                                    onClick = {
                                                        messageState = true
                                                        device = deviceX.name
                                                    },
                                                    //containerColor = Color(0xFF908bc3),
                                                ) {
                                                    Text(
                                                        //modifier = Modifier.padding(start = 0.06 * screenWidth, end = 0.06 * screenWidth),
                                                        text = "Send")
                                                }
                                                if (messageState) {
                                                    AlertDialogExample(
                                                        onDismissRequest = { messageState = false },
                                                        onConfirmation = { messageState = false },
                                                        device = device,
                                                        data = data,
                                                        context = context
                                                    )
                                                }
                                            }
                                        }
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
                    IconButton(onClick = { }) {
                        Icon(
                            painterResource(id = R.drawable.home),
                            tint = Color(0xFF908bc3),
                            contentDescription = "Home")
                    }
                    IconButton(onClick = { navController.navigate("MapPage") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.location_outline),
                            tint = Color(0xFF818183),
                            contentDescription = "Add")
                    }
                    IconButton(onClick = { navController.navigate("NotificationPage") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.notification_outline),
                            tint = Color(0xFF818183),
                            contentDescription = "Profile")
                    }
                    IconButton(onClick = {
                        if (data["admin"] == true) {
                            navController.navigate("ProfilePage")
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

fun handleIconClick(device: String, data: Map<String, Any?>, context: Context) {
    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formattedTime = currentTime.format(formatter)

    val database = FirebaseDatabase.getInstance().getReference("groups").child("Robogyan").child("Notifications")

    if(device == "Hardware"){
        val hardware = FirebaseDatabase.getInstance().getReference("devices").child("Hardware")

        hardware.child("clicked").setValue(true)
        Toast.makeText(context, "Vibration Alert sent to ${device}!", Toast.LENGTH_SHORT).show()

    }
    Log.d("Hii@@", "Device clicked: ${device}")
}

@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    device: String,
    data: Map<String, Any?>,
    context: Context,
) {

    var text by remember { mutableStateOf("") }
    fun handleIconClick(device: String, data: Map<String, Any?>, context: Context, message:String) {
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedTime = currentTime.format(formatter)

        val database = FirebaseDatabase.getInstance().getReference("groups").child("Robogyan").child("Notifications")

        if(device == "Hardware"){
            val hardware = FirebaseDatabase.getInstance().getReference("devices").child("Hardware")

            hardware.child("clicked").setValue(true)
            Toast.makeText(context, "Vibration Alert sent to ${device}!", Toast.LENGTH_SHORT).show()

        }else {
            val newGroup = mapOf(
                "sender" to data["username"],
                "reciever" to device,
                "message" to message,
                "time" to formattedTime.toString()
            )
            database.push().setValue(newGroup)
            Toast.makeText(context, "Alert sent to ${device}!", Toast.LENGTH_SHORT).show()
        }
        Log.d("Hii@@", "Device clicked: ${device}")
    }

    AlertDialog(
        icon = {
            Icon(Icons.Filled.MailOutline, contentDescription = "Example Icon")
        },
        title = {
            Text(text = "Type your message")
        },
        text = {TextField(value = text, onValueChange = { text = it })},
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            Button(
                onClick = {
                    handleIconClick(device, data, context, text)
                    onConfirmation()
                }){
                Text(
                    text = "Send",
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    text = "Dismiss",
                    color = Color.Black
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    HomePage(rememberNavController())
}