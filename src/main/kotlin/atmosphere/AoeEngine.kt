package com.kuraky.atmosphere

import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect

object AoeEngine {/**
 * Aplica un efecto de poción a todas las entidades vivas en un radio.
 * @param excludePlayers Si es true, el efecto solo afectará a los Mobs, ignorando jugadores.
 */
fun applyPotion(center: Location, radius: Double, effect: PotionEffect, excludePlayers: Boolean = false) {
    val entities = center.world?.getNearbyLivingEntities(center, radius) ?: return

    for (entity in entities) {
        if (excludePlayers && entity is Player) continue
        entity.addPotionEffect(effect)
    }
}

    /**
     * Ejecuta código personalizado para cada entidad en un radio.
     * Útil para crear daño explosivo o empujes personalizados.
     */
    fun applyCustom(center: Location, radius: Double, action: (LivingEntity) -> Unit) {
        val entities = center.world?.getNearbyLivingEntities(center, radius) ?: return
        for (entity in entities) {
            action(entity)
        }
    }
}