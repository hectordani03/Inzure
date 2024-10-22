package io.inzure.app.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.geometry.CornerRadius
import io.inzure.app.R

@Composable
fun MainView(
    onNavigateToProfile: () -> Unit,
    onNavigateToCarInsurance: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(onNavigateToProfile)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            WelcomeMessage()
            InsuranceCategories(onNavigateToCarInsurance)
            LearnAboutInsurance()
        }

        BottomBar()
    }
}




@Composable
fun TopBar(onNavigateToProfile: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* TODO: Handle menu click */ }) {
            Image(
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = "Menú",
                modifier = Modifier.size(80.dp)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp)
        )
        IconButton(onClick = onNavigateToProfile) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


@Composable
fun BottomBar() {
    // Barra inferior con color de fondo azul y bordes redondeados
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
            .background(Color(0xFF072A4A)) // Color azul de fondo
            .padding(vertical = 12.dp), // Padding interno
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly, // Repartir los íconos equitativamente
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarIcon(R.drawable.ic_file, "Home")
            BottomBarIcon(R.drawable.ic_history, "Search")
            BottomBarIcon(R.drawable.ic_search, "Notifications")
            BottomBarIcon(R.drawable.ic_profile2, "Settings")
        }
    }
}

@Composable
fun BottomBarIcon(iconResId: Int, contentDescription: String) {
    IconButton(onClick = { /* TODO: Handle click */ }) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            modifier = Modifier.size(30.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White) // Cambiar el color a blanco
        )
    }
}

@Composable
fun WelcomeMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "¡Buenas tardes, Gilberto Ceja!",
            modifier = Modifier
                .align(Alignment.Center)
                .background(color = Color(0xFF072A4A), shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InsuranceCategories(onNavigateToCarInsurance: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Categorías de Seguros",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InsuranceCategory("Autos", R.drawable.ic_auto, onClick = onNavigateToCarInsurance)
            InsuranceCategory("Personal", R.drawable.ic_personal, onClick = { /* Navegación Personal */ })
            InsuranceCategory("Empresarial", R.drawable.ic_empresarial, onClick = { /* Navegación Empresarial */ })
        }
    }
}


@Composable
fun InsuranceCategory(name: String, iconResId: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() } // Agregar la acción de clic
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = name,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = name,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Composable
fun LearnAboutInsurance() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Aprende sobre seguros",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        InsuranceImage()
        Spacer(modifier = Modifier.height(20.dp)) // Espaciador aumentado
        InsuranceImage2()
        Spacer(modifier = Modifier.height(10.dp)) // Espaciador aumentado
        InsuranceImage3()
        Spacer(modifier = Modifier.height(10.dp)) // Espaciador aumentado
        InsuranceImage4()
    }
}

@Composable
fun InsuranceImage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            // Aplicamos la sombra usando elevation
            .drawBehind {
                // Dibujamos múltiples sombras con diferentes opacidades para crear efecto blur
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = .1f),
                        size = size.copy(height = size.height + (i * .5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
                // Sombra principal
                drawRoundRect(
                    color = Color.Gray.copy(alpha = .2f),
                    size = size.copy(height = size.height + .5.dp.toPx()),
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
            painter = painterResource(id = R.drawable.insurance_image4),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Caja con el texto y la imagen a la derecha
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto dentro de la caja
            Text(
                text = "Aprende todo sobre seguros desde cero y fácil",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Imagen a la derecha del texto
            Image(
                painter = painterResource(id = R.drawable.ic_learn),
                contentDescription = "Info Image",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun InsuranceImage2() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp)
            // Aplicamos la sombra usando elevation
            .drawBehind {
                // Dibujamos múltiples sombras con diferentes opacidades para crear efecto blur
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = .1f),
                        size = size.copy(height = size.height + (i * .5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
                // Sombra principal
                drawRoundRect(
                    color = Color.Gray.copy(alpha = .2f),
                    size = size.copy(height = size.height + .5.dp.toPx()),
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
            painter = painterResource(id = R.drawable.insurance_image2),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Caja con el texto y la imagen a la derecha
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto dentro de la caja
            Text(
                text = "Contamos con todo tipo de cobertura para tu vehiculo",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Imagen a la derecha del texto
            Image(
                painter = painterResource(id = R.drawable.ic_auto),
                contentDescription = "Info Image",
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun InsuranceImage3() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp)
            // Aplicamos la sombra usando elevation
            .drawBehind {
                // Dibujamos múltiples sombras con diferentes opacidades para crear efecto blur
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = .1f),
                        size = size.copy(height = size.height + (i * .5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
                // Sombra principal
                drawRoundRect(
                    color = Color.Gray.copy(alpha = .2f),
                    size = size.copy(height = size.height + .5.dp.toPx()),
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
            painter = painterResource(id = R.drawable.insurance_image1),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Caja con el texto y la imagen a la derecha
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto dentro de la caja
            Text(
                text = "Protege a los tuyos desde ahora",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Imagen a la derecha del texto
            Image(
                painter = painterResource(id = R.drawable.ic_personal),
                contentDescription = "Info Image",
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun InsuranceImage4() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp)
            // Aplicamos la sombra usando elevation
            .drawBehind {
                // Dibujamos múltiples sombras con diferentes opacidades para crear efecto blur
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = .1f),
                        size = size.copy(height = size.height + (i * .5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
                // Sombra principal
                drawRoundRect(
                    color = Color.Gray.copy(alpha = .2f),
                    size = size.copy(height = size.height + .5.dp.toPx()),
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
            painter = painterResource(id = R.drawable.insurance_image3),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Caja con el texto y la imagen a la derecha
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto dentro de la caja
            Text(
                text = "Protege tu empresa y evita riesgos",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Imagen a la derecha del texto
            Image(
                painter = painterResource(id = R.drawable.ic_empresarial),
                contentDescription = "Info Image",
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}


