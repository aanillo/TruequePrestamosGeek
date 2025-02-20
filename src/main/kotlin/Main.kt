import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.google.gson.Gson
import controller.UsuarioController
import dao.LibroDAOH2
import dao.SolicitudDAOH2
import dao.UsuarioDAOH2
import dao.VideojuegoDAOH2
import entity.*
import services.LibroService
import services.SolicitudService
import services.UsuarioService
import services.VideojuegoService
import sql.DataSourceFactory
import ui.Ui
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


private val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)
private val usuarioDAO = dataSource?.let { UsuarioDAOH2(it) }
private val usuarioService = usuarioDAO?.let { UsuarioService(it) }
private val videojuegoDAO = dataSource?.let { VideojuegoDAOH2(it) }
private val videojuegoSevice = videojuegoDAO?.let { VideojuegoService(it) }
private val libroDAO = dataSource?.let { LibroDAOH2(it) }
private val libroSevice = libroDAO?.let { LibroService(it) }
private val solicitudDAO = dataSource?.let { SolicitudDAOH2(it) }
private val solicitudService = solicitudDAO?.let { SolicitudService(it) }
private val usuarioController = usuarioService?.let { UsuarioController() }

fun main() {
    var usuarioLogueado: Usuario? = null

    println("Introduce nombre de usuario:")
    val usernameLogin = readln()
    println("Introduce contraseña:")
    val passwordLogin = readln()

    val usuarios: List<Usuario>? = usuarioService?.getAll()

    usuarioLogueado = usuarios?.find { it.nombre == usernameLogin && it.password == passwordLogin }

    if (usuarioLogueado != null) {
        usuarioLogueado = usuarioService?.login(usernameLogin, passwordLogin)

        var salir = false
        do {
            println("\n===== USUARIO: ${usuarioLogueado?.nombre} =====")
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
                1 -> usuarioController?.registrarUsuario()
                2 -> agregarProducto(usuarioLogueado)
                3 -> listarProducto(usuarioLogueado)
                4 -> crearSolicitud(usuarioLogueado)
                5 -> filtrarSolicitud(usuarioLogueado)
                6 -> actualizarSolicitud(usuarioLogueado)
                7 -> {
                    println("Abriendo la interfaz gráfica...")
                    application {
                        Window(onCloseRequest = ::exitApplication) {
                            Ui().MyApp()
                        }
                    }
                }
                8 -> descargarProductos(usuarioLogueado)
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

fun filtrarUsuario(nombre: String) {
    val productos: MutableList<Producto> = mutableListOf()
    val videojuegos: List<Videojuego>? = videojuegoDAO?.getAllByOwner(nombre)
    val libros: List<Libro>? = libroDAO?.getAllByOwner(nombre)

    if (videojuegos != null) {
        productos.addAll(videojuegos)
    }
    if (libros != null) {
        productos.addAll(libros)
    }
    if (productos.isEmpty()) {
        println("No se encontraron productos para el usuario '$nombre'.")
    } else {
        for (producto in productos) {
            println(producto.mostrarDetalles())
        }
    }
}

fun filtrarEstado(estado: String) {
    val productos: MutableList<Producto> = mutableListOf()
    val videojuegos: List<Videojuego>? = videojuegoDAO?.getAllByState(estado)
    val libros: List<Libro>? = libroDAO?.getAllByState(estado)

    if (videojuegos != null) {
        productos.addAll(videojuegos)
    }
    if (libros != null) {
        productos.addAll(libros)
    }
    if (productos.isEmpty()) {
        println("No se encontraron productos con el estado '$estado'.")
    } else {
        for (producto in productos) {
            println(producto.mostrarDetalles())
        }
    }
}

fun listarSinFiltro() {
    val productos: MutableList<Producto> = mutableListOf()
    val videojuegos: List<Videojuego>? = videojuegoDAO?.getAll()
    val libros: List<Libro>? = libroDAO?.getAll()

    if (videojuegos != null) {
        productos.addAll(videojuegos)
    }
    if (libros != null) {
        productos.addAll(libros)
    }
    if (productos.isEmpty()) {
        println("No se encontraron productos.")
    } else {
        for (producto in productos) {
            println(producto.mostrarDetalles())
        }
    }
}

fun crearSolicitud(usuarioLogueado: Usuario?) {
    println("\n=== Crear Solicitud ===")
    println("Ingrese el ID del producto:")
    val idProducto = readlnOrNull()?.toIntOrNull()

    if (idProducto == null) {
        println("Error: El ID del producto debe ser un número válido.")
        return
    }

    println("Ingrese el tipo (VIDEOJUEGO o LIBRO):")
    val tipoProducto = readln().trim().uppercase()

    if (tipoProducto != "VIDEOJUEGO" && tipoProducto != "LIBRO") {
        println("Error: El tipo de producto debe ser VIDEOJUEGO o LIBRO.")
        return
    }

    println("Tipo de solicitud (1 - Trueque, 2 - Préstamo):")
    val opcion = readLine()?.toInt()

    val idUsuario = usuarioLogueado?.id

    val tipoSolicitud = when (opcion) {
        1 -> TipoSolicitud.TRUEQUE
        2 -> TipoSolicitud.PRESTAMO
        else -> {
            println("Error: Debe ingresar 1 para Trueque o 2 para Préstamo.")
            return
        }
    }

    val solicitud = idUsuario?.let {
        Solicitud(
            tipoSolicitud = tipoSolicitud,
            solicitante_id = it,
            producto_id = idProducto,
            estado = EstadoSolicitud.PENDIENTE
        )
    }

    val solicitudCreada = solicitud?.let { solicitudService?.create(it, idProducto, tipoProducto) }

    if (solicitudCreada != null) {
        val nombreProducto = when (tipoProducto) {
            "VIDEOJUEGO" -> videojuegoSevice?.getById(idProducto)
            "LIBRO" -> libroSevice?.getById(idProducto)
            else -> null
        }?.titulo

        if (nombreProducto != null) {
            println("Solicitud de ${solicitudCreada.tipoSolicitud} creada correctamente con el producto: $nombreProducto.")
        } else {
            println("Error: No se pudo obtener el nombre del producto.")
        }
    } else {
        println("Hubo un error al crear la solicitud.")
    }

}

fun filtrarSolicitud(usuarioLogueado: Usuario?) {
    println("\n=== Listado de solicitudes ===")
    val idUsuario = usuarioLogueado?.id ?: run {
        println("Error: Usuario no identificado.")
        return
    }

    println("¿Deseas ver solicitudes activas o histórico? (A/H)")
    val estadoFiltro = when (readLine()?.trim()?.uppercase()) {
        "A" -> EstadoSolicitud.PENDIENTE.name
        "H" -> null
        else -> {
            println("Error: Por favor, responda 'A' para solicitudes activas o 'H' para histórico.")
            return
        }
    }

    println("¿Desea listar sus solicitudes o las realizadas a sus productos? \n1. Mis solicitudes / 2. Solicitudes a mis productos")
    val rolFiltro = readln().toIntOrNull()
    if (rolFiltro !in listOf(1, 2)) {
        println("Error: Opción no reconocida. Ingrese '1' para ver sus solicitudes o '2' para ver las solicitudes a sus productos.")
        return
    }

    val solicitudes = when (rolFiltro) {
        1 -> solicitudService?.getSolicitudesPorUsuario(idUsuario, estadoFiltro)
        2 -> solicitudService?.getSolicitudesAProductosDeUsuario(idUsuario, estadoFiltro)
        else -> null
    }

    if (solicitudes.isNullOrEmpty()) {
        println("No se encontraron solicitudes para los criterios seleccionados.")
    } else {
        println("Producto | Estado | Tipo de Solicitud | ${if (rolFiltro == 1) "Propietario" else "Solicitante"}")
        println("-".repeat(50))
        solicitudes.forEach { solicitud ->
            println("${solicitud.producto_nombre} | ${solicitud.estado} | ${solicitud.tipoSolicitud} | ${solicitud.solicitante_nombre}")
        }
    }
}


fun actualizarSolicitud(usuarioLogueado: Usuario?) {
    println("\n=== Actualizar estado de solicitud ===")
    println("Introduzca el ID de la solicitud:")
    val id = readln().trim().toIntOrNull()

    if (id == null) {
        println("Error: El ID ingresado no es válido. Debe ser un número.")
        return
    }

    val solicitud = solicitudService?.getById(id)

    if (solicitud == null) {
        println("Error: No se encontró una solicitud con el ID ingresado.")
        return
    }

    println("Ingrese el nuevo estado: \n1. ACEPTADA \n2. RECHAZADA \n3. FINALIZADA")
    val respuesta = readln().trim().uppercase()

    if (solicitud.estado == EstadoSolicitud.PENDIENTE) {
        val estadoNuevo = when (respuesta) {
            "ACEPTADA" -> EstadoSolicitud.ACEPTADA
            "RECHAZADA" -> EstadoSolicitud.RECHAZADA
            else -> {
                println("Error. El estado debe ser ACEPTADA, RECHAZADA o FINALIZADA")
                return
            }
        }

        val solicitudNueva = solicitudService.updateSolicitud(id, estadoNuevo.toString())

        if (solicitudNueva != null) {
            println("Solicitud ${solicitudNueva.id} actualizada a estado: ${solicitudNueva.estado}")
        } else {
            println("Error: No se pudo actualizar la solicitud.")
        }
    } else if (solicitud.estado == EstadoSolicitud.ACEPTADA) {
        val estadoNuevo = when (respuesta) {
            "FINALIZADA" -> EstadoSolicitud.ACEPTADA
            else -> {
                println("Error. El estado debe ser ACEPTADA, RECHAZADA o FINALIZADA")
                return
            }
        }
        val solicitudNueva = solicitudService.updateSolicitud(id, estadoNuevo.toString())

        if (solicitudNueva != null) {
            println("Solicitud ${solicitudNueva.id} actualizada a estado: ${solicitudNueva.estado}")
        } else {
            println("Error: No se pudo actualizar la solicitud.")
        }
    } else {
        println("Error: Solo se pueden actualizar solicitudes en estado 'PENDIENTE'.")
    }
}

fun cargarProductosDesdeTXT(ruta: String): Boolean {
    val archivo = File(ruta)

    if (!archivo.exists()) {
        println("Error: El archivo no existe.")
        return false
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
                        println("Error: ID inválido en la línea -> $line")
                        return@forEachLine
                    }

                    val producto: Producto = when (tipo) {
                        "Videojuego" -> Videojuego(
                            id = id,
                            titulo = titulo,
                            descripcion = descripcion,
                            propietario_id = 1,
                            estado = EstadoProducto.valueOf(estado),
                            plataforma = Plataforma.valueOf(atributoEspecifico)
                        )
                        "Libro" -> Libro(
                            id = id,
                            titulo = titulo,
                            descripcion = descripcion,
                            propietario_id = 1,
                            estado = EstadoProducto.valueOf(estado),
                            autor = atributoEspecifico
                        )
                        else -> {
                            println("Error: Tipo de producto desconocido -> $tipo")
                            return@forEachLine
                        }
                    }

                    producto.mostrarDetalles()
                    println("Producto ${producto.titulo} cargado correctamente.")
                } else {
                    println("Error: Línea con formato incorrecto -> $line")
                }
            }
        }
    } catch (e: FileNotFoundException) {
        println("Error: El archivo no se puede leer.")
        return false
    } catch (e: Exception) {
        println("Error: Ocurrió un problema al leer el archivo TXT -> ${e.message}")
        return false
    }

    return true
}


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


fun cargarProductosDesdeJson(ruta: String): Boolean {
    val archivo = File(ruta)


    if (!archivo.exists()) {
        println("Error: El archivo no existe.")
        return false
    }

    try {
        val gson = Gson()

        val productosJson = gson.fromJson(archivo.reader(), List::class.java) as List<Map<String, Any>>


        productosJson.forEach { productoMap ->
            val tipo = productoMap["tipo"] as String
            val id = (productoMap["id"] as Double).toInt()
            val titulo = productoMap["titulo"] as String
            val descripcion = productoMap["descripcion"] as String
            val atributoEspecifico = productoMap["atributo"] as String
            val estado = EstadoProducto.valueOf(productoMap["estado"] as String)

            val producto: Producto = when (tipo) {
                "Videojuego" -> Videojuego(
                    id = id,
                    titulo = titulo,
                    descripcion = descripcion,
                    propietario_id = 1,
                    estado = estado,
                    plataforma = Plataforma.valueOf(atributoEspecifico)
                )
                "Libro" -> Libro(
                    id = id,
                    titulo = titulo,
                    descripcion = descripcion,
                    propietario_id = 1,
                    estado = estado,
                    autor = atributoEspecifico
                )
                else -> {
                    println("Error: Tipo de producto desconocido.")
                    return@forEach
                }
            }


            producto.mostrarDetalles()
            println("Producto ${producto.titulo} cargado correctamente.")
        }
    } catch (e: IOException) {
        println("Error: No se pudo leer el archivo JSON.")
        return false
    } catch (e: Exception) {
        println("Error: Formato de archivo incorrecto.")
        return false
    }

    return true
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

    val videojuegos = videojuegoSevice?.getAllByOwner(nombreUsuario)
    val libros = libroSevice?.getAllByOwner(nombreUsuario)

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