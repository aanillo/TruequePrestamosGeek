package services

import entity.Libro
import entity.Videojuego

interface ProductoServiceImpl {
    fun createVideogame(videojuego: Videojuego) : Videojuego?
    fun getVideogamesByTitle(titulo: String) : List<Videojuego>
    fun getVideogamesByDesciption(descripcion: String) : List<Videojuego>
    fun getVideogamesByOwner(propietarioNombre: String): List<Videojuego>
    fun getVideogamesByState(estado: String): List<Videojuego>
    fun getAllVideogames(): List<Videojuego>
    fun getVideogameById(id: Int): Videojuego?
    fun createBook(libro: Libro) : Libro?
    fun getBooksByTitle(titulo: String) : List<Libro>
    fun getBooksByDesciption(descripcion: String) : List<Libro>
    fun getBooksByOwner(propietarioNombre: String): List<Libro>
    fun getBooksByState(estado: String): List<Libro>
    fun getAllBooks(): List<Libro>
    fun getBookById(id: Int): Libro?
}