package io.inzure.app.ui.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.inzure.app.R

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.inzure.app.InzureTheme

class InsuranceInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InzureTheme {
                InsuranceInfoView()
            }
        }
    }
}

@Composable
fun InsuranceInfoView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Fondo blanco
            .verticalScroll(rememberScrollState()) // Habilitar scroll
    ) {
        // Encabezado
        InsuranceNavigationBar()

        // Contenido principal
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            // Imagen de la aseguradora
            Image(
                painter = painterResource(id = R.drawable.ic_qualitas), // Sustituir con la imagen de la aseguradora
                contentDescription = "Logo de la aseguradora",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre de la aseguradora
            Text(
                text = "Seguros Integrales",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                text = "Con más de 20 años de experiencia en el mercado, ofrecemos soluciones innovadoras para proteger lo que más valoras: tu familia, tu auto, y tu negocio.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Misión de la aseguradora
            MissionSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Servicios ofrecidos
            ServicesSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de asesores
            AdvisorsSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de contacto
            ContactSectionInsurance()
        }
    }
}

@Composable
fun MissionSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Nuestra misión",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Proteger y garantizar la tranquilidad de nuestros clientes a través de soluciones personalizadas en seguros de vida, autos y empresas, con un enfoque en la excelencia y la confianza.",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
fun AdvisorsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Nuestros asesores",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Lista de asesores
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            AdvisorCard("Maria Gomez", "Especialista en seguros de vida", R.drawable.ic_profile)
            AdvisorCard("Juan Perez", "Asesor de seguros empresariales", R.drawable.ic_profile2)
            AdvisorCard("Lucia Torres", "Experta en seguros de autos", R.drawable.ic_profile)
        }
    }
}

@Composable
fun AdvisorCard(name: String, expertise: String, image: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .background(Color(0xFFF3F3F3), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = expertise,
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InsuranceNavigationBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF04305A)) // Fondo azul oscuro
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu), // Ícono del menú
                contentDescription = "Menú",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Información de la Aseguradora",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun ServicesSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Nuestros servicios",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ServiceItem("Seguros de vida", "Protege a tu familia con las mejores coberturas de vida.")
        ServiceItem("Seguros para autos", "Cubre cualquier daño o accidente de tu vehículo.")
        ServiceItem("Seguros empresariales", "Ofrecemos planes diseñados para proteger tu negocio.")
    }
}

@Composable
fun ServiceItem(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F3F3), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF04305A),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ContactSectionInsurance() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF04305A), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Contáctanos",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        ContactRowInsurance(icon = R.drawable.ic_profile2, text = "(55) 5678-1234")
        Spacer(modifier = Modifier.height(8.dp))
        ContactRowInsurance(icon = R.drawable.email_icon, text = "contacto@segurosintegrales.com")
        Spacer(modifier = Modifier.height(8.dp))
        ContactRowInsurance(icon = R.drawable.ic_profile2, text = "Atendemos de lunes a viernes de 9:00 AM a 6:00 PM.")
    }
}

@Composable
fun ContactRowInsurance(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}
