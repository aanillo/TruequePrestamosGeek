package entity

class Videojuego(
    titulo : String,
    descripcion: String,
    val plataforma: Plataforma
) : Producto(titulo, descripcion) {

    val id : Int

    companion object {
        private var contadorId = 0
    }

    init {
        id = ++contadorId
        require(plataforma in Plataforma.entries.toTypedArray()) { "Plataforma no válida. Debe ser XBOX, PLAYSTATION, NINTENDO o PC." }
    }
}