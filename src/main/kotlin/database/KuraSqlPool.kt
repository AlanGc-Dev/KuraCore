package com.kuraky.database

import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

/**
 * Representa una conexión de base de datos aislada para un plugin específico.
 */
class KuraSqlPool(private val dataSource: HikariDataSource, val type: SqlType) {

    /**
     * Obtiene una conexión activa para hacer consultas.
     * Recuerda siempre cerrarla usando el bloque .use { } de Kotlin.
     */
    fun getConnection(): Connection {
        return dataSource.connection ?: throw IllegalStateException("¡No se pudo obtener la conexión del pool!")
    }

    /**
     * Apaga este pool de conexiones.
     * DEBE llamarse en el onDisable() del plugin que lo solicitó.
     */
    fun close() {
        if (!dataSource.isClosed) {
            dataSource.close()
        }
    }
}