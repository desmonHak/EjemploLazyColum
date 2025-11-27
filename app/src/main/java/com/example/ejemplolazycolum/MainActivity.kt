package com.example.ejemplolazycolum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejemplolazycolum.model.Producto
import com.example.ejemplolazycolum.ui.theme.EjemploLazyColumTheme
import com.example.ejemplolazycolum.ui.theme.TopBarColor

val listaEjemplo = listOf(
    Producto(1, "Manzanas", "1kg de manzanas rojas", 2.5, R.drawable.manzana),
    Producto(2, "Leche", "Leche entera 1L", 1.2, R.drawable.leche),
    Producto(3, "Pan", "Pan integral", 1.0, R.drawable.pan)
)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjemploLazyColumTheme {
              ListaCompraScreen(listaEjemplo,{})

                }
            }
        }
    }


@Composable
fun ListaCompraScreen(productos: List<Producto>, onAgregarClick: () -> Unit) {
    Scaffold(
        topBar = { ListaCompraTopBar() },
        floatingActionButton = { AgregarFab(onClick = onAgregarClick) }
    ) { paddingValues ->
        ListaCompra(
            productos = productos,
            modifier = Modifier.padding(paddingValues) // evita superposición con TopBar y FAB
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCompraTopBar() {
    CenterAlignedTopAppBar(
        title = { Text(text = "ListaCompra") },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = TopBarColor,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
@Composable
fun AgregarFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick,containerColor = TopBarColor,            // 👈 mismo color que la TopAppBar
        contentColor = MaterialTheme.colorScheme.onPrimary) {
        Icon( painter = painterResource(id = R.drawable.baseline_add_24), contentDescription = "Agregar")
    }
}

@Composable
fun ListaCompra(productos: List<Producto>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(productos) { producto ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Imagen
                    Image(
                        painter = painterResource(id = producto.imagenRes),
                        contentDescription = producto.nombre,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 8.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Descripción y nombre
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = producto.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )
                    }

                    // Precio
                    Text(
                        text = "${producto.precio}€",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
@Preview
@Composable
fun mostrarScreen()
{
    ListaCompraScreen(listaEjemplo,{})
}