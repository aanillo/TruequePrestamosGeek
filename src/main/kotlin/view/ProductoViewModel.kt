package view

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import entity.*
import java.io.File

class ProductoViewModel {

    var productos = mutableStateListOf<Producto>()
        private set

    var errorMessage by mutableStateOf("")

    fun cargarProductosDesdeTXT(ruta: String) {
        val archivo = File(ruta)
        if (!archivo.exists()) {
            errorMessage = "Error: El archivo no existe."
            return
        }

        try {
            archivo.bufferedReader().use { br ->
                br.forEachLine { line ->
                    val campos = line.split(";")
                    if (campos.size == 6) {
                        val id = campos[0].toIntOrNull()
                        val titulo = campos[1]
                        val descripcion = campos[2]
                        val tipo = campos[3]
                        val atributoEspecifico = campos[4]
                        val estado = campos[5]

                        if (id == null) {
                            errorMessage = "Error: ID inválido en la línea -> $line"
                            return@forEachLine
                        }

                        val producto: Producto = when (tipo) {
                            "Videojuego" -> Videojuego(id, titulo, descripcion, 1, EstadoProducto.valueOf(estado), Plataforma.valueOf(atributoEspecifico))
                            "Libro" -> Libro(id, titulo, descripcion, 1, EstadoProducto.valueOf(estado), atributoEspecifico)
                            else -> {
                                errorMessage = "Error: Tipo de producto desconocido -> $tipo"
                                return@forEachLine
                            }
                        }

                        productos.add(producto)
                    } else {
                        errorMessage = "Error: Línea con formato incorrecto -> $line"
                    }
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error: Ocurrió un problema al leer el archivo TXT -> ${e.message}"
        }
    }

    fun cargarProductosDesdeJson(ruta: String) {
        val archivo = File(ruta)
        if (!archivo.exists()) {
            errorMessage = "Error: El archivo no existe."
            return
        }

        try {
            val gson = Gson()
            val productosJson = gson.fromJson(archivo.reader(), List::class.java) as List<Map<String, Any>>

            productos.clear()

            productosJson.forEach { productoMap ->
                val tipo = productoMap["tipo"] as String
                val id = (productoMap["id"] as Double).toInt()
                val titulo = productoMap["titulo"] as String
                val descripcion = productoMap["descripcion"] as String
                val atributoEspecifico = productoMap["atributo"] as String
                val estado = EstadoProducto.valueOf(productoMap["estado"] as String)

                val producto: Producto = when (tipo) {
                    "Videojuego" -> Videojuego(id, titulo, descripcion, 1, estado, Plataforma.valueOf(atributoEspecifico))
                    "Libro" -> Libro(id, titulo, descripcion, 1, estado, atributoEspecifico)
                    else -> {
                        errorMessage = "Error: Tipo de producto desconocido."
                        return@forEach
                    }
                }

                productos.add(producto)
            }
        } catch (e: Exception) {
            errorMessage = "Error: Formato de archivo incorrecto -> ${e.message}"
        }
    }
}
