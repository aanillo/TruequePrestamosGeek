package entity

data class Usuario (
    var id: Int? = null,
    val nombre: String,
    val email: String,
    val password: String) {


    init {
       require(nombre.isNotBlank()) { "El nombre no puede estar vacío." }
       require(email.isNotBlank()) { "El email no puede estar vacío." }
       require(email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) { "El email no tiene un formato válido." }
        if (nombre != "admin") {
            require(password.isNotBlank()) { "La contraseña no puede estar vacía. Por favor, ingrese una contraseña segura." }
            require(password.length >= 6) { "La contraseña debe tener al menos 6 caracteres." }
            require(password.any { it.isDigit() }) { "La contraseña debe contener al menos un número." }
            require(password.any { it.isUpperCase() }) { "La contraseña debe contener al menos una letra mayúscula." }
        }
   }

    override fun toString(): String {
        return "Usuario(id='$id' nombre='$nombre', email='$email', contraseña='$password')"
    }


}