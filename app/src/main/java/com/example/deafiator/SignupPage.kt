package com.example.deafiator

import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.deafiator.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupPageMain(navController: NavHostController){

    val activity = LocalContext.current as? ComponentActivity
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var groupname by remember{ mutableStateOf("")}
    var username by remember{ mutableStateOf("")}
    var email by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }
    var hasHardware by remember { mutableStateOf(false) }
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("groups")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { activity?.onBackPressed() }) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(color = Color(0xFF131621)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 0.04 * screenHeight,
                            end = 0.04 * screenHeight,
                            top = 0.03 * screenHeight,
                            bottom = 0.05 * screenHeight
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Create your",
                        fontSize = 0.05 * screenHeight.value.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Account",
                        fontSize = 0.05 * screenHeight.value.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                    )
                    Spacer(modifier = Modifier.height(0.03 * screenHeight))
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
                            onValueChange = {groupname = it},
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF292c35),
                                focusedBorderColor = Color(0xFF0677e8),
                                unfocusedBorderColor = Color(0xFF0677e8),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.AccountBox,
                                    tint = Color(0xFF0677e8),
                                    contentDescription = ""
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Group Name",
                                    color = Color(0xFF0677e8)
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
                            onValueChange = {username = it},
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF292c35),
                                focusedBorderColor = Color(0xFF0677e8),
                                unfocusedBorderColor = Color(0xFF0677e8),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    tint = Color(0xFF0677e8),
                                    contentDescription = ""
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Username",
                                    color = Color(0xFF0677e8)
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
                            onValueChange = {email = it},
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF292c35),
                                focusedBorderColor = Color(0xFF0677e8),
                                unfocusedBorderColor = Color(0xFF0677e8),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.MailOutline,
                                    tint = Color(0xFF0677e8),
                                    contentDescription = ""
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Email Id",
                                    color = Color(0xFF0677e8)
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
                            onValueChange = {password = it},
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF292c35),
                                focusedBorderColor = Color(0xFF0677e8),
                                unfocusedBorderColor = Color(0xFF0677e8),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    tint = Color(0xFF0677e8),
                                    contentDescription = ""
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Password",
                                    color = Color(0xFF0677e8)
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        Row(
                            modifier = Modifier.fillMaxWidth().height(0.08 * screenHeight)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF0677e8),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(text = "Using Hardware", color = Color.White, fontSize = 20.sp)
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

                                val newGroup = mapOf(
                                    username to mapOf(
                                        "admin" to true,
                                        "hardware" to hasHardware,
                                        "emergency" to "false",
                                        "username" to username,
                                        "email" to email,
                                        "password" to password,
                                        "latitude" to 28.678302,
                                        "longitude" to 77.261116
                                    )
                                )
                                database.child(groupname).setValue(newGroup)

                                navController.navigate("CongratsPage"){
                                popUpTo("SignupPage") { inclusive = true }
                            } },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color(0xFF0677e8)
                        ){
                            Text(text = "Register", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        Row {
                            Text(text = "Already have an account?", color = Color(0xFF868893))
                            Spacer(modifier = Modifier.width(0.01 * screenWidth))
                            Text(text = "Sign In", color = Color(0xFF0677e8))
                        }
                    }
                }
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 0.04 * screenHeight,
                            end = 0.04 * screenHeight,
                            top = 0.05 * screenHeight,
                            bottom = 0.04 * screenHeight
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "Continue With Accounts",
                        color = Color(0xFF868893)
                    )
                    Spacer(modifier = Modifier.height(0.02 * screenHeight))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        FloatingActionButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(0.07 * screenHeight),
                            onClick = {  },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color(0xFF0f2e53)
                        ){
                            Icon(
                                modifier = Modifier.padding(0.01 * screenHeight),
                                painter = painterResource(id = R.drawable.google2),
                                tint = Color(0xFF0677e8),
                                contentDescription = "")
                        }
                        Spacer(modifier = Modifier.width(0.03 * screenWidth))
                        FloatingActionButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(0.07 * screenHeight),
                            onClick = {  },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color(0xFF0f2e53)
                        ){
                            Icon(
                                modifier = Modifier.padding(0.008 * screenHeight),
                                painter = painterResource(id = R.drawable.facebook2),
                                tint = Color(0xFF0677e8),
                                contentDescription = "")
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun UserPagePreview(){
    SignupPageMain( rememberNavController())
}