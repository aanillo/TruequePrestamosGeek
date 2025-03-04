package dao

import entity.Usuario
import entity.Videojuego

interface VideojuegoDAO {
    fun create(videojuego: Videojuego) : Videojuego?
    fun getAllByTitle(titulo: String) : List<Videojuego>
    fun getAllByDesciption(descripcion: String) : List<Videojuego>
    fun getAllByOwner(propietarioNombre: String): List<Videojuego>
    fun getAllByState(estado: String): List<Videojuego>
    fun getAll(): List<Videojuego>
    fun getById(id: Int): Videojuego?
}