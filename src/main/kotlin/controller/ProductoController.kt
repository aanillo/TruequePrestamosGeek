package controller

import dao.LibroDAOH2
import dao.UsuarioDAOH2
import dao.VideojuegoDAOH2
import entity.*
import services.ProductoService
import services.UsuarioService
import sql.DataSourceFactory

private val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)
val videojuegoDAO = dataSource?.let { VideojuegoDAOH2(it) }
val libroDAO = dataSource?.let { LibroDAOH2(it) }
private val productoService = if (libroDAO != null && videojuegoDAO != null) {
    ProductoService(libroDAO, videojuegoDAO)
} else {
    null
}

class ProductoController {
    fun agregarProducto(usuarioLogueado: Usuario?) {
        println("\n=== Agregar producto ===")
        println("Por favor, ingrese el título del producto")
        val titulo = readln()
        println("Por favor, ingrese una breve descripción del producto")
        val descripcion = readln()

        println("¿Qué tipo de producto es? 1. Videojuego, 2. Libro")
        val tipo = readLine()?.toIntOrNull() ?: -1
        when(tipo) {
            1 -> {
                println("Por favor, ingrese la plataforma del videojuego (PLAYSTATION, XBOX, NINTENDO O PC):")
                val plataformaString = readlnOrNull()?.uppercase() ?: ""
                try {
                    val plataforma = Plataforma.valueOf(plataformaString)
                    val propietario = usuarioLogueado?.id
                    val videojuego = propietario?.let { Videojuego(titulo = titulo, descripcion = descripcion, propietario_id = it, plataforma = plataforma) }
                    val videojuegoNuevo = videojuego?.let { productoService?.createVideogame(it) }
                    println("${videojuegoNuevo?.titulo} agregado correctamente")
                } catch (e: IllegalArgumentException) {
                    println("No se pudo agregar el producto debido a un fallo interno. Inténtelo de nuevo más tarde")
                }
            }

            2 -> {
                println("Por favor, introduzca el nombre del autor: ")
                val autor = readln()
                try {
                    val propietarioId = usuarioLogueado?.id
                    val libro = propietarioId?.let { Libro(titulo = titulo, descripcion = descripcion, propietario_id = it, autor = autor) }
                    val libroNuevo = libro?.let { productoService?.createBook(it) }
                    println("${libroNuevo?.titulo} agregado correctamente")
                } catch (e: IllegalArgumentException) {
                    println("Error: ${e.message}")
                }
            }
            else -> {
                println("No se pudo agregar el producto debido a un fallo interno. Inténtelo de nuevo más tarde")
            }
        }
    }

    fun listarProducto(usuarioLogueado: Usuario?) {
        println("\n=== Listado de productos ===")
        println("¿Desea filtrar los productos (S,N)")
        val respuesta = readln().trim().uppercase()
        when (respuesta) {
            "S" -> {
                println("Selecciona el campo de filtrado: (1 - Título, 2 - Descripción, 3 - Propietario, 4 - Estado, 0 - Salir)")
                val campo = readln().toInt()
                when (campo) {
                    1 -> {
                        println("Introduce el título del producto:")
                        val titulo = readln().trim()
                        filtrarTitulo(titulo)
                    }
                    2 -> {
                        println("Introduce la descripción del producto:")
                        val descripcion = readln().trim()
                        filtrarDescripcion(descripcion)
                    }
                    3 -> {
                        println("Introduce la id del usuario:")
                        val usuario = readln().trim()
                        filtrarUsuario(usuario)
                    }
                    4 -> {
                        println("Introduce el estado:")
                        val estado = readln().trim().uppercase()
                        filtrarEstado(estado)
                    }
                    else -> println("Error. Por favor, introduce una opción correcta.")
                }
            }
            "N" -> {
                listarSinFiltro()
            }
            else -> println("Error. Por favor, introduce una opción correcta.")
        }
    }

    fun filtrarTitulo(titulo: String) {
        val productos = mutableListOf<Producto>()
        productoService?.getVideogamesByTitle(titulo)?.let { productos.addAll(it) }
        productoService?.getBooksByTitle(titulo)?.let { productos.addAll(it) }

        if (productos.isEmpty()) {
            println("No se encontraron productos con el título '$titulo'.")
        } else {
            productos.forEach { println(it.mostrarDetalles()) }
        }
    }

    fun filtrarDescripcion(descripcion: String) {
        val productos = mutableListOf<Producto>()
        productoService?.getVideogamesByDesciption(descripcion)?.let { productos.addAll(it) }
        productoService?.getBooksByDesciption(descripcion)?.let { productos.addAll(it) }

        if (productos.isEmpty()) {
            println("No se encontraron productos con la descripción '$descripcion'.")
        } else {
            productos.forEach { println(it.mostrarDetalles()) }
        }
    }

    fun filtrarUsuario(nombre: String) {
        val productos = mutableListOf<Producto>()
        productoService?.getVideogamesByOwner(nombre)?.let { productos.addAll(it) }
        productoService?.getBooksByOwner(nombre)?.let { productos.addAll(it) }

        if (productos.isEmpty()) {
            println("No se encontraron productos para el usuario '$nombre'.")
        } else {
            productos.forEach { println(it.mostrarDetalles()) }
        }
    }

    fun filtrarEstado(estado: String) {
        val productos = mutableListOf<Producto>()
        productoService?.getVideogamesByState(estado)?.let { productos.addAll(it) }
        productoService?.getBooksByState(estado)?.let { productos.addAll(it) } // Corrección aquí

        if (productos.isEmpty()) {
            println("No se encontraron productos con el estado '$estado'.")
        } else {
            productos.forEach { println(it.mostrarDetalles()) }
        }
    }

    fun listarSinFiltro() {
        val productos = mutableListOf<Producto>()
        productoService?.getAllVideogames()?.let { productos.addAll(it) }
        productoService?.getAllBooks()?.let { productos.addAll(it) }

        if (productos.isEmpty()) {
            println("No se encontraron productos.")
        } else {
            productos.forEach { println(it.mostrarDetalles()) }
        }
    }
}