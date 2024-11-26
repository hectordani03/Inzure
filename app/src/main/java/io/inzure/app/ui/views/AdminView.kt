package io.inzure.app.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.google.firebase.auth.FirebaseAuth
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

        composable("personal_information") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                context.startActivity(Intent(context, AdminPersonalInformationView::class.java))
            }
        }
        composable("login") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val intent = Intent(context, LoginView::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    val context = LocalContext.current
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

            AdminOptionButton("Información Personal", R.drawable.ic_info) {
                navController.navigate("personal_information")
            }
            AdminOptionButton("Usuarios", R.drawable.ic_profile2) {
                navController.navigate("users")
            }
            AdminOptionButton("Publicaciones", R.drawable.ic_profile2) {
                navController.navigate("publications")
            }
            AdminOptionButton("Cerrar Sesión", R.drawable.ic_logout) {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login")
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

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_logout),
            contentDescription = "Cerrar sesión",
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Cerrar Sesión",
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}
