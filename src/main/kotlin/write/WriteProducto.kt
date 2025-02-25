package write

import com.google.gson.Gson
import dao.LibroDAOH2
import dao.SolicitudDAOH2
import dao.UsuarioDAOH2
import dao.VideojuegoDAOH2
import entity.Libro
import entity.Producto
import entity.Usuario
import entity.Videojuego
import services.ProductoService
import services.SolicitudService
import services.UsuarioService
import sql.DataSourceFactory
import java.io.File
import java.io.IOException

private val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)
private val usuarioDAO = dataSource?.let { UsuarioDAOH2(it) }
private val usuarioService = usuarioDAO?.let { UsuarioService(it) }
val videojuegoDAO = dataSource?.let { VideojuegoDAOH2(it) }
val libroDAO = dataSource?.let { LibroDAOH2(it) }
private val solicitudDAO = dataSource?.let { SolicitudDAOH2(it) }
private val solicitudService = solicitudDAO?.let { SolicitudService(it) }
private val productoService = if (libroDAO != null && videojuegoDAO != null) {
    ProductoService(libroDAO, videojuegoDAO)
} else {
    null
}

class WriteProducto {

    fun exportarProductosAArchivoTXT(nombreArchivo: String, productos: MutableList<Producto>): Boolean {
        val directorio = File("src/main/resources/ficheros")
        if (!directorio.exists() && !directorio.mkdirs()) {
            println("Error: No se pudo crear la carpeta '${directorio.path}'.")
            return false
        }

        val archivo = File(directorio, nombreArchivo)


        if (archivo.exists() && !archivo.canWrite()) {
            println("Error: No se puede escribir en el archivo '${archivo.path}'. Verifique los permisos.")
            return false
        }

        return try {
            archivo.bufferedWriter().use { writer ->
                productos.forEach { producto ->
                    val tipo = when (producto) {
                        is Videojuego -> "Videojuego"
                        is Libro -> "Libro"
                        else -> "Desconocido"
                    }

                    val atributoEspecifico = when (producto) {
                        is Videojuego -> producto.plataforma.name
                        is Libro -> producto.autor
                        else -> "Desconocido"
                    }

                    val linea = "${producto.id};${producto.titulo};${producto.descripcion};$tipo;$atributoEspecifico;${producto.estado}"
                    writer.write(linea)
                    writer.newLine()
                }
                writer.flush()
            }
            println("Productos exportados exitosamente a '${archivo.path}'.")
            println("Contenido del archivo '${archivo.path}':")
            println(archivo.readText())
            true
        } catch (e: IOException) {
            println("Error: No se pudo generar el archivo '${archivo.path}'. Detalles: ${e.message}")
            false
        }
    }


    fun exportarProductosAArchivoJSON(nombreArchivo: String, productos: List<Producto>): Boolean {
        val directorio = File("src/main/resources/ficheros")
        if (!directorio.exists() && !directorio.mkdirs()) {
            println("Error: No se pudo crear la carpeta '${directorio.path}'.")
            return false
        }

        val archivo = File(directorio, nombreArchivo)

        if (archivo.exists() && !archivo.canWrite()) {
            println("Error: No se puede escribir en el archivo '${archivo.path}'. Verifique los permisos.")
            return false
        }

        return try {
            val productosJson = productos.map { producto ->
                val tipo = when (producto) {
                    is Videojuego -> "Videojuego"
                    is Libro -> "Libro"
                    else -> "Desconocido"
                }

                val atributoEspecifico = when (producto) {
                    is Videojuego -> producto.plataforma.name
                    is Libro -> producto.autor
                    else -> "Desconocido"
                }

                mapOf(
                    "id" to producto.id,
                    "titulo" to producto.titulo,
                    "descripcion" to producto.descripcion,
                    "tipo" to tipo,
                    "atributo" to atributoEspecifico,
                    "estado" to producto.estado.toString()
                )
            }

            val gson = Gson()
            archivo.outputStream().bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(gson.toJson(productosJson))
                writer.flush()
            }

            println("Productos exportados exitosamente a '${archivo.path}'.")
            println("Contenido del archivo '${archivo.path}':")
            println(archivo.readText())
            true
        } catch (e: IOException) {
            println("Error: No se pudo generar el archivo '${archivo.path}'. Detalles: ${e.message}")
            false
        }
    }

    fun descargarProductos(usuarioLogueado: Usuario?) {
        println("\n=== Descarga de productos ===")
        println("Indique el formato de descarga (1. TXT, 2. JSON): ")
        val respuesta = readln().toIntOrNull()

        if (usuarioLogueado == null) {
            println("Error: No hay un usuario logueado.")
            return
        }

        val nombreUsuario = usuarioLogueado.nombre
        val productos: MutableList<Producto> = mutableListOf()
        val videojuegos = productoService?.getVideogamesByOwner(nombreUsuario)
        val libros = productoService?.getBooksByOwner(nombreUsuario)

        if (videojuegos != null) productos.addAll(videojuegos)
        if (libros != null) productos.addAll(libros)

        if (productos.isEmpty()) {
            println("No tiene productos registrados para exportar.")
            return
        }

        println("Productos encontrados (${productos.size}):")
        productos.forEach { println(it) }

        val archivo: String
        val exito: Boolean

        when (respuesta) {
            1 -> {
                archivo = "mis_productos.txt"
                exito = exportarProductosAArchivoTXT(archivo, productos)
            }
            2 -> {
                archivo = "mis_productos.json"
                exito = exportarProductosAArchivoJSON(archivo, productos)
            }
            else -> {
                println("Error: Formato no reconocido. Ingrese '1' para TXT o '2' para JSON.")
                return
            }
        }

        if (exito) {
            println("Se han exportado ${productos.size} productos a 'ficheros/$archivo'.")
        } else {
            println("Error: No se pudo exportar los productos a 'ficheros/$archivo'.")
        }
    }
}