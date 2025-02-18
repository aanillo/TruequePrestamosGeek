import dao.LibroDAOH2
import dao.UsuarioDAOH2
import dao.VideojuegoDAOH2
import entity.*
import services.LibroService
import services.UsuarioService
import services.VideojuegoService
import sql.DataSourceFactory


private val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)
private val usuarioDAO = dataSource?.let { UsuarioDAOH2(it) }
private val usuarioService = usuarioDAO?.let { UsuarioService(it) }
private val videojuegoDAO = dataSource?.let { VideojuegoDAOH2(it) }
private val videojuegoSevice = videojuegoDAO?.let { VideojuegoService(it) }
private val libroDAO = dataSource?.let { LibroDAOH2(it) }
private val libroSevice = libroDAO?.let { LibroService(it) }


fun main() {
    var usuarioLogueado: Usuario? = null

    println("Introduce nombre de usuario:")
    val usernameAdmin = readln()
    println("Introduce contraseña:")
    val passwordAdmin = readln()

    if(usernameAdmin.equals("admin") && passwordAdmin.equals("admin")) {
        usuarioLogueado = usuarioService?.login(usernameAdmin, passwordAdmin)

        var salir : Boolean = false

        do {
            println("\n===== USUARIO: ${ usuarioLogueado?.nombre } =====")
            println("1. Registrar nuevo usuario")
            println("2. Agregar producto")
            println("3. Listar productos")
            println("4. Crear solicitudes")
            println("5. Listar solicitudes")
            println("6. Actualizar estado de solicitud")
            println("7. Ficheros Carga productos (txt, json)")
            println("8. Ficheros Descarga productos (txt, json)")
            println("0. Salir")

            print("Por favor, indique una opción: ")
            val opcion = readlnOrNull()?.toIntOrNull() ?: -1

            when (opcion) {
                1 -> registrarUsuario()
                2 -> {
                    if (usuarioLogueado != null) {
                        agregarProducto(usuarioLogueado)
                    } else {
                        println("Debe iniciar sesión primero.")
                    }
                }
                3 -> {
                    if (usuarioLogueado != null) {
                        listarProducto(usuarioLogueado)
                    } else {
                        println("Debe iniciar sesión primero.")
                    }
                }
                0 -> {
                    println("Saliendo del programa...")
                    salir = true
                }
                else -> println("Opción incorrecta, por favor ingrese un número válido.")
            }
        } while (!salir)
    } else {
        println("Error. Por favor, introduce las credenciales correctas para acceder al programa.")
    }

}

fun registrarUsuario() {
    println("\n=== Registro de Usuario ===")
    println("Por favor, introduzca el nombre:")
    val nombre = readln()
    println("Por favor, introduzca el correo:")
    val email = readln().trim()
    println("Por favor, ingrese una contraseña. Mínimo de 8 caracteres y debe contener un número y un carácter especial:")
    val password = readln().trim()

    try {
        val usuario = Usuario(nombre = nombre, email = email, password = password)
        val nuevoUsuario = usuarioService?.create(usuario)
        println("Usuario ${nuevoUsuario?.nombre} registrado correctamente.")
    } catch (e: IllegalArgumentException) {
        println("No se pudo agregar el usuario debido a un fallo interno. Inténtelo de nuevo más tarde")
    }
}




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
                val propietarioId = usuarioLogueado?.id
                val videojuego = propietarioId?.let { Videojuego(titulo = titulo, descripcion = descripcion, propietario_id = it, plataforma = plataforma) }
                val videojuegoNuevo = videojuego?.let { videojuegoSevice?.create(it) }
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
                val libroNuevo = libro?.let { libroSevice?.create(it) }
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
            }
        }
    }
}

fun filtrarTitulo(titulo: String) {
    val productos: MutableList<Producto> = mutableListOf()
    val videojuegos: List<Videojuego>? = videojuegoDAO?.getAllByTitle(titulo)
    val libros: List<Libro>? = libroDAO?.getAllByTitle(titulo)

    if (videojuegos != null) {
        productos.addAll(videojuegos)
    }
    if (libros != null) {
        productos.addAll(libros)
    }
    if (productos.isEmpty()) {
        println("No se encontraron productos con el título '$titulo'.")
    } else {
        for (producto in productos) {
            println(producto.mostrarDetalles())
        }
    }
}

fun filtrarDescripcion(descripcion: String) {
    val productos: MutableList<Producto> = mutableListOf()
    val videojuegos: List<Videojuego>? = videojuegoDAO?.getAllByDesciption(descripcion)
    val libros: List<Libro>? = libroDAO?.getAllByDesciption(descripcion)

    if (videojuegos != null) {
        productos.addAll(videojuegos)
    }
    if (libros != null) {
        productos.addAll(libros)
    }
    if (productos.isEmpty()) {
        println("No se encontraron productos con la descripción '$descripcion'.")
    } else {
        for (producto in productos) {
            println(producto.mostrarDetalles())
        }
    }
}
/*
fun crearSolicitud() {
    println("\n=== Crear Solicitud ===")
    println("Ingrese el ID del producto:")
    val id = readLine()?.toIntOrNull()

    println("Tipo de solicitud (1 - Trueque, 2 - Préstamo):")
    val opcion = readLine()?.toInt()
    val tipo = when(opcion) {
        1 -> {
            TipoSolicitud.TRUEQUE
        }
        2 -> {
            TipoSolicitud.PRESTAMO
        }
        else -> throw IllegalArgumentException("Debe ingresar 1 para Trueque o 2 para Préstamo.")
    }

    val solicitud = Solicitud(id, tipo, null, null);

    println("Solicitud de $tipo creada correctamente con ID: ${solicitud.id}")
 */





