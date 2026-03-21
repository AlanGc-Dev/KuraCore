package com.kuraky.entities

import org.bukkit.inventory.ItemStack
import kotlin.random.Random


/**
 * Representa un ítem individual dentro de una tabla de botín.
 * @param item El ItemStack base a dropear.
 * @param chance Probabilidad de que caiga (0.0 a 100.0).
 * @param min Cantidad mínima a dropear.
 * @param max Cantidad máxima a dropear.
 */


data class LootItem(
    val item: ItemStack,
    val chance: Double,
    val min: Int = 1,
    val max: Int = 1
)

/**
 * Tabla de botín profesional para entidades o bloques.
 * @param id Identificador único de esta tabla (ej: "boss_zombi_rey").
 */

class KuraLootTable(val id: String) {

    private val lootItems = mutableListOf<LootItem>()

    fun addDrio(item: ItemStack, chance: Double, min: Int = 1, max: Int = 1): KuraLootTable {
        lootItems.add(LootItem(item, chance, min, max))
        return this
    }

    fun generateLoot(): List<ItemStack> {
        val generated = mutableListOf<ItemStack>()

        for(loot in lootItems) {
            val randomChance = Random.nextDouble(0.0, 100.0)

            if (randomChance <= loot.chance) {
                val drop = loot.item.clone()
                val amount = if (loot.min == loot.max) loot.min else Random.nextInt(loot.min, loot.max + 1)

                drop.amount = amount
                generated.add(drop)

            }
        }
        return generated

    }

}