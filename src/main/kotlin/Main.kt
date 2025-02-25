import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.google.gson.Gson
import controller.ProductoController
import controller.SolicitudController
import controller.UsuarioController
import dao.LibroDAOH2
import dao.SolicitudDAOH2
import dao.UsuarioDAOH2
import dao.VideojuegoDAOH2
import entity.*
import services.*
import sql.DataSourceFactory
import ui.Ui
import write.WriteProducto


private val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)
private val usuarioDAO = dataSource?.let { UsuarioDAOH2(it) }
private val usuarioService = usuarioDAO?.let { UsuarioService(it) }
private val videojuegoDAO = dataSource?.let { VideojuegoDAOH2(it) }
private val libroDAO = dataSource?.let { LibroDAOH2(it) }
private val solicitudDAO = dataSource?.let { SolicitudDAOH2(it) }
private val solicitudService = solicitudDAO?.let { SolicitudService(it) }
private val usuarioController = usuarioService?.let { UsuarioController() }
private val productoService = if (controller.libroDAO != null && controller.videojuegoDAO != null) {
    ProductoService(controller.libroDAO, controller.videojuegoDAO)
} else {
    null
}
private val productoController = productoService?.let { ProductoController() }
private val solicitudController = solicitudService?.let { SolicitudController() }
private val writer = productoService?.let { WriteProducto() }

fun main() {
    var usuarioLogueado: Usuario? = null

    println("Introduce nombre de usuario:")
    val usernameLogin = readln()
    println("Introduce contraseña:")
    val passwordLogin = readln()

    usuarioLogueado = usuarioController?.login(usernameLogin, passwordLogin)

    if (usuarioLogueado != null) {
        println("\n Bienvenido, ${usuarioLogueado.nombre}!")


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
                2 -> productoController?.agregarProducto(usuarioLogueado)
                3 -> productoController?.listarProducto(usuarioLogueado)
                4 -> solicitudController?.crearSolicitud(usuarioLogueado)
                5 -> solicitudController?.filtrarSolicitud(usuarioLogueado)
                6 -> solicitudController?.actualizarSolicitud(usuarioLogueado)
                7 -> {
                    println("Abriendo la interfaz gráfica...")
                    application {
                        Window(onCloseRequest = ::exitApplication) {
                            Ui().MyApp()
                        }
                    }
                }
                8 -> writer?.descargarProductos(usuarioLogueado)
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
