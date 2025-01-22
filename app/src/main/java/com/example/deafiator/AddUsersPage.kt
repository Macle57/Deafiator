package com.example.deafiator

import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deafiator.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUsersPage(navController: NavHostController) {

    val activity = LocalContext.current as? ComponentActivity
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var groupname by remember{ mutableStateOf("") }
    var username by remember{ mutableStateOf("") }
    var email by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }
    var hasHardware by remember { mutableStateOf(false) }
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("groups")
    val database2: DatabaseReference = FirebaseDatabase.getInstance().getReference("devices")

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
                        IconButton(onClick = { navController.navigate("") }) {
                            Icon(Icons.Default.Menu,tint = Color.Black, contentDescription = "")
                        }
                        Text(
                            text = "Add Users",
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
                        Text(text = "Add New User to Group",
                            color = Color.Black,
                            fontSize = 0.075 * screenWidth.value.sp
                        )
                    }
                    Spacer(modifier = Modifier.size(0.04 * screenHeight))
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 0.04 * screenHeight,
                            end = 0.04 * screenHeight,
                            top = 0.06 * screenHeight,
                            bottom = 0.06 * screenHeight
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(0.02 * screenHeight))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.08 * screenHeight),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            value = groupname,
                            onValueChange = { groupname = it },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF908bc3),
                                focusedBorderColor = Color(0xFF908bc3),
                                unfocusedBorderColor = Color(0xFF908bc3),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.AccountBox,
                                    tint = Color.White,
                                    contentDescription = ""
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Group Name",
                                    color = Color.White
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.08 * screenHeight),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            value = username,
                            onValueChange = { username = it },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF908bc3),
                                focusedBorderColor = Color(0xFF908bc3),
                                unfocusedBorderColor = Color(0xFF908bc3),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    tint = Color.White,
                                    contentDescription = ""
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Username",
                                    color = Color.White
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.08 * screenHeight),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            value = email,
                            onValueChange = { email = it },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF908bc3),
                                focusedBorderColor = Color(0xFF908bc3),
                                unfocusedBorderColor = Color(0xFF908bc3),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.MailOutline,
                                    tint = Color.White,
                                    contentDescription = ""
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Email Id",
                                    color = Color.White
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.08 * screenHeight),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            value = password,
                            onValueChange = { password = it },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF908bc3),
                                focusedBorderColor = Color(0xFF908bc3),
                                unfocusedBorderColor = Color(0xFF908bc3),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    tint = Color.White,
                                    contentDescription = ""
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Password",
                                    color = Color.White
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.08 * screenHeight)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF908bc3),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Using Hardware", color = Color.Black, fontSize = 20.sp)
                            Checkbox(
                                checked = hasHardware,
                                onCheckedChange = { checked ->
                                    hasHardware = checked
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Green, // Green when checked
                                    uncheckedColor = Color.Gray, // Gray when unchecked
                                    checkmarkColor = Color.White // Color of the checkmark inside the checkbox
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(0.03 * screenHeight))
                        FloatingActionButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.07 * screenHeight),
                            onClick = {

                                val newUser = mapOf(
                                    "admin" to false,
                                    "hardware" to hasHardware,
                                    "emergency" to false,
                                    "username" to username,
                                    "email" to email,
                                    "password" to password,
                                    "latitude" to 28.678302,
                                    "longitude" to 77.261116
                                )

                                val newUser2 = mapOf(
                                    "clicked" to false,
                                    "groupname" to groupname
                                )

                                database.child(groupname).child(username).setValue(newUser)
                                database2.child(username).setValue(newUser2)
                                navController.navigate("HomePage") {
                                    popUpTo("SignupPage") { inclusive = true }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color(0xFF0677e8)
                        ) {
                            Text(text = "Add User", color = Color.White)
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddUsersPagePreview() {
    AddUsersPage(rememberNavController())
}