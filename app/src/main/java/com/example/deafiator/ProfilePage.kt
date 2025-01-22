package com.example.deafiator

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(navController: NavHostController){

    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val data = UserManager.getUserData()
    //val (email, password, admin) = UserManager.getLoginData()

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
                        IconButton(onClick = { navController.navigate("") }) {
                            Image(painter = painterResource(id = R.drawable.profile), contentDescription = "")
                        }
                    }
                    Spacer(modifier = Modifier.size(0.03 * screenHeight))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(0.18 * screenHeight),
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.height(0.015 * screenHeight))
                        Text(
                            text = data["username"] as String? ?: "Username",
                            fontSize = (0.03*screenHeight).value.sp,
                            color = Color.Black
                        )
                        Text(
                            text = data["email"] as String? ?: "Email",
                            fontSize = (0.02*screenHeight).value.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.size(0.04 * screenHeight))
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.04 * screenWidth)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 0.05 * screenWidth,
                                end = 0.05 * screenWidth,
                                top = 0.04 * screenHeight
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Settings,
                                tint = Color(0xFF908bc3),
                                modifier = Modifier.size(40.dp),
                                contentDescription = "Settings"
                            )
                            Spacer(modifier = Modifier.width(0.05 * screenWidth))
                            Text(
                                modifier = Modifier.fillMaxWidth(0.9f),
                                text = "Settings",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.W500,
                                color = Color.Black
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Open",
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF818183)
                            )
                        }
                        Spacer(modifier = Modifier.height(0.04 * screenHeight))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("AddUser")},
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                tint = Color(0xFF908bc3),
                                modifier = Modifier.size(40.dp),
                                contentDescription = "Add"
                            )
                            Spacer(modifier = Modifier.width(0.05 * screenWidth))
                            Text(
                                modifier = Modifier.fillMaxWidth(0.9f),
                                text = "Add User",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.W500,
                                color = Color.Black
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Open",
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF818183)
                            )
                        }
                        Spacer(modifier = Modifier.height(0.04 * screenHeight))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Build,
                                tint = Color(0xFF908bc3),
                                modifier = Modifier.size(40.dp),
                                contentDescription = "Support"
                            )
                            Spacer(modifier = Modifier.width(0.05 * screenWidth))
                            Text(
                                modifier = Modifier.fillMaxWidth(0.9f),
                                text = "Customer Support",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.W500,

                                color = Color.Black
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Open",
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF818183)
                            )
                        }
                        Spacer(modifier = Modifier.height(0.04 * screenHeight))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    UserManager.clearUserData()
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(context, intent, null)
                                    activity?.finish()
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.ExitToApp,
                                tint = Color(0xFF908bc3),
                                modifier = Modifier.size(40.dp),
                                contentDescription = "Logout"
                            )
                            Spacer(modifier = Modifier.width(0.05 * screenWidth))
                            Text(
                                modifier = Modifier.fillMaxWidth(0.9f),
                                text = "Logout",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.W500,
                                color = Color.Black
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Open",
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF818183)
                            )
                        }
                        Spacer(modifier = Modifier.height(0.02 * screenHeight))
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
                    IconButton(onClick = { navController.navigate("HomePage") {
                        popUpTo("ProfilePage"){
                            inclusive = true
                        }
                    }}) {
                        Icon(
                            painterResource(id = R.drawable.home_outlined),
                            tint = Color(0xFF818183),
                            contentDescription = "Home")
                    }
                    IconButton(onClick = { navController.navigate("MapPage") {
                        popUpTo("ProfilePage"){
                            inclusive = true
                        }
                    }}) {
                        Icon(
                            painter = painterResource(id = R.drawable.location_outline),
                            tint = Color(0xFF818183),
                            contentDescription = "Add")
                    }
                    IconButton(onClick = { navController.navigate("NotificationPage"){
                        popUpTo("ProfilePage"){
                            inclusive = true
                        }
                    } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.notification_outline),
                            tint = Color(0xFF818183),
                            contentDescription = "Profile")
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            tint = Color(0xFF908bc3),
                            contentDescription = "Profile")
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    ProfilePage(rememberNavController())
}