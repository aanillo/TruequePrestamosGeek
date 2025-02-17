import entity.*

fun main() {
/*
    var salir : Boolean = false

    do {
        println("\n===== USUARIO =====")
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
            2 -> agregarProducto()
            4 -> crearSolicitud()
            0 -> {
                println("Saliendo del programa...")
                salir = true
            }
            else -> println("Opción incorrecta, por favor ingrese un número válido.")
        }
    } while (!salir)
}

fun registrarUsuario() {
    println("\n=== Registro de Usuario ===")
    println("Por favor, introduzca el nombre:")
    val nombre = readln()
    println("Por favor, introduzca el correo:")
    val email = readln().trim()
    println("Por favor, ingrese una contraseña. Mínimo de 8 caracteres y debe contener un número y un carácter especial:")
    val contraseña = readln().trim()

    try {
        val usuario = Usuario(nombre, email, contraseña)
        println("Usuario ${usuario.nombre} registrado correctamente.")
    } catch (e: IllegalArgumentException) {
        println("No se pudo agregar el usuario debido a un fallo interno. Inténtelo de nuevo más tarde")
    }
}

fun agregarProducto() {
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
                val videojuego = Videojuego(titulo, descripcion, null, plataforma)
                println("${videojuego.titulo} agregado correctamente")
            } catch (e: IllegalArgumentException) {
                println("No se pudo agregar el producto debido a un fallo interno. Inténtelo de nuevo más tarde")
            }
        }
        2 -> {
            println("Por favor, introduzca el nombre del autor: ")
            val autor = readln()
            try {
                val libro = Libro(titulo, descripcion, null, autor)
                println("${libro.titulo} agregado correctamente")
            } catch (e: IllegalArgumentException) {
                println("Error: ${e.message}")
            }
        }
        else -> {
            println("No se pudo agregar el producto debido a un fallo interno. Inténtelo de nuevo más tarde")
        }
    }
}

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
}




