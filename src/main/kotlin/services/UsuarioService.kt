package services

import dao.UsuarioDAOH2
import entity.Usuario

class UsuarioService(private val usuarioDAO: UsuarioDAOH2): UsuarioServiceImp {

    override fun create(usuario: Usuario): Usuario? {
        return usuarioDAO.create(usuario)
    }


    override fun login(username: String, password: String): Usuario? {
        return usuarioDAO.login(username, password)
    }

    override fun getAll(): List<Usuario> {
        return usuarioDAO.getAll()
    }

    override fun getByUsername(nombre: String): Usuario? {
        return usuarioDAO.getByUsername(nombre)
    }

}