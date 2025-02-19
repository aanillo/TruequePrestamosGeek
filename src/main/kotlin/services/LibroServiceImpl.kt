package services

import entity.Libro
import entity.Videojuego

interface LibroServiceImpl {
    fun create(libro: Libro) : Libro?
    fun getAllByTitle(titulo: String) : List<Libro>
    fun getAllByDesciption(descripcion: String) : List<Libro>
    fun getAllByOwner(propietarioNombre: String): List<Libro>
    fun getAllByState(estado: String): List<Libro>
    fun getAll(): List<Libro>
    fun getById(id: Int): Libro?
}