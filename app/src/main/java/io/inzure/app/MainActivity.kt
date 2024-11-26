// MainActivity.kt
package io.inzure.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.inzure.app.ui.views.CarInsuranceView
import io.inzure.app.ui.views.ProfileView
import io.inzure.app.ui.views.MainView
import io.inzure.app.ui.views.UsersView
import io.inzure.app.ui.views.AdminView // Importa AdminView
import io.inzure.app.ui.views.LoginView // Importa LoginView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0),
            navigationBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0)
        )
        setContent {
            InzureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MainView(
                        onNavigateToProfile = {
                            val intent = Intent(this@MainActivity, ProfileView::class.java)
                            startActivity(intent)
                        },
                        onNavigateToCarInsurance = {
                            val intent = Intent(this@MainActivity, CarInsuranceView::class.java)
                            startActivity(intent)
                        },
                        onNavigateToUsers = {
                            val intent = Intent(this@MainActivity, UsersView::class.java)
                            startActivity(intent)
                        },
                        onNavigateToAdmin = {
                            val intent = Intent(this@MainActivity, AdminView::class.java)
                            startActivity(intent)
                        },
                        onNavigateToChat = {
                            // Implementa la navegación al chat si es necesario
                        },
                        onNavigateToLogin = {
                            // Cerrar sesión y redireccionar al LoginView
                            val intent = Intent(this@MainActivity, LoginView::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
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