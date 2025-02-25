package dao

import entity.Usuario

interface UsuarioDAO {
    fun create(usuario: Usuario) : Usuario?
    fun getAll(): List<Usuario>
    fun login(nombre: String, password: String) : Usuario?
    fun getByUsername(nombre: String): Usuario?
}