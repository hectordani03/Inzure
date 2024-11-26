package io.inzure.app.ui.components

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import io.inzure.app.R
import io.inzure.app.data.model.SearchItem

@Composable
fun BottomSheetContent(allSearchItems: List<SearchItem>) {
    var searchText by remember { mutableStateOf("") }
    val filteredSearchItems = remember(searchText, allSearchItems) {
        if (searchText.isEmpty()) {
            allSearchItems
        } else {
            allSearchItems.filter { item ->
                when (item) {
                    is SearchItem.InsuranceItem -> item.companyName.contains(searchText, ignoreCase = true) ||
                            item.description.contains(searchText, ignoreCase = true)
                    is SearchItem.ChatItem -> item.userName.contains(searchText, ignoreCase = true) ||
                            item.userCompany.contains(searchText, ignoreCase = true)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .background(Color(0xFF072A4A))
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Buscar",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de búsqueda con estado compartido
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Lupa",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            SearchField(
                text = searchText,
                onTextChange = { searchText = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp)
                .background(Color.White.copy(alpha = 0.6f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
        ) {
            if (filteredSearchItems.isEmpty()) {
                item {
                    Text(
                        text = "No se encontraron resultados",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp
                    )
                }
            } else {
                items(filteredSearchItems) { item ->
                    when (item) {
                        is SearchItem.InsuranceItem -> {
                            InsuranceCardWithImage(
                                imageRes = item.imageRes,
                                companyLogo = item.companyLogo,
                                companyName = item.companyName,
                                description = item.description
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        is SearchItem.ChatItem -> {
                            ChatItemComposable(
                                userName = item.userName,
                                userCompany = item.userCompany,
                                userImageRes = item.userImageRes,
                                onClick = item.onClick
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(text: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = {
            Text(
                text = "Encuentra tu seguro",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = Color.White
        )
    )
}

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

            // Íconos del corazón y de la flecha
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_personal),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(bottom = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = "Desplegar más",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ChatItemComposable(
    userName: String,
    userCompany: String,
    userImageRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF04305A), RoundedCornerShape(12.dp))
            .padding(12.dp)
            .clickable { onClick() }, // Acción de clic para seleccionar el chat
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = userImageRes),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = userCompany,
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_chat),
            contentDescription = "Chat",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
