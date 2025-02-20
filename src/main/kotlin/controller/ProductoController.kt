package controller

import dao.LibroDAOH2
import dao.UsuarioDAOH2
import dao.VideojuegoDAOH2
import services.LibroService
import services.UsuarioService
import services.VideojuegoService
import sql.DataSourceFactory

private val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)
private val videojuegoDAO = dataSource?.let { VideojuegoDAOH2(it) }
private val videojuegoSevice = videojuegoDAO?.let { VideojuegoService(it) }
private val libroDAO = dataSource?.let { LibroDAOH2(it) }
private val libroSevice = libroDAO?.let { LibroService(it) }

class ProductoController {

}