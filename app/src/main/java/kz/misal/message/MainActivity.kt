package kz.misal.message

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import kz.misal.message.frame.MainScreen
import kz.misal.message.frame.MainViewModel
import kz.misal.message.ui.theme.MessageTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel> ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //PreviewMessage5()
                    // в файле FinalMessage
                    MainScreen(viewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

