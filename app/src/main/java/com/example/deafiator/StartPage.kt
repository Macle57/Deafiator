package com.example.deafiator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun MainPage(navController: NavHostController){

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        content = { innerPadding ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White),
            ){
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 0.1 * screenHeight, bottom = 0.1 * screenHeight),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Image(
                        modifier = Modifier
                            .size(width = 350.dp, height = 250.dp),
                        painter = painterResource(id = R.drawable.loginpage),
                        contentDescription = "Login",
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.05 * screenHeight, bottom = 0.08 * screenHeight),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Image(
                                modifier = Modifier
                                    .size(width = 70.dp, height = 70.dp),
                                painter = painterResource(id = R.drawable.icon),
                                contentDescription = "")
                            Text(
                                text = "Deafiator",
                                color = Color.Black,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                                fontSize = 40.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(0.01f * screenHeight))
                        Text(
                            text = "One solution for all your calling problems!",
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        FloatingActionButton(
                            modifier = Modifier.width(0.5f * screenWidth),
                            onClick = { navController.navigate("SignupPage") }
                        ){
                            Text(
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                                text = "Get Started",
                                fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(0.015f * screenHeight))
                        FloatingActionButton(
                            modifier = Modifier.width(0.5f * screenWidth),
                            onClick = { navController.navigate("LoginPage") }
                        ){
                            Text(
                                text = "Login",
                                fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainPagePreview() {
    MainPage(navController = rememberNavController())
}
