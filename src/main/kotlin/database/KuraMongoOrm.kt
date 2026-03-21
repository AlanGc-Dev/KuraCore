package com.kuraky.database

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document

/**
 * ORM Dinámico y minimalista para MongoDB en KuraCore.
 */
class KuraMongoOrm(private val pool: KuraMongoPool) {

    /**
     * Inserta un nuevo documento en la colección.
     * Ejemplo: mongoOrm.insert("jugadores", "uuid" to "123", "nivel" to 1)
     */
    fun insert(collectionName: String, vararg data: Pair<String, Any>) {
        val collection = pool.database.getCollection(collectionName)
        val document = Document()
        data.forEach { document.append(it.first, it.second) }

        collection.insertOne(document)
    }

    /**
     * Obtiene UN documento y lo devuelve como un Diccionario (Map).
     */
    fun get(collectionName: String, vararg where: Pair<String, Any>): Map<String, Any>? {
        val collection = pool.database.getCollection(collectionName)

        // Construimos el filtro dinámicamente
        val filters = where.map { Filters.eq(it.first, it.second) }
        val finalFilter = if (filters.size > 1) Filters.and(filters) else filters.first()

        val document = collection.find(finalFilter).first() ?: return null

        // Convertimos el Document de Mongo a un Map normal de Kotlin
        return document.toMap() as Map<String, Any>
    }

    /**
     * Actualiza o crea un documento (Upsert).
     */
    fun update(collectionName: String, where: Pair<String, Any>, vararg data: Pair<String, Any>) {
        val collection = pool.database.getCollection(collectionName)
        val filter = Filters.eq(where.first, where.second)

        val updateDoc = Document()
        data.forEach { updateDoc.append(it.first, it.second) }

        // Usamos $set para actualizar solo esos campos sin borrar el resto del documento
        val setUpdate = Document("\$set", updateDoc)

        collection.updateOne(filter, setUpdate, UpdateOptions().upsert(true))
    }

    /**
     * Elimina un documento.
     */
    fun delete(collectionName: String, vararg where: Pair<String, Any>) {
        val collection = pool.database.getCollection(collectionName)
        val filters = where.map { Filters.eq(it.first, it.second) }
        val finalFilter = if (filters.size > 1) Filters.and(filters) else filters.first()

        collection.deleteOne(finalFilter)
    }
}