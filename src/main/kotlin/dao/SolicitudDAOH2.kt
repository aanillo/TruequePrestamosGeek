package dao

import entity.*
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

class SolicitudDAOH2(private val dataSource: DataSource): SolicitudDAO {

    override fun create(solicitud: Solicitud, idProducto: Int, tipoProducto: String): Solicitud? {
        val sql = "INSERT INTO solicitud (tipoSolicitud, solicitante_id, producto_id, producto_tipo, estado) VALUES (?, ?, ?, ?, ?)"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                    stmt.setString(1, solicitud.tipoSolicitud.name)
                    stmt.setInt(2, solicitud.solicitante_id)
                    stmt.setInt(3, idProducto)

                    val productoTipo = when (tipoProducto) {
                        "LIBRO" -> "LIBRO"
                        "VIDEOJUEGO" -> "VIDEOJUEGO"
                        else -> throw IllegalArgumentException("Tipo de producto desconocido.")
                    }

                    stmt.setString(4, productoTipo)
                    stmt.setString(5, solicitud.estado.name)

                    val filaAfectada = stmt.executeUpdate()

                    if (filaAfectada == 1) {
                        stmt.generatedKeys.use { rs ->
                            if (rs.next()) {
                                solicitud.id = rs.getInt(1)
                            }
                        }
                        return solicitud
                    } else {
                        println("Error al insertar la solicitud.")
                        null
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al insertar solicitud: ${e.message}")
            null
        }
    }

    override fun getAll(): List<Solicitud> {
        val sql = "SELECT * FROM solicitud"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        val solicitudes = mutableListOf<Solicitud>()
                        while (rs.next()) {
                            solicitudes.add(
                                Solicitud(
                                    id = rs.getInt("id"),
                                    tipoSolicitud = TipoSolicitud.valueOf(rs.getString("tipoSolicitud")),
                                    solicitante_id = rs.getInt("solicitante_id"),
                                    producto_id = rs.getInt("producto_id"),
                                    estado = EstadoSolicitud.valueOf(rs.getString("estado")),
                                    solicitante_nombre = rs.getString("solicitud_nombre"),
                                    producto_nombre = rs.getString("producto_nombre")
                                )
                            )
                        }
                        solicitudes
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener los videojuegos: ${e.message}")
            emptyList()
        }
    }


    override fun getFilteredSolicitudes(estadoFiltro: String, rolFiltro: Int, usuarioId: Int): List<Solicitud> {
        val sql = """
        SELECT s.id, s.tipoSolicitud, s.solicitante_id, s.producto_id, s.producto_tipo, s.estado,
               u.nombre AS solicitante_nombre, 
               CASE 
                   WHEN s.producto_tipo = 'LIBRO' THEN l.titulo 
                   WHEN s.producto_tipo = 'VIDEOJUEGO' THEN v.titulo 
                   ELSE 'DESCONOCIDO' 
               END AS producto_nombre
        FROM solicitud s
        JOIN usuario u ON s.solicitante_id = u.id
        LEFT JOIN libro l ON s.producto_id = l.id AND s.producto_tipo = 'LIBRO'
        LEFT JOIN videojuego v ON s.producto_id = v.id AND s.producto_tipo = 'VIDEOJUEGO'
        WHERE 
            (CASE 
                WHEN ? = 'A' THEN s.estado IN ('PENDIENTE', 'EN_PROCESO')
                WHEN ? = 'H' THEN s.estado IN ('FINALIZADA', 'RECHAZADA')
                ELSE FALSE 
            END)
            AND (CASE 
                WHEN ? = '1' THEN s.solicitante_id = ?
                WHEN ? = '2' THEN EXISTS (
                    SELECT 1 FROM producto p WHERE p.id = s.producto_id AND p.propietario_id = ?
                )
                ELSE FALSE
            END)
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, estadoFiltro)
                    stmt.setString(2, estadoFiltro)
                    stmt.setInt(3, rolFiltro)
                    stmt.setInt(4, usuarioId)
                    stmt.setInt(5, rolFiltro)
                    stmt.setInt(6, usuarioId)

                    stmt.executeQuery().use { rs ->
                        val solicitudes = mutableListOf<Solicitud>()
                        while (rs.next()) {
                            solicitudes.add(
                                Solicitud(
                                    id = rs.getInt("id"),
                                    tipoSolicitud = TipoSolicitud.valueOf(rs.getString("tipoSolicitud")),
                                    solicitante_id = rs.getInt("solicitante_id"),
                                    producto_id = rs.getInt("producto_id"),
                                    estado = EstadoSolicitud.valueOf(rs.getString("estado")),
                                    solicitante_nombre = rs.getString("solicitante_nombre"),
                                    producto_nombre = rs.getString("producto_nombre")
                                )
                            )
                        }
                        solicitudes
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al obtener las solicitudes filtradas: ${e.message}")
            emptyList()
        }
    }


    override fun getSolicitudesPorUsuario(idUsuario: Int, estado: String?): List<Solicitud> {
        val solicitudes = mutableListOf<Solicitud>()
        val query = if (estado != null) {
            """
    SELECT s.*, u.nombre AS solicitante_nombre, 
           CASE 
               WHEN s.producto_tipo = 'LIBRO' THEN l.titulo 
               WHEN s.producto_tipo = 'VIDEOJUEGO' THEN v.titulo 
               ELSE 'DESCONOCIDO' 
           END AS producto_nombre
    FROM solicitud s
    JOIN usuario u ON s.solicitante_id = u.id
    LEFT JOIN libro l ON s.producto_id = l.id AND s.producto_tipo = 'LIBRO'
    LEFT JOIN videojuego v ON s.producto_id = v.id AND s.producto_tipo = 'VIDEOJUEGO'
    WHERE s.solicitante_id = ? AND s.estado = ?
    """
        } else {
            """
    SELECT s.*, u.nombre AS solicitante_nombre, 
           CASE 
               WHEN s.producto_tipo = 'LIBRO' THEN l.titulo 
               WHEN s.producto_tipo = 'VIDEOJUEGO' THEN v.titulo 
               ELSE 'DESCONOCIDO' 
           END AS producto_nombre
    FROM solicitud s
    JOIN usuario u ON s.solicitante_id = u.id
    LEFT JOIN libro l ON s.producto_id = l.id AND s.producto_tipo = 'LIBRO'
    LEFT JOIN videojuego v ON s.producto_id = v.id AND s.producto_tipo = 'VIDEOJUEGO'
    WHERE s.solicitante_id = ?
    """
        }

        dataSource.connection.use { conn ->
            conn.prepareStatement(query).use { stmt ->
                stmt.setInt(1, idUsuario)
                if (estado != null) {
                    stmt.setString(2, estado)
                }
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    solicitudes.add(mapResultSetToSolicitud(rs))
                }
            }
        }
        return solicitudes
    }

    override fun getSolicitudesAProductosDeUsuario(idUsuario: Int, estado: String?): List<Solicitud> {
        val solicitudes = mutableListOf<Solicitud>()
        val query = if (estado != null) {
            """
        SELECT s.*, u.nombre AS solicitante_nombre, 
               CASE 
                   WHEN s.producto_tipo = 'LIBRO' THEN l.titulo 
                   WHEN s.producto_tipo = 'VIDEOJUEGO' THEN v.titulo 
                   ELSE 'DESCONOCIDO' 
               END AS producto_nombre
        FROM solicitud s
        JOIN usuario u ON s.solicitante_id = u.id
        LEFT JOIN libro l ON s.producto_id = l.id AND s.producto_tipo = 'LIBRO'
        LEFT JOIN videojuego v ON s.producto_id = v.id AND s.producto_tipo = 'VIDEOJUEGO'
        WHERE s.estado = ?
        AND (
            (s.producto_tipo = 'LIBRO' AND l.propietario_id = ?) OR
            (s.producto_tipo = 'VIDEOJUEGO' AND v.propietario_id = ?)
        )
        """
        } else {
            """
        SELECT s.*, u.nombre AS solicitante_nombre, 
               CASE 
                   WHEN s.producto_tipo = 'LIBRO' THEN l.titulo 
                   WHEN s.producto_tipo = 'VIDEOJUEGO' THEN v.titulo 
                   ELSE 'DESCONOCIDO' 
               END AS producto_nombre
        FROM solicitud s
        JOIN usuario u ON s.solicitante_id = u.id
        LEFT JOIN libro l ON s.producto_id = l.id AND s.producto_tipo = 'LIBRO'
        LEFT JOIN videojuego v ON s.producto_id = v.id AND s.producto_tipo = 'VIDEOJUEGO'
        WHERE 
        (s.producto_tipo = 'LIBRO' AND l.propietario_id = ?) OR
        (s.producto_tipo = 'VIDEOJUEGO' AND v.propietario_id = ?)
        """
        }

        dataSource.connection.use { conn ->
            conn.prepareStatement(query).use { stmt ->
                if (estado != null) {
                    stmt.setString(1, estado)
                    stmt.setInt(2, idUsuario)
                    stmt.setInt(3, idUsuario)
                } else {
                    stmt.setInt(1, idUsuario)
                    stmt.setInt(2, idUsuario)
                }

                val rs = stmt.executeQuery()
                while (rs.next()) {
                    solicitudes.add(mapResultSetToSolicitud(rs))
                }
            }
        }
        return solicitudes
    }

    override fun updateSolicitud(id: Int, estado: String?): Solicitud? {
        val query = """
        UPDATE solicitud 
        SET estado = ? 
        WHERE id = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, estado)
                    stmt.setInt(2, id)

                    val filasAfectadas = stmt.executeUpdate()

                    if (filasAfectadas > 0) {
                        return getById(id)
                    } else {
                        println("No se encontró ninguna solicitud con ID: $id")
                        null
                    }
                }
            }
        } catch (e: SQLException) {
            println("Error al actualizar la solicitud: ${e.message}")
            null
        }
    }


    override fun getById(id: Int): Solicitud? {
        val sql = """
        SELECT s.id, s.tipoSolicitud, s.solicitante_id, s.producto_id, s.producto_tipo, s.estado,
               u.nombre AS solicitante_nombre, 
               CASE 
                   WHEN s.producto_tipo = 'LIBRO' THEN l.titulo 
                   WHEN s.producto_tipo = 'VIDEOJUEGO' THEN v.titulo 
                   ELSE 'DESCONOCIDO' 
               END AS producto_nombre
        FROM solicitud s
        JOIN usuario u ON s.solicitante_id = u.id
        LEFT JOIN libro l ON s.producto_id = l.id AND s.producto_tipo = 'LIBRO'
        LEFT JOIN videojuego v ON s.producto_id = v.id AND s.producto_tipo = 'VIDEOJUEGO'
        WHERE s.id = ?
    """

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            return mapResultSetToSolicitud(rs)
                        }
                    }
                }
            }
            null
        } catch (e: SQLException) {
            println("Error al obtener la solicitud por ID: ${e.message}")
            null
        }
    }



    private fun mapResultSetToSolicitud(rs: ResultSet): Solicitud {
        return Solicitud(
            id = rs.getInt("id"),
            tipoSolicitud = TipoSolicitud.valueOf(rs.getString("tipoSolicitud")),
            solicitante_id = rs.getInt("solicitante_id"),
            producto_id = rs.getInt("producto_id"),
            estado = EstadoSolicitud.valueOf(rs.getString("estado")),
            solicitante_nombre = rs.getString("solicitante_nombre"),
            producto_nombre = rs.getString("producto_nombre")
        )
    }
}


