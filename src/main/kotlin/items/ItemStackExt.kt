package com.kuraky.items

import com.kuraky.api.Api
import com.kuraky.api.Api.plugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


/**
 * Obtiene un texto invisible (PDC) guardado en el ítem.
 * @return El texto guardado, o null si el ítem no tiene esa llave.
 */

fun ItemStack.getStringTag(key: String): String? {
    if (!this.hasItemMeta()) return null
    val namespacedKey = NamespacedKey(Api.plugin, key)
    return this.itemMeta?.persistentDataContainer?.get(namespacedKey, PersistentDataType.STRING)
}

/**
 * Obtiene un entero invisible (PDC) guardado en el ítem.
 */

fun ItemStack.getIntTag(key: String): Int? {
    if (!this.hasItemMeta()) return null
    val namespacedKey = NamespacedKey(Api.plugin, key)
    return this.itemMeta?.persistentDataContainer?.get(namespacedKey, PersistentDataType.INTEGER)
}

/**
 * Obtiene un booleano invisible (PDC) guardado en el ítem.
 */

fun ItemStack.getBooleanTag(key: String): Boolean {
    if (!this.hasItemMeta()) return false
    val namespacedKey = NamespacedKey(Api.plugin, key)
    val byteValue = this.itemMeta?.persistentDataContainer?.get(namespacedKey, PersistentDataType.BYTE) ?: return false
    return byteValue == 1.toByte()
}

/**
 * Verifica si el ítem tiene una llave específica guardada.
 */

fun ItemStack.hasTag(key: String): Boolean {
    if (!this.hasItemMeta()) return false
    val namespacedKey = NamespacedKey(Api.plugin, key)
    // Usamos el keys para ver si existe, sin importar el tipo de dato
    return this.itemMeta?.persistentDataContainer?.has(namespacedKey) ?: false
}