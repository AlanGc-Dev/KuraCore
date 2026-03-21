package com.kuraky.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.Plugin
import java.io.File
import java.sql.Connection

enum class SqlType {
    SQLITE, MYSQL, POSTGRESQL
}

object SqlManager {

    /**
     * Crea y abre una nueva conexión de base de datos para un plugin.
     * * @param targetPlugin El plugin que solicita la base de datos.
     * @param type El tipo de base de datos (SQLITE, MYSQL, POSTGRESQL).
     * @return KuraSqlPool que el plugin deberá guardar y cerrar al apagarse.
     */
    fun connect(
        targetPlugin: Plugin,
        type: SqlType,
        host: String = "",
        port: Int = 3306,
        database: String = "database",
        user: String = "",
        pass: String = ""
    ): KuraSqlPool {

        val config = HikariConfig()

        when (type) {
            SqlType.SQLITE -> {
                // El archivo .db se guardará en la carpeta del plugin que lo pide (Ej: plugins/MiEconomia/database.db)
                val dbFile = File(targetPlugin.dataFolder, "$database.db")
                if (!dbFile.exists()) {
                    dbFile.parentFile.mkdirs()
                    dbFile.createNewFile()
                }
                config.jdbcUrl = "jdbc:sqlite:${dbFile.absolutePath}"
                config.driverClassName = "org.sqlite.JDBC"
                config.maximumPoolSize = 1 // SQLite no soporta conexiones múltiples de escritura
            }
            SqlType.MYSQL -> {
                config.jdbcUrl = "jdbc:mysql://$host:$port/$database?useSSL=false&autoReconnect=true"
                config.username = user
                config.password = pass
                config.driverClassName = "com.mysql.cj.jdbc.Driver"
                applyOptimizations(config)
            }
            SqlType.POSTGRESQL -> {
                config.jdbcUrl = "jdbc:postgresql://$host:$port/$database"
                config.username = user
                config.password = pass
                config.driverClassName = "org.postgresql.Driver"
                applyOptimizations(config)
            }
        }

        config.poolName = "${targetPlugin.name}-${type.name}-Pool"
        val dataSource = HikariDataSource(config)

        targetPlugin.logger.info("§a[KuraCore] Base de datos ${type.name} conectada exitosamente.")

        return KuraSqlPool(dataSource, type)
    }

    private fun applyOptimizations(config: HikariConfig) {
        config.maximumPoolSize = 10
        config.minimumIdle = 2
        config.connectionTimeout = 10000
        config.idleTimeout = 600000
        config.maxLifetime = 1800000
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    }
}