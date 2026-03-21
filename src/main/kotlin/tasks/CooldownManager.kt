package com.kuraky.tasks

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Gestor en memoria de tiempos de espera (Cooldowns).
 */
object CooldownManager {

    // Mapa que guarda: NombreHabilidad -> (UUID Jugador -> Timestamp de Expiración)
    private val cooldowns = ConcurrentHashMap<String, ConcurrentHashMap<UUID, Long>>()

    /**
     * Aplica un cooldown a un jugador para una acción específica.
     */
    fun setCooldown(playerUuid: UUID, actionId: String, durationSeconds: Int) {
        cooldowns.putIfAbsent(actionId, ConcurrentHashMap())
        // Guardamos el tiempo exacto en el que este cooldown expirará
        cooldowns[actionId]!![playerUuid] = System.currentTimeMillis() + (durationSeconds * 1000L)
    }

    /**
     * Comprueba si el jugador aún tiene cooldown para esta acción.
     * Automáticamente limpia el dato de la memoria si ya expiró.
     */
    fun isOnCooldown(playerUuid: UUID, actionId: String): Boolean {
        val actionMap = cooldowns[actionId] ?: return false
        val expiryTime = actionMap[playerUuid] ?: return false

        if (System.currentTimeMillis() >= expiryTime) {
            actionMap.remove(playerUuid) // Limpieza automática para liberar RAM
            return false
        }
        return true
    }

    /**
     * Devuelve los segundos exactos que le faltan al jugador.
     */
    fun getRemainingSeconds(playerUuid: UUID, actionId: String): Int {
        val actionMap = cooldowns[actionId] ?: return 0
        val expiryTime = actionMap[playerUuid] ?: return 0

        val remainingMillis = expiryTime - System.currentTimeMillis()
        return if (remainingMillis > 0) (remainingMillis / 1000).toInt() + 1 else 0
    }
}