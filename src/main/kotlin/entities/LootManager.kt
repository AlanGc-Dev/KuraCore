package com.kuraky.entities

import com.kuraky.api.Api
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.persistence.PersistentDataType


/**
 * Gestor centralizado de Tablas de Botín.
 */


object LootManager : Listener {
    private val tables = mutableMapOf<String, KuraLootTable>()

    fun registerTable(table: KuraLootTable) {
        tables[table.id] = table
    }

    fun getTable(id: String): KuraLootTable? {
        return tables[id]
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity
        val key = NamespacedKey(Api.plugin, "kura_loot_table")

        if (entity.persistentDataContainer.has(key, PersistentDataType.STRING)) {
            val tableId = entity.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return

            val table = getTable(tableId) ?: return

            val customDrops = table.generateLoot()

            event.drops.addAll(customDrops)
        }
    }

}