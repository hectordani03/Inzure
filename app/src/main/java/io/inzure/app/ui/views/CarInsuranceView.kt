package io.inzure.app.ui.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.inzure.app.R

// Importa la función BottomBar desde BottomBar.kt
import io.inzure.app.ui.views.BottomBar

class CarInsuranceView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarInsuranceScreen(onNavigateToLogin = { /* Acción de navegación al login */ })
        }
    }
}

@Composable
fun CarInsuranceScreen(onNavigateToLogin: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarCar(onNavigateToLogin)

        Spacer(modifier = Modifier.height(22.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            // Título principal con ícono de carro
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Seguros de autos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_auto),
                    contentDescription = "Car Icon",
                    modifier = Modifier.size(28.dp)
                )
            }

            InsuranceCategoriesCar() // Categorías de seguros

            Spacer(modifier = Modifier.height(22.dp))

            // Tarjetas de seguro
            InsuranceCard(
                title = "Qualitas Seguros",
                description = "Explora por los diversos seguros que tenemos",
                imageResId = R.drawable.insurance_image2 // Imagen de ejemplo
            )
            Spacer(modifier = Modifier.height(16.dp))
            InsuranceCard(
                title = "GNP Seguros",
                description = "Seguros completos para tu vehículo",
                imageResId = R.drawable.insurance_image1 // Imagen de ejemplo
            )
        }

        // Reemplaza BottomBarCar() por BottomBar()
        BottomBar(
            onSwipeUp = { /* Acción al deslizar hacia arriba */ },
            onNavigateToUsers = { /* Acción de navegación */ }
        )
    }
}

@Composable
fun TopBarCar(onNavigateToLogin: () -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* TODO: Handle menu click */ }) {
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
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun InsuranceCategoriesCar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryButton("Más buscadas", R.drawable.ic_mas_buscados, iconSize = 20)
        CategoryButton("", R.drawable.ic_qualitas, iconSize = 30)
        CategoryButton("", R.drawable.ic_gnp, iconSize = 30)
        CategoryButton("", R.drawable.ic_inbursa, iconSize = 30)
        CategoryButton("", R.drawable.ic_hdi, iconSize = 30)
        CategoryButton("", R.drawable.ic_inbursa, iconSize = 30)
    }
}

@Composable
fun CategoryButton(name: String, iconResId: Int, iconSize: Int = 24) {
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
fun InsuranceCard(title: String, description: String, imageResId: Int) {
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
            painter = painterResource(id = imageResId),
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
                Image(
                    painter = painterResource(id = R.drawable.ic_qualitas), // Ajusta el ícono según corresponda
                    contentDescription = "Insurance Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                )

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

                // Ícono de favorito
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down), // Ajusta el ícono según corresponda
                    contentDescription = "Favorite",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
