package entity

class Libro(
    id: Int? = null,
    titulo : String,
    descripcion : String,
    propietario_id: Int,
    estado: EstadoProducto = EstadoProducto.DISPONIBLE,
    val autor : String,
    val propietario_nombre: String? = null
) : Producto(id, titulo, descripcion, propietario_id, estado) {


    init {
        require(titulo.isNotBlank()) { "El título del producto no puede estar vacío. Por favor, ingrese un título válido." }
        require(titulo.length < 50) { "El título del videojuego debe ser menos que 50 caracteres." }
        require(descripcion.isNotBlank()) { "La descripción no puede estar vacía. Por favor, proporcione una descripción detallada." }
        require(estado in EstadoProducto.values()) { "El estado debe ser DISPONIBLE o PRESTADO"}
        require(autor.isNotBlank()) { "El autor no debe estar vacío. Por favor, introduce el nombre del autor." }
        require(autor.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+\$"))) { "El nombre del autor solo puede contener letras y espacios." }
    }

    override fun mostrarDetalles() {
        println("Libro: Id: $id, título: $titulo, propietario: ${propietario_nombre ?: "Desconocido"}, autor: $autor")
    }
}