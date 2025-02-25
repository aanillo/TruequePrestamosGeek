package controller

import dao.UsuarioDAOH2
import entity.Usuario
import services.UsuarioService
import sql.DataSourceFactory
import at.favre.lib.crypto.bcrypt.BCrypt

private val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)
private val usuarioDAO = dataSource?.let { UsuarioDAOH2(it) }
private val usuarioService = usuarioDAO?.let { UsuarioService(it) }

class UsuarioController {

    fun registrarUsuario() {
        println("\n=== Registro de Usuario ===")
        println("Por favor, introduzca el nombre:")
        val nombre = readln()
        println("Por favor, introduzca el correo:")
        val email = readln().trim()
        println("Por favor, ingrese una contraseña. Mínimo de 8 caracteres y debe contener un número y un carácter especial:")
        val password = readln().trim()

        try {
            val usuario = Usuario(nombre = nombre, email = email, password = password)
            val nuevoUsuario = usuarioService?.create(usuario)
            println("Usuario ${nuevoUsuario?.nombre} registrado correctamente.")
        } catch (e: IllegalArgumentException) {
            println("No se pudo agregar el usuario debido a un fallo interno. Inténtelo de nuevo más tarde")
        }
    }


    fun login(username: String, password: String): Usuario? {
        val usuario = usuarioService?.getByUsername(username)

        if (usuario != null) {
            return if (usuario.password.startsWith("$2a$") || usuario.password.startsWith("$2y$")) {
                val result = BCrypt.verifyer().verify(password.toCharArray(), usuario.password.toCharArray())
                if (result.verified) usuario else null
            } else {
                if (usuario.password == password) usuario else null
            }
        }
        return null
    }

}