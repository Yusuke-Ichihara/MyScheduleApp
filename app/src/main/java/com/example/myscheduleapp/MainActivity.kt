package com.example.myscheduleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myscheduleapp.ui.theme.MyScheduleAppTheme

class MainActivity : ComponentActivity() { //MainActivityクラスはComponentActivityというクラスを継承している
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyScheduleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android_MyScheduleApp",
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Hello $name!", color = Color.Red)
        Text(text = "Hello $name!", color = Color.Green)
        Text(text = "MyScheduleApp", color = Color.Blue)
        Text(text = "MyScheduleApp02", color = Color.Yellow)
    }
}

@Composable
fun GreetingHeader(str : String) {
    Text(
        text = "Hello $str!!!",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyScheduleAppTheme {
        Greeting("Android_MyScheduleApp_GreetingPreview")
    }
}