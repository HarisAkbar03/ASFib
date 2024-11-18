package edu.farmingdale.threadsexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.farmingdale.threadsexample.ui.theme.ThreadsExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThreadsExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FibonacciDemoNoBgThrdWrapper(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun FibonacciDemoNoBgThrdWrapper(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        FibonacciDemoWithCoroutine()
    }
}

// ToDo 1: Call `FibonacciDemoNoBgThrd` that calculates the Fibonacci number of a given number.
// ToDo 2: Create a composable function called `FibonacciDemoWithCoroutine` that calculates the
//  Fibonacci number of a given number using a coroutine.
// ToDo 3: Start the application using the CountDownActivity
// ToDo 4: Make the Text of the timer larger
// ToDo 5: Show a visual indicator of the timer going down to 0
// ToDo 6: Add a button to rest the timer
// ToDo 7: Play a sound when the timer reaches 0
// ToDo 8: During the last 10 seconds, make the text red and bold
