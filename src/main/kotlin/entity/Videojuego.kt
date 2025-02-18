package entity

class Videojuego(
    id: Int? = null,
    titulo: String,
    descripcion: String,
    propietario_id: Int,
    estado: EstadoProducto = EstadoProducto.DISPONIBLE,
    val plataforma: Plataforma,
    val propietario_nombre: String? = null
) : Producto(id, titulo, descripcion, propietario_id, estado) {


    init {
        require(titulo.isNotBlank()) { "El título del producto no puede estar vacío. Por favor, ingrese un título válido." }
        require(titulo.length < 50) { "El título del videojuego debe ser menos que 50 caracteres." }
        require(descripcion.isNotBlank()) { "La descripción no puede estar vacía. Por favor, proporcione una descripción detallada." }
        require(estado in EstadoProducto.values()) { "El estado debe ser DISPONIBLE o PRESTADO"}
        require(plataforma in Plataforma.values()) { "Plataforma no válida. Debe ser XBOX, PLAYSTATION, NINTENDO o PC." }
    }

    override fun mostrarDetalles() {
        println("Videojuego: Id: $id, Título: $titulo, Descripción: $descripcion, propietario: ${propietario_nombre ?: "Desconocido"}, Plataforma: $plataforma")
    }
}