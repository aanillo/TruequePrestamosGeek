package entity

abstract class Producto(
    var id: Int? = null,
    val titulo: String,
    val descripcion: String,
    val propietario: Usuario?,
    val estado: EstadoProducto
) {

    abstract fun mostrarDetalles()

}