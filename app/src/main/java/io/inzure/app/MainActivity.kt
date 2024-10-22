package io.inzure.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import io.inzure.app.ui.views.CarInsuranceView
import io.inzure.app.ui.views.ProfileView
import io.inzure.app.ui.views.MainView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InzureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(
                        onNavigateToProfile = {
                            val intent = Intent(this@MainActivity, ProfileView::class.java)
                            startActivity(intent)
                        },
                        onNavigateToCarInsurance = {
                            val intent = Intent(this@MainActivity, CarInsuranceView::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InzureTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}
