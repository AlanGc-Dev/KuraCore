package com.kuraky.database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase

class KuraMongoPool(
    private val mongoClient: MongoClient,
    val database: MongoDatabase
) {

    /**
     * Cierra la conexión a MongoDB.
     * DEBE llamarse en el onDisable() del plugin que lo solicitó.
     */
    fun close() {
        try {
            mongoClient.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}