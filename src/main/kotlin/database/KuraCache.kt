package com.kuraky.database

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

/**
 * Sistema de Caché en memoria de alto rendimiento para evitar consultas constantes a la base de datos.
 * @param K El tipo de la llave (Ej: String para UUID).
 * @param V El tipo del valor que vas a guardar (Ej: Map, PlayerData).
 */
class KuraCache<K : Any, V : Any>(
    expireAfterWriteMinutes: Long = 30, // Se borra de la memoria si no se usa en 30 mins
    maximumSize: Long = 2000 // Máximo de elementos en memoria para no saturar la RAM
) {
    private val cache: Cache<K, V> = Caffeine.newBuilder()
        .expireAfterWrite(expireAfterWriteMinutes, TimeUnit.MINUTES)
        .maximumSize(maximumSize)
        .build()

    /** Guarda un dato en la memoria RAM (No en la base de datos) */
    fun put(key: K, value: V) = cache.put(key, value)

    /** Obtiene un dato de la RAM. Retorna null si no existe o ya expiró */
    fun get(key: K): V? = cache.getIfPresent(key)

    /** Borra un dato específico de la RAM */
    fun invalidate(key: K) = cache.invalidate(key)

    /**
     * ¡LA MAGIA DEL CACHÉ!:
     * Intenta buscar el dato en la RAM. Si NO existe, ejecuta el código que le pases (lambda)
     * para buscarlo en la Base de Datos, lo guarda en la RAM automáticamente, y te lo devuelve.
     */
    fun getOrLoad(key: K, loader: (K) -> V?): V? {
        // Caffeine se encarga de todo el trabajo pesado aquí
        return cache.get(key) { k -> loader(k) }
    }
}