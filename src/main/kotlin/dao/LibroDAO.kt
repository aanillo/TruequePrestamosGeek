package dao

import entity.Libro
import entity.Videojuego

interface LibroDAO{
    fun create(libro: Libro) : Libro?
    fun getAllByTitle(titulo: String) : List<Libro>
    fun getAllByDesciption(descripcion: String) : List<Libro>
    fun getAllByOwner(propietario: String): List<Libro>
    fun getAllByState(estado: String): List<Libro>
    fun getAll(): List<Libro>
}