package entity

class Videojuego(
    titulo: String,
    descripcion: String,
    propietario: Usuario?,
    val plataforma: Plataforma
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
        require(plataforma in Plataforma.values()) { "Plataforma no válida. Debe ser XBOX, PLAYSTATION, NINTENDO o PC." }
    }

    override fun mostrarDetalles() {
        println("Videojuego: Id: $id, título: $titulo, propietario: $propietario, plataforma: $plataforma")
    }
}