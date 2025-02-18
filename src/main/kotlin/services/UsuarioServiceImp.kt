package services

import entity.Usuario

interface UsuarioServiceImp {
    fun create(usuario: Usuario): Usuario?
    fun login(username: String, password: String): Usuario?
    fun getAll(): List<Usuario>
}