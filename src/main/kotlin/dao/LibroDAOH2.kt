package dao

import entity.EstadoProducto
import entity.Libro
import entity.Plataforma
import entity.Videojuego
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

class LibroDAOH2(private val dataSource: DataSource): LibroDAO {

    override fun create(libro: Libro): Libro? {
        val sql = "INSERT INTO libro (titulo, descripcion, propietario_id, estado, autor) VALUES (?, ?, ?, ?, ?)"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                    stmt.setString(1, libro.titulo)
                    stmt.setString(2, libro.descripcion)
                    stmt.setInt(3, libro.propietario_id)
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
                        println("Error al insertar el libro.")
                        null
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al insertar libro: ${e.message}")
            null
        }
    }

    override fun getAllByTitle(titulo: String): List<Libro> {
        val sql = """
        SELECT l.id, l.titulo, l.descripcion, l.estado, l.autor, u.id AS propietario
        FROM libro l
        JOIN usuario u ON l.propietario_id = u.id
        WHERE l.titulo = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, titulo)
                    stmt.executeQuery().use { rs ->
                        val libros = mutableListOf<Libro>()
                        while (rs.next()) {
                            val propietarioNombre = rs.getString("propietario")

                            libros.add(
                                Libro(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario_id = -1,
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    autor = rs.getString("autor"),
                                    propietario_nombre = propietarioNombre
                                )
                            )
                        }
                        libros
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los libros: ${e.message}")
            emptyList()
        }
    }

    override fun getAllByDesciption(descripcion: String): List<Libro> {
        val sql = """
        SELECT l.id, l.titulo, l.descripcion, l.estado, l.autor, u.id AS propietario
        FROM libro l
        JOIN usuario u ON l.propietario_id = u.id
        WHERE l.descripcion = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, descripcion)
                    stmt.executeQuery().use { rs ->
                        val libros = mutableListOf<Libro>()
                        while (rs.next()) {
                            val propietarioNombre = rs.getString("propietario")

                            libros.add(
                                Libro(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario_id = -1,
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    autor = rs.getString("autor"),
                                    propietario_nombre = propietarioNombre
                                )
                            )
                        }
                        libros
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los libros: ${e.message}")
            emptyList()
        }
    }

    override fun getAllByOwner(propietarioNombre: String): List<Libro> {
        val sql = """
        SELECT l.id, l.titulo, l.descripcion, l.estado, l.autor, u.nombre AS propietario
        FROM libro l
        JOIN usuario u ON l.propietario_id = u.id
        WHERE u.nombre = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, propietarioNombre)
                    stmt.executeQuery().use { rs ->
                        val libros = mutableListOf<Libro>()
                        while (rs.next()) {
                            libros.add(
                                Libro(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario_id = -1,
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    autor = rs.getString("autor"),
                                    propietario_nombre = rs.getString("propietario")
                                )
                            )
                        }
                        libros
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los libros: ${e.message}")
            emptyList()
        }
    }

    override fun getAllByState(estado: String): List<Libro> {
        val sql = """
        SELECT l.id, l.titulo, l.descripcion, l.estado, l.autor, u.id AS propietario
        FROM libro l
        JOIN usuario u ON l.propietario_id = u.id
        WHERE l.estado = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, estado)
                    stmt.executeQuery().use { rs ->
                        val libros = mutableListOf<Libro>()
                        while (rs.next()) {
                            val propietarioNombre = rs.getString("propietario")

                            libros.add(
                                Libro(
                                    id = rs.getInt("id"),
                                    titulo = rs.getString("titulo"),
                                    descripcion = rs.getString("descripcion"),
                                    propietario_id = -1,
                                    estado = EstadoProducto.valueOf(rs.getString("estado")),
                                    autor = rs.getString("autor"),
                                    propietario_nombre = propietarioNombre
                                )
                            )
                        }
                        libros
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los libros: ${e.message}")
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
                                    propietario_id = rs.getInt("propietario_id"),
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
            println("Error al obtener los libros: ${e.message}")
            emptyList()
        }
    }

    override fun getById(id: Int): Libro? {
        val sql = """
        SELECT l.id, l.titulo, l.descripcion, l.estado, l.autor, u.id AS propietario
        FROM libro l
        JOIN usuario u ON l.propietario_id = u.id
        WHERE l.id = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            val propietarioNombre = rs.getString("propietario")
                            return Libro(
                                id = rs.getInt("id"),
                                titulo = rs.getString("titulo"),
                                descripcion = rs.getString("descripcion"),
                                propietario_id = -1,  // Si es necesario, cambia esto para asignar el valor correcto
                                estado = EstadoProducto.valueOf(rs.getString("estado")),
                                autor = rs.getString("autor"),
                                propietario_nombre = propietarioNombre
                            )
                        } else {
                            // No se encontró el videojuego con el id proporcionado
                            null
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener el libro por ID: ${e.message}")
            null
        }
    }
}