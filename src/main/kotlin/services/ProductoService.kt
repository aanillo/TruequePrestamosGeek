package services

import dao.LibroDAOH2
import dao.VideojuegoDAOH2
import entity.Libro
import entity.Videojuego

class ProductoService(private val libroDAO: LibroDAOH2, private val videojuegoDAO: VideojuegoDAOH2): ProductoServiceImpl {
    override fun createVideogame(videojuego: Videojuego): Videojuego? {
        return videojuegoDAO.create(videojuego)
    }

    override fun getVideogamesByTitle(titulo: String): List<Videojuego> {
        return videojuegoDAO.getAllByTitle(titulo)
    }

    override fun getVideogamesByDesciption(descripcion: String): List<Videojuego> {
        return videojuegoDAO.getAllByTitle(descripcion)
    }

    override fun getVideogamesByOwner(propietarioNombre: String): List<Videojuego> {
        return videojuegoDAO.getAllByOwner(propietarioNombre)
    }

    override fun getVideogamesByState(estado: String): List<Videojuego> {
        return videojuegoDAO.getAllByTitle(estado)
    }

    override fun getAllVideogames(): List<Videojuego> {
        return videojuegoDAO.getAll()
    }

    override fun getVideogameById(id: Int): Videojuego? {
        return videojuegoDAO.getById(id)
    }

    override fun createBook(libro: Libro): Libro? {
        return libroDAO.create(libro)
    }

    override fun getBooksByTitle(titulo: String): List<Libro> {
        return libroDAO.getAllByTitle(titulo)
    }

    override fun getBooksByDesciption(descripcion: String): List<Libro> {
        return libroDAO.getAllByDesciption(descripcion)
    }

    override fun getBooksByOwner(propietarioNombre: String): List<Libro> {
        return libroDAO.getAllByOwner(propietarioNombre)
    }

    override fun getBooksByState(estado: String): List<Libro> {
        return libroDAO.getAllByState(estado)
    }

    override fun getAllBooks(): List<Libro> {
        return libroDAO.getAll()
    }

    override fun getBookById(id: Int): Libro? {
        return libroDAO.getById(id)
    }
}