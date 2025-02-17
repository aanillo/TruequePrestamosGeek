package dao

import entity.*
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

class LibroDAOH2(private val dataSource: DataSource): LibroDAO {

    override fun create(libro: Libro): Libro? {
        val sql = "INSERT INTO libro (titulo, descripcion, propietario, estado, autor) VALUES (?, ?, ?, ?, ?)"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                    stmt.setString(1, libro.titulo)
                    stmt.setString(2, libro.descripcion)
                    stmt.setString(3, libro.propietario?.nombre)
                    stmt.setString(4, libro.estado.name)
                    stmt.setString(5, libro.autor)

                    val filaAfectada = stmt.executeUpdate()

                    if (filaAfectada == 1) {
                        stmt.generatedKeys.use { rs ->
                            if (rs.next()) {
                                libro.id = rs.getInt(1)
                            }
                        }
                        return libro
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

    override fun getAllByTitle(titulo: String): List<Libro> {
        val sql = "SELECT * FROM libro WHERE titulo = ?"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, titulo)
                    stmt.executeQuery().use { rs ->
                        val libros = mutableListOf<Libro>()
                        while (rs.next()) {
                            libros.add(
                                Libro(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario = Usuario(
                                        nombre = rs.getString("propietario"),
                                        email = "",
                                        password = ""
                                    ),
                                    estado = EstadoProducto.valueOf("estado"),
                                    autor = rs.getString("autor")
                                )
                            )
                        }
                        libros
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los usuarios: ${e.message}")
            emptyList()
        }
    }

    override fun getAllByDesciption(descripcion: String): List<Libro> {
        val sql = "SELECT * FROM libro WHERE descripcion = ?"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, descripcion)
                    stmt.executeQuery().use { rs ->
                        val libros = mutableListOf<Libro>()
                        while (rs.next()) {
                            libros.add(
                                Libro(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario = Usuario(
                                        nombre = rs.getString("propietario"),
                                        email = "",
                                        password = ""
                                    ),
                                    estado = EstadoProducto.valueOf("estado"),
                                    autor = rs.getString("autor")
                                )
                            )
                        }
                        libros
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los usuarios: ${e.message}")
            emptyList()
        }
    }

    override fun getAllByOwner(propietario: String): List<Libro> {
        val sql = """
        SELECT l.*, u.nombre AS propietario_nombre
        FROM libro v
        JOIN usuario u ON v.propietario_id = u.id
        WHERE u.nombre = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, propietario)
                    stmt.executeQuery().use { rs ->
                        val libros = mutableListOf<Libro>()
                        while (rs.next()) {
                            libros.add(
                                Libro(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario = Usuario(
                                        nombre = rs.getString("propietario_nombre"),
                                        email = "",
                                        password = ""
                                    ),
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    autor = rs.getString("autor")
                                )
                            )
                        }
                        libros
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los videojuegos: ${e.message}")
            emptyList()
        }
    }

    override fun getAllByState(estado: String): List<Libro> {
        val sql = "SELECT * FROM libro WHERE estado = ?"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, estado)
                    stmt.executeQuery().use { rs ->
                        val libros = mutableListOf<Libro>()
                        while (rs.next()) {
                            libros.add(
                                Libro(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario = Usuario(
                                        nombre = rs.getString("propietario"),
                                        email = "",
                                        password = ""
                                    ),
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    autor = rs.getString("resultado")
                                )
                            )
                        }
                        libros
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los videojuegos: ${e.message}")
            emptyList()
        }
    }

    override fun getAll(): List<Libro> {
        val sql = "SELECT * FROM libro"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        val libros = mutableListOf<Libro>()
                        while (rs.next()) {
                            libros.add(
                                Libro(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario = Usuario(
                                        nombre = rs.getString("propietario"),
                                        email = "",
                                        password = ""
                                    ),
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    autor = rs.getString("autor")
                                )
                            )
                        }
                        libros
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los usuarios: ${e.message}")
            emptyList()
        }
    }
}