package sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.h2.jdbcx.JdbcDataSource
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javax.sql.DataSource

object DataSourceFactory {
    enum class DataSourceType {
        HIKARI,
        JDBC
    }

    private const val URL = "jdbc:h2:tcp://localhost/~/test"
    private const val USUARIO = "sa"
    private const val CONTRASEÑA = ""

    fun getDS(dataSourceType: DataSourceFactory.DataSourceType): DataSource? {
        try {
            when (dataSourceType) {
                DataSourceFactory.DataSourceType.HIKARI -> {
                    val config = HikariConfig()
                    // Configura la URL en modo servidor
                    config.jdbcUrl = "jdbc:h2:tcp://localhost/~/test"
                    config.username = "sa"
                    config.password = ""
                    config.driverClassName = "org.h2.Driver"
                    config.maximumPoolSize = 10
                    config.isAutoCommit = true
                    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                    return HikariDataSource(config)
                }

                DataSourceFactory.DataSourceType.JDBC -> {
                    val jdbcUrl = "jdbc:h2:tcp://localhost/~/test"  // Modo servidor
                    val username = "sa"
                    val password = ""
                    val dataSource = JdbcDataSource()
                    dataSource.setURL(jdbcUrl)
                    dataSource.user = username
                    dataSource.password = password
                    return dataSource
                }
            }
        } catch (_: SQLException) {
            return null
        }
    }

    fun getConnection(): Connection? {
        // Crear la carpeta ./database si no existe
        val databaseFolder = File("./database")
        if (!databaseFolder.exists()) {
            databaseFolder.mkdirs()  // Crea la carpeta si no existe
        }

        var conexion: Connection? = null
        try {
            // Aquí mantenemos la URL original (modo servidor) para acceder a la base de datos
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return conexion
    }

    fun closeBD(conexion: Connection?) {
        try {
            conexion?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}

