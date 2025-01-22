package com.example.deafiator

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class User(
    val name: String,
    val email: String,
    val hasHardware: Boolean,
    val isAdmin: Boolean,
    val distance: Double
)

data class Devices(val name: String, val clicked: Boolean, val distance: Int)
data class Distances(val name: String, val latitude: Double, val longitude: Double)

@SuppressLint("DefaultLocale")
fun calculateDistances(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Radius of the Earth in kilometers

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = earthRadius * c // Distance in kilometers

    return String.format("%.2f", distance).toDouble()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewMembersPage(navController: NavHostController) {

    val data = UserManager.getUserData()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
//    var groupname by remember{ mutableStateOf("") }
//    var username by remember{ mutableStateOf("") }
//    var email by remember{ mutableStateOf("") }
//    var password by remember{ mutableStateOf("") }
//    var hasHardware by remember { mutableStateOf(false) }
    var searchName by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf<List<User>>(emptyList()) }


    fun getData(){
        val locationData = UserManager.getLocation()
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("groups").child("Robogyan")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newUserList = mutableListOf<User>()

                for (deviceSnapshot in dataSnapshot.children) {
                    if(deviceSnapshot.key != "Notifications" && deviceSnapshot.key != "Emergency" && deviceSnapshot.key != "emergency" && deviceSnapshot.key != data["username"]){
                        val name = deviceSnapshot.key ?: "username"
                        val latitude = deviceSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                        val longitude = deviceSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0
                        val email = deviceSnapshot.child("email").getValue(String::class.java) ?: "NA"
                        val hasHardware = deviceSnapshot.child("hardware").getValue(Boolean::class.java) ?: false
                        val isAdmin = deviceSnapshot.child("admin").getValue(Boolean::class.java) ?: false
                        val distance = calculateDistances(latitude, longitude, locationData.first, locationData.second)

                        // Add the device to the new list
                        newUserList.add(User(name, email, hasHardware, isAdmin, distance))
                    }
                }

                // Update the devices list state after data is fetched
                userList = newUserList
                Log.d("55@@@@", "User Data: $userList")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Toast.makeText(context, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    getData()

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                        IconButton(onClick = {
                            if (data["admin"] == true) {
                                navController.navigate("ProfilePage")
                            } else {
                                navController.navigate("UserProfile")
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack,tint = Color.Black, contentDescription = "")
                        }
                        Text(
                            text = "View Group",
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
                        Text(text = "View Your Group",
                                    color = Color.Black,
                            fontSize = 0.075 * screenWidth.value.sp
                        )
                    }
                    Spacer(modifier = Modifier.size(0.02 * screenHeight))
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
                    Spacer(modifier = Modifier.size(0.03 * screenHeight))
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 0.04 * screenHeight,
                            end = 0.04 * screenHeight,
                            top = 0.06 * screenHeight,
                            bottom = 0.04 * screenHeight
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (searchName.isEmpty()) {
                        for (user in userList) {
                            Spacer(modifier = Modifier.size(0.02 * screenHeight))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
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
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(0.02 * screenWidth),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text(
                                                    text = user.name,
                                                    color = Color.Black,
                                                    fontSize = 0.06 * screenWidth.value.sp
                                                )
                                                Spacer(modifier = Modifier.size(0.006 * screenHeight))
                                                Text(
                                                    text = "Email: ${user.email}",
                                                    color = Color(0xFF818183),
                                                    fontSize = 0.035 * screenWidth.value.sp
                                                )
                                                Text(
                                                    text = "With device: ${user.hasHardware}",
                                                    color = Color(0xFF818183),
                                                    fontSize = 0.035 * screenWidth.value.sp
                                                )
                                                Text(
                                                    text = "Distance: ${user.distance} km away.",
                                                    color = Color(0xFF818183),
                                                    fontSize = 0.035 * screenWidth.value.sp
                                                )
                                                Text(
                                                    text = "Admin Powers: ${user.isAdmin}",
                                                    color = Color(0xFF818183),
                                                    fontSize = 0.035 * screenWidth.value.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (user in userList) {
                            if (user.name.contains(searchName, ignoreCase = true)) {
                                Spacer(modifier = Modifier.size(0.02 * screenHeight))
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
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
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(0.02 * screenWidth),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.Start
                                                ) {
                                                    Text(
                                                        text = user.name,
                                                        color = Color.Black,
                                                        fontSize = 0.06 * screenWidth.value.sp
                                                    )
                                                    Spacer(modifier = Modifier.size(0.006 * screenHeight))
                                                    Text(
                                                        text = "Email: ${user.email}",
                                                        color = Color(0xFF818183),
                                                        fontSize = 0.035 * screenWidth.value.sp
                                                    )
                                                    Text(
                                                        text = "With device: ${user.hasHardware}",
                                                        color = Color(0xFF818183),
                                                        fontSize = 0.035 * screenWidth.value.sp
                                                    )
                                                    Text(
                                                        text = "Distance: ${user.distance} km away.",
                                                        color = Color(0xFF818183),
                                                        fontSize = 0.035 * screenWidth.value.sp
                                                    )
                                                    Text(
                                                        text = "Admin Powers: ${user.isAdmin}",
                                                        color = Color(0xFF818183),
                                                        fontSize = 0.035 * screenWidth.value.sp
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
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ViewMembersPagePreview() {
    ViewMembersPage(rememberNavController())
}