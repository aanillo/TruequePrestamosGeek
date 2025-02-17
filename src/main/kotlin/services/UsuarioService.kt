package services

import entity.Usuario

interface UsuarioService {
    fun create(usuario: Usuario): Usuario?
    fun getById(id: Int): Usuario?
    fun update(usuario: Usuario): Usuario
    fun delete(id: Int)
    fun getAll(): List<Usuario>
}