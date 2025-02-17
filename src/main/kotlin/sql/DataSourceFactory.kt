package sql

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javax.sql.DataSource

object DataSourceFactory {
    enum class DataSourceType {
        HIKARI,
        JDBC
    }

    private const val URL = "jdbc:h2:./default"
    private const val USUARIO = "user"
    private const val CONTRASEÑA = "user"

    fun getDS(dataSourceType: DataSourceFactory.DataSourceType): DataSource {
        try {
            when (dataSourceType) {
                DataSourceFactory.DataSourceType.HIKARI -> {
                    val config = HikariConfig()
                    config.jdbcUrl = "jdbc:h2:./default"
                    config.username = "user"
                    config.password = "user"
                    config.driverClassName = "org.h2.Driver"
                    config.maximumPoolSize = 10
                    config.isAutoCommit = true
                    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                    return HikariDataSource(config)
                }

                DataSourceFactory.DataSourceType.JDBC -> {
                    val jdbcUrl = "jdbc:h2:./default"
                    val username = "user"
                    val password = "user"
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
        var conexion: Connection? = null
        try {
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

