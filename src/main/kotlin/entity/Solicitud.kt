package entity

class Solicitud(
    var id : Int? = null,
    val tipoSolicitud : TipoSolicitud = TipoSolicitud.PENDIENTE,
    val solicitante_id: Int,
    val producto_id : Int,
    val estado: EstadoSolicitud = EstadoSolicitud.PENDIENTE,
    val solicitante_nombre: String? = null,
    val producto_nombre: String? = null
) {

    init {
        requireNotNull(tipoSolicitud) { "El tipo de solicitud no puede ser nulo." }
        require(tipoSolicitud in TipoSolicitud.values()) { "El tipo de solicitud debe ser uno de los valores definidos en la enumeración (TRUEQUE, PRESTAMO)." }
        require(estado in EstadoSolicitud.values()) { "El estado debe ser uno de los valores válidos (PENDIENTE, ACEPTADA, RECHAZADA, FINALIZADA)." }
    }

    override fun toString(): String {
        return "Solicitud(id=$id, tipoSolicitud=$tipoSolicitud, solicitante_id=$solicitante_id, producto=$producto_id, estado=$estado)"
    }


}
