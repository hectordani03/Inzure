// AdminView.kt
package io.inzure.app.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.inzure.app.R

class AdminView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminMainScreen()
        }
    }
}

@Composable
fun AdminMainScreen() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "admin_screen") {
        composable("admin_screen") { AdminScreen(navController) }
        composable("users") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                context.startActivity(Intent(context, UsersView::class.java))
            }
        }
        composable("publications") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                context.startActivity(Intent(context, PostsView::class.java))
            }
        }
        composable("agents") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                context.startActivity(Intent(context, AgentsView::class.java))
            }
        }
        composable("personal_information") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                context.startActivity(Intent(context, AdminPersonalInformationView::class.java))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Panel", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF072A4A),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Opciones del Administrador",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF072A4A),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AdminOptionButton("Información Personal", R.drawable.ic_info,) {
                navController.navigate("personal_information")
            }
            AdminOptionButton("Usuarios", R.drawable.ic_profile2) {
                navController.navigate("users")
            }
            AdminOptionButton("Publicaciones", R.drawable.ic_profile2) {
                navController.navigate("publications")
            }
            AdminOptionButton("Agentes", R.drawable.ic_profile2) {
                navController.navigate("agents")
            }
        }
    }
}

@Composable
fun AdminOptionButton(text: String, icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFD8DC))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
