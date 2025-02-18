package services

import dao.VideojuegoDAOH2
import entity.Videojuego

class VideojuegoService(private val videojuegoDAO: VideojuegoDAOH2): VideojuegoServiceImpl {

    override fun create(videojuego: Videojuego): Videojuego? {
        return videojuegoDAO.create(videojuego)
    }

    override fun getAllByTitle(titulo: String): List<Videojuego> {
        return videojuegoDAO.getAllByTitle(titulo)
    }

    override fun getAllByDesciption(descripcion: String): List<Videojuego> {
        return videojuegoDAO.getAllByDesciption(descripcion)
    }

    override fun getAllByOwner(propietario_id: Int): List<Videojuego> {
        TODO("Not yet implemented")
    }

    override fun getAllByState(estado: String): List<Videojuego> {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Videojuego> {
        TODO("Not yet implemented")
    }

}