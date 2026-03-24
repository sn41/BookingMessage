package kz.misal.message

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kz.misal.message.frame.PreviewMessage5
import kz.misal.message.frame.PreviewMessage7
import kz.misal.message.ui.theme.MessageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PreviewMessage5()

                }
            }
        }
    }
}

