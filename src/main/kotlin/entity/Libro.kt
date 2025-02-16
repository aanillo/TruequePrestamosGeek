package entity

class Libro(
    titulo : String,
    descripcion : String,
    propietario: Usuario?,
    val autor : String
) : Producto(titulo, descripcion, propietario) {

    val id : Int

    companion object {
        private var contadorId = 0
    }

    init {
        id = ++contadorId
        require(titulo.isNotBlank()) { "El título del producto no puede estar vacío. Por favor, ingrese un título válido." }
        require(titulo.length < 50) { "El título del videojuego debe ser menos que 50 caracteres." }
        require(descripcion.isNotBlank()) { "La descripción no puede estar vacía. Por favor, proporcione una descripción detallada." }
        require(autor.isNotBlank()) { "El autor no debe estar vacío. Por favor, introduce el nombre del autor." }
        require(autor.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+\$"))) { "El nombre del autor solo puede contener letras y espacios." }
    }

    override fun mostrarDetalles() {
        println("Videojuego: Id: $id, título: $titulo, propietario: $propietario, autor: $autor")
    }
}