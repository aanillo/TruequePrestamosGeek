package entity

abstract class Producto (
    val titulo: String,
    val descripcion: String
) {

    init {
        require(titulo.isNotBlank()) { "El título del producto no puede estar vacío. Por favor, ingrese un título válido." }
        require(titulo.length < 50) { "El título del videojuego debe ser menos que 50 caracteres." }
        require(descripcion.isNotBlank()) { "La descripción no puede estar vacía. Por favor, proporcione una descripción detallada." }
    }
}