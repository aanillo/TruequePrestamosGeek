package dao

import entity.Usuario
import org.mindrot.jbcrypt.BCrypt
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

class UsuarioDAOH2(private val dataSource: DataSource): UsuarioDAO {

    override fun create(usuario: Usuario): Usuario? {
        val sql = "INSERT INTO usuario (nombre, email, password) VALUES (?, ?, ?)"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                    val hashedPassword = BCrypt.hashpw(usuario.password, BCrypt.gensalt())
                    stmt.setString(1, usuario.nombre)
                    stmt.setString(2, usuario.email)
                    stmt.setString(3, hashedPassword)

                    val filaAfectada = stmt.executeUpdate()

                    if (filaAfectada == 1) {
                        stmt.generatedKeys.use { rs ->
                            if (rs.next()) {
                                usuario.id = rs.getInt(1)
                            }
                        }
                        return usuario
                    } else {
                        println("Error al insertar el usuario.")
                        null
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al insertar usuario: ${e.message}")
            null
        }
    }

    override fun getAll(): List<Usuario> {
        val sql = "SELECT * FROM usuario"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        val usuarios = mutableListOf<Usuario>()
                        while (rs.next()) {
                            usuarios.add(
                                Usuario(
                                    id = rs.getInt("id"),
                                    nombre = rs.getString("nombre"),
                                    email = rs.getString("email"),
                                    password = rs.getString("password")
                                )
                            )
                        }
                        usuarios
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los usuarios: ${e.message}")
            emptyList()
        }
    }


    override fun login(nombre: String, password: String): Usuario? {
        val sql = "SELECT * FROM usuario WHERE nombre = ? AND password = ?"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, nombre)
                    stmt.setString(2, password)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            Usuario(
                                id = rs.getInt("id"),
                                nombre = rs.getString("nombre"),
                                email = rs.getString("email"),
                                password = rs.getString("password")
                            )
                        } else {
                            null
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener el usuario: ${e.message}")
            null
        }
    }


    override fun update(usuario: Usuario): Usuario {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }


}