package dao

import entity.EstadoProducto
import entity.Plataforma
import entity.Usuario
import entity.Videojuego
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

class VideojuegoDAOH2(private val dataSource: DataSource) : VideojuegoDAO {

    override fun create(videojuego: Videojuego): Videojuego? {
        val sql = "INSERT INTO videojuego (titulo, descripcion, propietario, estado, plataforma) VALUES (?, ?, ?, ?, ?)"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                    stmt.setString(1, videojuego.titulo)
                    stmt.setString(2, videojuego.descripcion)
                    stmt.setInt(3, videojuego.propietario_id)
                    stmt.setString(4, videojuego.estado.name)
                    stmt.setString(5, videojuego.plataforma.name)

                    val filaAfectada = stmt.executeUpdate()

                    if (filaAfectada == 1) {
                        stmt.generatedKeys.use { rs ->
                            if (rs.next()) {
                                videojuego.id = rs.getInt(1)
                            }
                        }
                        return videojuego
                    } else {
                        println("Error al insertar el videojuego.")
                        null
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al insertar videojuego: ${e.message}")
            null
        }
    }

    override fun getAllByTitle(titulo: String): List<Videojuego> {
        val sql = """
        SELECT v.id, v.titulo, v.descripcion, v.estado, v.plataforma, u.nombre AS propietario
        FROM videojuego v
        JOIN usuario u ON v.propietario = u.id
        WHERE v.titulo = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, titulo)
                    stmt.executeQuery().use { rs ->
                        val videojuegos = mutableListOf<Videojuego>()
                        while (rs.next()) {
                            val propietarioNombre = rs.getString("propietario")

                            videojuegos.add(
                                Videojuego(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario_id = -1,
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    plataforma = Plataforma.valueOf(rs.getString("plataforma")),
                                    propietario_nombre = propietarioNombre
                                )
                            )
                        }
                        videojuegos
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los videojuegos: ${e.message}")
            emptyList()
        }
    }

    override fun getAllByDesciption(descripcion: String): List<Videojuego> {
        val sql = """
        SELECT v.id, v.titulo, v.descripcion, v.estado, v.plataforma, u.nombre AS propietario
        FROM videojuego v
        JOIN usuario u ON v.propietario = u.id
        WHERE v.descripcion = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, descripcion)
                    stmt.executeQuery().use { rs ->
                        val videojuegos = mutableListOf<Videojuego>()
                        while (rs.next()) {
                            val propietarioNombre = rs.getString("propietario")

                            videojuegos.add(
                                Videojuego(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario_id = -1,
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    plataforma = Plataforma.valueOf(rs.getString("plataforma")),
                                    propietario_nombre = propietarioNombre
                                )
                            )
                        }
                        videojuegos
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los videojuegos: ${e.message}")
            emptyList()
        }
    }

    override fun getAllByOwner(propietario_id: Int): List<Videojuego> {
        val sql = """
        SELECT v.id, v.titulo, v.descripcion, v.estado, v.plataforma, u.id AS propietario_id
        FROM videojuego v
        JOIN usuario u ON v.propietario = u.id
        WHERE v.propietario_id = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, propietario_id)
                    stmt.executeQuery().use { rs ->
                        val videojuegos = mutableListOf<Videojuego>()
                        while (rs.next()) {
                            val propietarioId = rs.getInt("propietario_id")

                            videojuegos.add(
                                Videojuego(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario_id = propietarioId,
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    plataforma = Plataforma.valueOf(rs.getString("plataforma"))
                                )
                            )
                        }
                        videojuegos
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los videojuegos: ${e.message}")
            emptyList()
        }
    }

    override fun getAllByState(estado: String): List<Videojuego> {
        val sql = """
        SELECT v.id, v.titulo, v.descripcion, v.estado, v.plataforma, u.nombre AS propietario
        FROM videojuego v
        JOIN usuario u ON v.propietario = u.id
        WHERE v.estado = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, estado)
                    stmt.executeQuery().use { rs ->
                        val videojuegos = mutableListOf<Videojuego>()
                        while (rs.next()) {
                            val propietarioNombre = rs.getString("propietario")

                            videojuegos.add(
                                Videojuego(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario_id = -1,
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    plataforma = Plataforma.valueOf(rs.getString("plataforma")),
                                    propietario_nombre = propietarioNombre
                                )
                            )
                        }
                        videojuegos
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los videojuegos: ${e.message}")
            emptyList()
        }
    }

    override fun getAll(): List<Videojuego> {
        val sql = "SELECT * FROM videojuego"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        val videojuegos = mutableListOf<Videojuego>()
                        while (rs.next()) {
                            videojuegos.add(
                                Videojuego(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario_id = rs.getInt("propietario_id"),
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    plataforma = Plataforma.valueOf(rs.getString("plataforma"))
                                )
                            )
                        }
                        videojuegos
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los videojuegos: ${e.message}")
            emptyList()
        }
    }


}