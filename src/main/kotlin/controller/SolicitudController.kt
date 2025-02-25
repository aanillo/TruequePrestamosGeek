package controller

import dao.LibroDAOH2
import dao.SolicitudDAOH2
import dao.UsuarioDAOH2
import dao.VideojuegoDAOH2
import entity.EstadoSolicitud
import entity.Solicitud
import entity.TipoSolicitud
import entity.Usuario
import services.*
import sql.DataSourceFactory

private val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)
private val usuarioDAO = dataSource?.let { UsuarioDAOH2(it) }
private val usuarioService = usuarioDAO?.let { UsuarioService(it) }
val videojuegoDAO2 = dataSource?.let { VideojuegoDAOH2(it) }
val libroDAO2 = dataSource?.let { LibroDAOH2(it) }
private val solicitudDAO = dataSource?.let { SolicitudDAOH2(it) }
private val solicitudService = solicitudDAO?.let { SolicitudService(it) }
private val productoService = if (libroDAO2 != null && videojuegoDAO2 != null) {
    ProductoService(libroDAO2, videojuegoDAO2)
} else {
    null
}

class SolicitudController {

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
                "VIDEOJUEGO" -> productoService?.getVideogameById(idProducto)
                "LIBRO" -> productoService?.getBookById(idProducto)
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

        println("Ingrese el nuevo estado: \nACEPTADA \nRECHAZADA \nFINALIZADA")
        val respuesta = readln().trim().uppercase()

        if (solicitud.estado == EstadoSolicitud.PENDIENTE) {
            val estadoNuevo = when (respuesta) {
                "ACEPTADA" -> EstadoSolicitud.ACEPTADA
                "RECHAZADA" -> EstadoSolicitud.RECHAZADA
                else -> {
                    println("Error. El estado debe ser ACEPTADA, RECHAZADA para solicitudes pendientes o FINALIZADA para solicitudes aceptadas")
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
                "FINALIZADA" -> EstadoSolicitud.FINALIZADA
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

}