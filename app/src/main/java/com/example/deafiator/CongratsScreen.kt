package com.example.deafiator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deafiator.R

@Composable
fun CongratsScreen(navController: NavHostController){

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.01f,
        animationSpec = tween(durationMillis = 800), label = ""
    )


    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(color = Color(0xFF131621)),
            ) {
                startAnimation = true
                Spacer(modifier = Modifier.height(0.18 * screenHeight))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.graphicsLayer(rotationZ = rotation),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.tick),
                            contentDescription = "Check",
                            tint = Color(0xFF0677e8),
                            modifier = Modifier.size(0.7 * screenWidth)
                        )
                    }
                    Spacer(modifier = Modifier.height(0.04 * screenHeight))
                    Text(
                        text = "Congrats!",
                        color = Color.White,
                        fontSize = 48.sp,
                    )
                    Spacer(modifier = Modifier.height(0.005 * screenHeight))
                    Text(
                        text = "Account Created",
                        color = Color(0xFF868893),
                        fontSize = 16.sp,
                    )
                    Text(
                        text = "Successfully",
                        color = Color(0xFF868893),
                        fontSize = 16.sp,
                    )
                }
                FloatingActionButton(
                    modifier = Modifier
                        .padding(
                            start = 0.25 * screenWidth,
                            end = 0.25 * screenWidth,
                            top = 0.15 * screenHeight
                        )
                        .fillMaxWidth()
                        .height(0.07 * screenHeight),
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = Color(0xFF0677e8)
                ){
                    Text(text = "Let's Start", color = Color.White)
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CongratsScreenPreview(){
    CongratsScreen(rememberNavController())
}