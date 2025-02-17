package entity

class Solicitud(
    var id : Int? = null,
    val tipoSolicitud : TipoSolicitud = TipoSolicitud.PENDIENTE,
    val solicitante : Usuario?,
    val producto : Producto?,
    val estado: EstadoSolicitud = EstadoSolicitud.PENDIENTE
) {

    init {
        requireNotNull(tipoSolicitud) { "El tipo de solicitud no puede ser nulo." }
        require(tipoSolicitud in TipoSolicitud.values()) { "El tipo de solicitud debe ser uno de los valores definidos en la enumeración (TRUEQUE, PRESTAMO)." }
        requireNotNull(solicitante) { "El solicitante no puede ser nulo." }
        requireNotNull(producto) { "El producto no puede ser nulo." }
        require(estado in EstadoSolicitud.values()) { "El estado debe ser uno de los valores válidos (PENDIENTE, ACEPTADA, RECHAZADA, FINALIZADA)." }
    }
}
