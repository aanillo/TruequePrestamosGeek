package entity

import com.sun.jdi.IntegerType

class Libro(
    titulo : String,
    descripcion : String,
    val autor : String
) : Producto(titulo, descripcion) {

    val id : Int

    companion object {
        private var contadorId = 0
    }

    init {
        id = ++contadorId
        require(autor.isNotBlank()) { "El autor no debe estar vacío. Por favor, introduce el nombre del autor." }
        require(autor.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+\$"))) { "El nombre del autor solo puede contener letras y espacios." }
    }
}