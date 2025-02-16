package entity

abstract class Producto(
    val titulo: String,
    val descripcion: String,
    val propietario: Usuario?
) {

    abstract fun mostrarDetalles()

}