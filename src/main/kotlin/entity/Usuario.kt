package entity

class Usuario (
    val nombre: String,
    val email: String,
    val contraseña: String) {

    val id: Int

    companion object {
        private var contadorId = 0
    }


    init {
       id = ++contadorId
       require(nombre.isNotBlank()) { "El nombre no puede estar vacío." }
       require(email.isNotBlank()) { "El email no puede estar vacío." }
       require(email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) { "El email no tiene un formato válido." }
       require(contraseña.isNotBlank()) { "La contraseña no puede estar vacía. Por favor, ingrese una contraseña segura." }
       require(contraseña.length >= 8) { "La contraseña debe tener al menos 8 caracteres." }
       require(contraseña.any { it.isDigit() }) { "La contraseña debe contener al menos un número." }
       require(contraseña.any { it.isUpperCase() }) { "La contraseña debe contener al menos una letra mayúscula." }
   }

    override fun toString(): String {
        return "Usuario(nombre='$nombre', email='$email', contraseña='$contraseña')"
    }


}