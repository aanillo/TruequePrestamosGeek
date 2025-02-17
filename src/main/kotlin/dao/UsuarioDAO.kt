package dao

import entity.Usuario

interface UsuarioDAO {
    fun create(usuario: Usuario) : Usuario?
    fun getAll(): List<Usuario>
    fun getById(id: Int): Usuario?
    fun login(nombre: String, password: String) : Usuario?
    fun update(usuario: Usuario): Usuario
    fun delete(id: Int)
}