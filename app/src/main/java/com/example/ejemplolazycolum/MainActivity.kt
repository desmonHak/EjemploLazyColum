package com.example.ejemplolazycolum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon

import coil.compose.AsyncImage // si quieres usarlo
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ejemplolazycolum.model.Producto
import com.example.ejemplolazycolum.ui.theme.EjemploLazyColumTheme
import com.example.ejemplolazycolum.ui.theme.TopBarColor
import kotlin.random.Random

/**
 * Lista inicial de productos para inicializar la UI.
 * Esta lista estática NO se usa directamente en pantalla,
 * solo sirve para poblar la lista reactiva.
 */
val listaEjemplo = mutableListOf<Producto>(
    Producto(1, "Manzanas", "1kg de manzanas rojas", 2.5, imagenRes = R.drawable.manzana),
    Producto(2, "Leche", "Leche entera 1L", 1.2, imagenRes = R.drawable.leche),
    Producto(3, "Pan", "Pan integral", 1.0, imagenRes = R.drawable.pan)
)

/**
 * Activity principal que configura la aplicación completa usando Jetpack Compose.
 *
 * Jetpack Compose reemplaza el XML tradicional: toda la UI se define con código Kotlin
 * usando funciones marcadas con @Composable.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configura la UI usando Jetpack Compose
        setContent {

            // Tema de Material Design 3 (colores, tipografía, espaciado)
            EjemploLazyColumTheme {

                // Estado principal de la aplicación

                // manejar la lista como estado,
                // mutableStateListOf crea una lista observable por Compose.
                // Cualquier cambio (add, remove) provoca recomposición automática.
                val productos = remember {
                    mutableStateListOf<Producto>().apply {
                        addAll(listaEjemplo)
                    }
                }

                // Estado para controlar visibilidad del diálogo
                var mostrarDialogo by remember { mutableStateOf(false) }

                // Diálogo condicional: se muestra solo si mostrarDialogo es true
                if (mostrarDialogo) {
                    DialogoNuevoProducto(
                        onDismiss = { mostrarDialogo = false },
                        onConfirm = { nuevoProducto ->
                            productos.add(nuevoProducto)
                            mostrarDialogo = false
                        }
                    )
                }

                // Pantalla principal pasándole la lista reactiva
                ListaCompraScreen(
                    // es importante usar productos, y no listaEjemplo, por que en caso de usar
                    // listaEjemplo la UI no se actualizaria al agregar elementos, por lo
                    // que no veriamos cambios
                    productos = productos,
                    onAgregarClick = { mostrarDialogo = true }
                )

            }
        }
    }
}

/**
 * Diálogo modal para crear un nuevo producto.
 *
 * @param onDismiss Acción ejecutada al cerrar el diálogo
 * @param onConfirm Callback con el nuevo producto creado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoNuevoProducto(
    onDismiss: () -> Unit,
    onConfirm: (Producto) -> Unit
) {
    // Estados locales del diálogo (se resetean cada vez que se abre)
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // Launcher para seleccionar imagen desde galería/cámara
    val launcherImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imagenUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val precio = precioTexto.toDoubleOrNull() ?: 0.0

                val nuevoProducto = Producto(
                    id = Random.nextInt(0, Int.MAX_VALUE),
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    // Lógica: URI tiene prioridad sobre drawable
                    imagenUri = imagenUri,
                    imagenRes = if (imagenUri == null) R.drawable.manzana else null
                )
                onConfirm(nuevoProducto)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Nuevo producto") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precioTexto,
                    onValueChange = { precioTexto = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.size(8.dp))

                TextButton(onClick = { launcherImagen.launch("image/*") }) {
                    Text(if (imagenUri != null) "Cambiar imagen" else "Elegir imagen")
                }
            }
        }
    )
}

/**
 * Pantalla principal usando Scaffold (estructura Material Design).
 * Scaffold proporciona TopBar, FAB y contenido principal automáticamente.
 */
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

/**
 * Barra superior centrada con título y colores personalizados.
 */
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

/**
 * FAB (Floating Action Button) para agregar productos.
 */
@Composable
fun AgregarFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = TopBarColor,            // mismo color que la TopAppBar
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_add_24),
            contentDescription = "Agregar"
        )
    }
}

/**
 * Lista de productos usando LazyColumn (equivalente a RecyclerView).
 * Solo renderiza elementos visibles en pantalla (mejor rendimiento).
 */
@Composable
fun ListaCompra(productos: List<Producto>, modifier: Modifier = Modifier) {
    return LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(productos) { producto ->
            // Cada producto es una Card de Material 3
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

                    /**
                     * when es como un switch-case de Java/C++, pero mucho más potente.
                     * Reemplaza if-else anidados cuando tienes varias condiciones.
                     * when {
                     *     producto.imagenUri != null -> AsyncImage(...)     // Si hay URI de galería
                     *     producto.imagenRes != null -> Image(...)          // Si hay drawable
                     *     else -> Image(placeholder)                        // Si no hay nada
                     * }
                     */
                    when {

                        // Imagen del producto (64dp x 64dp)
                        producto.imagenUri != null -> { // Si hay URI de galería
                            // Necesitas Coil: implementation("io.coil-kt:coil-compose:2.7.0")
                            AsyncImage(
                                model = producto.imagenUri,
                                contentDescription = producto.nombre,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.manzana) // loading
                            )
                        }

                        producto.imagenRes != null -> { // Si hay drawable
                            Image(
                                painter = painterResource(id = producto.imagenRes),
                                contentDescription = producto.nombre,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        else -> { // Si no hay nada

                            // Placeholder si no hay imagen
                            Image(
                                painter = painterResource(R.drawable.manzana),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }


                    // Información del producto
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

                    // Precio a la derecha
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
fun mostrarScreen() {
    ListaCompraScreen(listaEjemplo, {})
}