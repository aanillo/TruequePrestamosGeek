package entity

class Solicitud(
    val tipoSolicitud : TipoSolicitud?,
    val solicitante : Usuario?,
    val producto : Producto?,
    val estado: Estado = Estado.PENDIENTE
) {
    val id: Int

    companion object {
        private var contadorId = 0
    }

    init {
        id = ++contadorId
        requireNotNull(tipoSolicitud) { "El tipo de solicitud no puede ser nulo." }
        require(tipoSolicitud in TipoSolicitud.values()) { "El tipo de solicitud debe ser uno de los valores definidos en la enumeración (TRUEQUE, PRESTAMO)." }
        requireNotNull(solicitante) { "El solicitante no puede ser nulo." }
        requireNotNull(producto) { "El producto no puede ser nulo." }
        require(estado in Estado.values()) { "El estado debe ser uno de los valores válidos (PENDIENTE, ACEPTADA, RECHAZADA, FINALIZADA)." }
    }
}
