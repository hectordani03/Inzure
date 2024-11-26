package io.inzure.app.ui.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import io.inzure.app.ui.components.TopBar

import io.inzure.app.R


// Importa la función BottomBar desde BottomBar.kt
import io.inzure.app.ui.components.BottomBar
import io.inzure.app.ui.components.TopBar
import io.inzure.app.viewmodel.PostsViewModel
import kotlinx.coroutines.launch

class EnterpriseInsuranceView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnterpriseInsuranceScreen(onNavigateToLogin = { /* Acción de navegación al login */ })
        }
    }
}

@Composable
fun EnterpriseInsuranceScreen(onNavigateToLogin: () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val postsViewModel: PostsViewModel = viewModel()
    val businessPosts by postsViewModel.businessPosts.collectAsState()
    LaunchedEffect(Unit) {
        postsViewModel.getBusinessPosts()
    }
    Scaffold(
        topBar = {
            TopBar(
                onMenuClick = {
                    scope.launch {
                        drawerState.open() // Abre el drawer al hacer clic en el menú
                    }
                },
                onNavigateToProfile = onNavigateToLogin // Configura la acción de navegación
            )
        },
        bottomBar = {
            val showChatView = remember { mutableStateOf(false) }
            BottomBar(
                onSwipeUp = { /* Acción al deslizar hacia arriba */ },
                onNavigateToProfile = { /* Acción de navegación */ },
                onNavigateToChat = { showChatView.value = true }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                // Título principal con ícono empresarial
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "Seguros Empresariales",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_empresarial),
                        contentDescription = "Enterprise Icon",
                        modifier = Modifier.size(28.dp)
                    )
                }

                EnterpriseInsuranceCategories() // Categorías de seguros empresariales

                Spacer(modifier = Modifier.height(22.dp))

                businessPosts.forEach { postWithUser ->

                    InsuranceCard(
                        title = postWithUser.post.titulo,
                        description = postWithUser.post.descripcion,
                        postImage = postWithUser.post.image,
                        userImage = postWithUser.profileImage
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                }

            }
        }
    }
}

@Composable
fun EnterpriseInsuranceTopBar(onNavigateToLogin: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* TODO: Manejar clic en menú */ }) {
            Image(
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = "Menú",
                modifier = Modifier.size(40.dp)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp)
        )
        IconButton(onClick = onNavigateToLogin) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Perfil",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun EnterpriseInsuranceCategories() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EnterpriseCategoryButton("Más Buscadas", R.drawable.ic_mas_buscados, iconSize = 20)
        EnterpriseCategoryButton("MetLife", R.drawable.metlife, iconSize = 30)
        EnterpriseCategoryButton("NegocioSeguro", R.drawable.axa, iconSize = 30)
        EnterpriseCategoryButton("Protección Empresarial", R.drawable.bbva, iconSize = 30)
        EnterpriseCategoryButton("Ahorro Empresarial", R.drawable.ic_hdi, iconSize = 30)
    }
}

@Composable
fun EnterpriseCategoryButton(name: String, iconResId: Int, iconSize: Int = 24) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(40.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE0E0E0))
            .padding(horizontal = 8.dp)
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = name,
            modifier = Modifier.size(iconSize.dp)
        )
        if (name.isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun EnterpriseInsuranceCard(title: String, description: String, postImage: String, userImage: String) {
    val postsViewModel: PostsViewModel = viewModel()
    val posts by postsViewModel.posts.collectAsState()
    LaunchedEffect(Unit) {
        postsViewModel.getCarPosts()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .drawBehind {
                // Efecto de sombra y blur
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = 0.1f),
                        size = size.copy(height = size.height + (i * 0.5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
                // Sombra principal
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.2f),
                    size = size.copy(height = size.height + 0.5.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    blendMode = BlendMode.Multiply,
                    topLeft = Offset(0f, 4.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        // Imagen principal
        Image(
            painter = rememberAsyncImagePainter(postImage),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )

        // Caja de información en la parte inferior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icono en la izquierda
                if (!userImage.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(userImage),
                        contentDescription = "Insurance Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                    )
                } else{
                    Image(
                        painter = painterResource(R.drawable.ic_profile_default),
                        contentDescription = "Insurance Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Título y descripción
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
