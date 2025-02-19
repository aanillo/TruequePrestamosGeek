package services

import dao.SolicitudDAO
import dao.SolicitudDAOH2
import entity.Producto
import entity.Solicitud

class SolicitudService(private val solicitudDAO: SolicitudDAOH2): SolicitudServiceImpl {

    override fun create(solicitud: Solicitud, idProducto: Int, tipoProducto: String): Solicitud? {
        return solicitudDAO.create(solicitud, idProducto, tipoProducto)
    }

    override fun getAll(): List<Solicitud> {
        return solicitudDAO.getAll()
    }

    override fun getFilteredSolicitudes(estadoFiltro: String, rolFiltro: Int, usuarioId: Int): List<Solicitud> {
        return solicitudDAO.getFilteredSolicitudes(estadoFiltro, rolFiltro, usuarioId)
    }

    override fun getSolicitudesPorUsuario(idUsuario: Int, estado: String?): List<Solicitud> {
        return solicitudDAO.getSolicitudesPorUsuario(idUsuario, estado)
    }

    override fun getSolicitudesAProductosDeUsuario(idUsuario: Int, estado: String?): List<Solicitud> {
        return solicitudDAO.getSolicitudesAProductosDeUsuario(idUsuario, estado)
    }

    override fun updateSolicitud(id: Int, estado: String): Solicitud? {
        return solicitudDAO.updateSolicitud(id, estado)
    }

    override fun getById(id: Int): Solicitud? {
        return solicitudDAO.getById(id)
    }
}