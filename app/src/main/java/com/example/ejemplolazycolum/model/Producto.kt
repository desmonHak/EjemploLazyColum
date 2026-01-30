package com.example.ejemplolazycolum.model

import android.net.Uri

/**
 * Data class que representa un producto en la lista de compra.
 * Soporta tanto imágenes de recursos (drawables) como URIs de galería.
 *
 * @param id Identificador único del producto
 * @param nombre Nombre visible del producto
 * @param descripcion Descripción detallada
 * @param precio Precio en euros
 * @param imagenRes ID de recurso drawable (para imágenes empaquetadas)
 * @param imagenUri URI de imagen seleccionada desde galería (nullable)
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenRes: Int? = null,        // Nullable para productos con URI
    val imagenUri: Uri? = null         // Nullable para productos con drawable
)
