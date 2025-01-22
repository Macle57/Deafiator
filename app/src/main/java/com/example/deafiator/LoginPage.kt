package com.example.deafiator

import android.content.Context
import android.content.Intent
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPageMain(navController: NavHostController){

    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var username by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }

    fun loginUser(inputUsername: String, inputPassword: String, context: Context) {
        UserManager.initialize(context)

        FirebaseFun().authenticateUser(inputUsername, inputPassword, {
            Log.e("@@@@", "User authenticated")
            FirebaseFun().fetchUserData(inputUsername, { userData ->
                FirebaseFun().storeUserData(userData)
                val intent = Intent(context, HomeActivity::class.java)
                startActivity(context, intent, null)
                activity?.finish()
            }, { error ->
                Log.d("Firestore", "Error fetching user data: $error")
            })
        }, { errorMessage ->
            Log.d("Firestore", "Error fetching user data: $errorMessage")
        })
    }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(color = Color.White),
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
                            color = Color.Transparent, //0xFF908bc3
                            shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp)
                        ),
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 0.04 * screenHeight, top = 0.04 * screenHeight),
                    ){
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Login your",
                            fontSize = 0.05 * screenHeight.value.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Account",
                            fontSize = 0.05 * screenHeight.value.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Start,
                            maxLines = 2,
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
                            top = 0.03 * screenHeight,
                            bottom = 0.06 * screenHeight
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                            value = username,
                            onValueChange = {username = it},
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF908bc3),
                                focusedBorderColor = Color(0xFF908bc3),
                                unfocusedBorderColor = Color(0xFF908bc3),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
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
                            value = password,
                            onValueChange = {password = it},
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFF908bc3),
                                focusedBorderColor = Color(0xFF908bc3),
                                unfocusedBorderColor = Color(0xFF908bc3),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
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
                        Text(
                            text = "Forgot Password?",
                            color = Color(0xFF868893),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(0.03 * screenHeight))
                        FloatingActionButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.07 * screenHeight),
                            onClick = {
                                loginUser(username, password, context)
                            },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color(0xFF0677e8)
                        ){
                            Text(text = "Login", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(0.01 * screenHeight))
                        Row {
                            Text(text = "Create new account?", color = Color(0xFF868893))
                            Spacer(modifier = Modifier.width(0.01 * screenWidth))
                            Text(text = "Sign Up", color = Color(0xFF908bc3), fontWeight = FontWeight.Bold)
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
                            top = 0.06 * screenHeight,
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
                            onClick = { },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color(0xFF908bc3)
                        ){
                            Image(
                                modifier = Modifier.padding(0.01 * screenHeight),
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "")
                        }
                        Spacer(modifier = Modifier.width(0.03 * screenWidth))
                        FloatingActionButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(0.07 * screenHeight),
                            onClick = { /*TODO*/ },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color(0xFF908bc3)
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
fun AdminPagePreview(){
    LoginPageMain( rememberNavController())
}