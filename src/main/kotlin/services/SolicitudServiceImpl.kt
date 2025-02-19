package services

import entity.Producto
import entity.Solicitud

interface SolicitudServiceImpl {
    fun create(solicitud: Solicitud, idProducto: Int, tipoProducto: String): Solicitud?
    fun getAll(): List<Solicitud>
    fun getFilteredSolicitudes(estadoFiltro: String, rolFiltro: Int, usuarioId: Int): List<Solicitud>
    fun getSolicitudesPorUsuario(idUsuario: Int, estado: String?): List<Solicitud>
    fun getSolicitudesAProductosDeUsuario(idUsuario: Int, estado: String?): List<Solicitud>
    fun updateSolicitud(id: Int, estado: String): Solicitud?
    fun getById(id: Int): Solicitud?
}