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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.ExperimentalMaterial3Api




// Clase de datos para la lista de seguros
data class InsuranceData(
    val imageRes: Int,
    val companyLogo: Int,
    val companyName: String,
    val description: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    onNavigateToProfile: () -> Unit,
    onNavigateToCarInsurance: () -> Unit,
    onNavigateToUsers: () -> Unit // Añadido nuevo parámetro
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var isDrawerOpen by remember { mutableStateOf(false) }

    // Estado del BottomSheetScaffold
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    // Animación de color para el sombreado al abrir el Drawer
    val scrimColor by animateColorAsState(
        targetValue = if (isDrawerOpen) Color.Black.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(durationMillis = 500)
    )

    // Lista de seguros de ejemplo
    val insuranceList = listOf(
        InsuranceData(
            imageRes = R.drawable.aseguradora1,
            companyLogo = R.drawable.ic_qualitas,
            companyName = "Qualitas Seguros",
            description = "Explora por los diversos seguros que tenemos"
        ),

        InsuranceData(
            imageRes = R.drawable.aseguradora3,
            companyLogo = R.drawable.ic_aseguradora3,
            companyName = "MetLife",
            description = "Descubriendo la vida juntos"
        ),

        InsuranceData(
            imageRes = R.drawable.aseguradora2,
            companyLogo = R.drawable.ic_aseguradora2,
            companyName = "GNP Seguros",
            description = "Cobertura completa para tu auto"
        ),

        InsuranceData(
            imageRes = R.drawable.aseguradora4,
            companyLogo = R.drawable.ic_aseguradora4,
            companyName = "HDI Seguros",
            description = "Hacer fáciles tus momentos dificiles"
        ),
        InsuranceData(
            imageRes = R.drawable.aseguradora5,
            companyLogo = R.drawable.ic_aseguradora5,
            companyName = "Zurich Seguros",
            description = "Cobertura total para tu hogar"
        )
    )

    // Implementación del BottomSheetScaffold para manejar el comportamiento de la Bottom Bar
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp, // Comienza colapsado
        containerColor = Color(0xFF072A4A), // Fondo azul del Bottom Sheet
        sheetContent = {
            BottomSheetContent(insuranceList)
        }
    )
    {
        // Uso de ModalNavigationDrawer para el menú lateral
        ModalNavigationDrawer(
            drawerState = drawerState,
            scrimColor = scrimColor,
            drawerContent = {
                // Contenido del menú lateral
                Box(
                    modifier = Modifier
                        .width(screenWidth * 0.75f)
                        .fillMaxHeight()
                        .background(Color(0xFF072A4A))
                ) {
                    // Diseño del menú lateral
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))

                        // Sección de perfil
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile3),
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Información del usuario
                        Text(
                            text = "Jose Joshua",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        Text(
                            text = "josejoshua01@gmail.com",
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Opciones del menú lateral
                        MenuOption(iconRes = R.drawable.ic_profile2, text = "Perfil", spacerHeight = 20.dp)
                        MenuOption(iconRes = R.drawable.ic_file, text = "Mis Pólizas", spacerHeight = 20.dp)
                        MenuOption(iconRes = R.drawable.ic_search, text = "Buscador", spacerHeight = 20.dp)
                        MenuOption(iconRes = R.drawable.ic_history, text = "Historial", spacerHeight = 20.dp)
                        MenuOption(iconRes = R.drawable.ic_chat, text = "Chat", spacerHeight = 20.dp)
                        MenuOption(iconRes = R.drawable.ic_learn, text = "Educativo", spacerHeight = 20.dp)

                        Spacer(modifier = Modifier.weight(1f))

                        // Botón de cerrar sesión
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logout),
                                contentDescription = "Cerrar sesión",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cerrar Sesión",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        ) {
            // Uso de Scaffold para mantener la TopBar y la BottomBar fijas
            Scaffold(
                topBar = {
                    TopBar(
                        onMenuClick = {
                            scope.launch {
                                isDrawerOpen = true
                                drawerState.open() // Abrir el Drawer
                            }
                        },
                        onNavigateToProfile = onNavigateToProfile
                    )
                },
                bottomBar = {
                    BottomBar(
                        onSwipeUp = {
                            scope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand() // Expandir el Bottom Sheet
                            }
                        },
                        onNavigateToUsers = onNavigateToUsers // Pasa el parámetro aquí
                    )
                }
            ){ innerPadding ->
                // Contenido principal de la pantalla
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                ) {
                    WelcomeMessage()
                    InsuranceCategories(onNavigateToCarInsurance)
                    LearnAboutInsurance()
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
fun TopBar(
    onMenuClick: () -> Unit, // Agregar el parámetro onMenuClick
    onNavigateToProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Añadir fondo blanco a la TopBar
            .statusBarsPadding() // Ajuste para evitar superposición con los íconos del sistema
            .padding(16.dp), // Padding interno
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón del menú de hamburguesa
        IconButton(onClick = onMenuClick) { // Usar el parámetro onMenuClick
            Image(
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = "Menú",
                modifier = Modifier.size(40.dp)
            )
        }

        // Logo de la aplicación
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp)
        )

        // Botón de perfil
        IconButton(onClick = onNavigateToProfile) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile3),
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


// Contenido del Bottom Sheet con lista de seguros
@Composable
fun BottomSheetContent(insuranceList: List<InsuranceData>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f) // Cubre 3/4 de la pantalla
            .background(Color(0xFF072A4A)) // Fondo azul
    ) {
        // Barra superior del Bottom Sheet
        Spacer(modifier = Modifier.height(16.dp)) // Espacio superior para el texto "Buscar"
        Text(
            text = "Buscar",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp)) // Más espacio debajo del texto "Buscar"

        // Campo de búsqueda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de lupa
            Icon(
                painter = painterResource(id = R.drawable.ic_search), // Reemplaza con tu ícono de lupa
                contentDescription = "Lupa",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Texto de búsqueda (hint) en negrita
            SearchField()
        }

        // Línea debajo del campo de búsqueda, más separada
        Spacer(modifier = Modifier.height(8.dp)) // Espacio adicional antes de la línea
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp)
                .background(Color.White.copy(alpha = 0.6f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de seguros desplazable con imagen en cada elemento
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
        ) {
            items(insuranceList) { insurance ->
                InsuranceCardWithImage(
                    imageRes = insurance.imageRes,
                    companyLogo = insurance.companyLogo,
                    companyName = insurance.companyName,
                    description = insurance.description
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField() {
    var text by remember { mutableStateOf("") } // Estado para el texto ingresado

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = {
            Text(
                text = "Encuentra tu seguro", // Placeholder
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 20.sp, // Tamaño de texto más grande
                fontWeight = FontWeight.Bold // Texto en bold
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp), // Ajustar según el diseño
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 20.sp, // Tamaño de texto más grande
            fontWeight = FontWeight.Bold // Texto en bold
        ),
        singleLine = true, // Limitar a una sola línea de texto
        shape = RoundedCornerShape(8.dp), // Esquinas edondeadas
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = Color.Black
        )
    )
}


// Función para mostrar una tarjeta de seguro con imagen
@Composable
fun InsuranceCardWithImage(imageRes: Int, companyLogo: Int, companyName: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = companyLogo),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = companyName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Íconos del corazón y de la flecha en la misma fila
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_personal), // Ícono de corazón
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(bottom = 4.dp) // Añade padding hacia abajo para mayor separación
                )

                Spacer(modifier = Modifier.height(8.dp)) // Aumenta la altura del espaciado

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow), // Ícono de flecha hacia abajo
                    contentDescription = "Desplegar más",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 4.dp) // Añade padding hacia arriba para mayor separación
                )
            }

        }
    }
}


@Composable
fun BottomBar(
    onSwipeUp: () -> Unit,
    onNavigateToUsers: () -> Unit // Añadir el nuevo parámetro aquí
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
            .background(Color(0xFF072A4A))
            .navigationBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSwipeUp) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_file),
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = onSwipeUp) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = onSwipeUp) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = onNavigateToUsers) { // Ahora sí se reconoce la referencia
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile2),
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}


@Composable
fun MenuOption(iconRes: Int, text: String, spacerHeight: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Acción de la opción */ },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start // Alinear opciones del menú a la izquierda
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
    Spacer(modifier = Modifier.height(spacerHeight)) // Espaciado adicional
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
        modifier = Modifier
            .fillMaxWidth(),
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
        )  {
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
    // Agregar padding general de 16.dp alrededor de toda la sección
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Aprende sobre seguros",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        InsuranceImage()
        Spacer(modifier = Modifier.height(20.dp))
        InsuranceImage2()
        Spacer(modifier = Modifier.height(10.dp))
        InsuranceImage3()
        Spacer(modifier = Modifier.height(10.dp))
        InsuranceImage4()
    }
}

@Composable
fun InsuranceImage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .drawBehind {
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = .1f),
                        size = size.copy(height = size.height + (i * .5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
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
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
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



