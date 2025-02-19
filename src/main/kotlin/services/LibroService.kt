package services

import dao.LibroDAO
import entity.Libro

class LibroService(private val libroDAO: LibroDAO): LibroServiceImpl {

    override fun create(libro: Libro): Libro? {
        return libroDAO.create(libro)
    }

    override fun getAllByTitle(titulo: String): List<Libro> {
        return libroDAO.getAllByTitle(titulo)
    }

    override fun getAllByDesciption(descripcion: String): List<Libro> {
        return libroDAO.getAllByDesciption(descripcion)
    }

    override fun getAllByOwner(propietarioNombre: String): List<Libro> {
        return libroDAO.getAllByOwner(propietarioNombre)
    }

    override fun getAllByState(estado: String): List<Libro> {
        return libroDAO.getAllByState(estado)
    }

    override fun getAll(): List<Libro> {
        return libroDAO.getAll()
    }

    override fun getById(id: Int): Libro? {
        return libroDAO.getById(id)
    }
}