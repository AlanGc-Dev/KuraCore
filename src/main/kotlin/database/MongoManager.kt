package com.kuraky.database

import com.mongodb.client.MongoClients
import org.bukkit.plugin.Plugin

object MongoManager {

    /**
     * Crea y abre una nueva conexión a MongoDB para un plugin.
     * * @param targetPlugin El plugin que solicita la base de datos.
     * @param uri La URI de conexión (Ej: "mongodb+srv://user:pass@cluster0...").
     * @param databaseName El nombre de la base de datos dentro del cluster.
     * @return KuraMongoPool que el plugin deberá guardar y cerrar al apagarse.
     */
    fun connect(targetPlugin: Plugin, uri: String, databaseName: String): KuraMongoPool {
        try {
            val mongoClient = MongoClients.create(uri)
            val database = mongoClient.getDatabase(databaseName)

            targetPlugin.logger.info("§a[KuraCore] Conexión a MongoDB establecida exitosamente.")

            return KuraMongoPool(mongoClient, database)
        } catch (e: Exception) {
            targetPlugin.logger.severe("§c[KuraCore] Error conectando a MongoDB: ${e.message}")
            throw e
        }
    }
}