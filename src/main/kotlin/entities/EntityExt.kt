package com.kuraky.entities

import com.kuraky.api.Api
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType

/**
 * Establece la vida máxima de la entidad.
 * @param health Cantidad de vida (1 corazón = 2.0).
 * @param healToMax Si es true, cura a la entidad automáticamente a su nueva vida máxima.
 */
fun LivingEntity.setMaxHealth(health: Double, healToMax: Boolean = true) {
    this.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = health
    if (healToMax) {
        this.health = health
    }
}

/**
 * Establece la velocidad de movimiento de la entidad.
 * (El valor normal de un zombi es aprox 0.23).
 */
fun LivingEntity.setSpeed(speed: Double) {
    this.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = speed
}

/**
 * Establece el daño de ataque base de la entidad.
 */
fun LivingEntity.setAttackDamage(damage: Double) {
    this.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = damage
}

/**
 * Aplica un nombre personalizado formateado con colores de KuraCore.
 * @param name Nombre con códigos de color o Hex.
 * @param alwaysVisible Si el nombre se ve a través de las paredes/de lejos.
 */
fun LivingEntity.setKuraName(name: String, alwaysVisible: Boolean = true) {
    this.customName = Api.chat.format(name)
    this.isCustomNameVisible = alwaysVisible
}

/**
 * Vacía completamente el inventario/armadura de la entidad.
 * Útil para limpiar mobs antes de ponerles armadura custom.
 */
fun LivingEntity.clearEquipment() {
    this.equipment?.clear()
}

fun LivingEntity.setLootTable(tableId: String) {
    val key = NamespacedKey(Api.plugin, "kura_loot_table")
    this.persistentDataContainer.set(key, PersistentDataType.STRING, tableId)
}