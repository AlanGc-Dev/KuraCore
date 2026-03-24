package com.kuraky.items

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64

/**
 * Utilidad para comprimir y descomprimir Ítems e Inventarios enteros a Texto (Base64).
 * Ideal para guardarlos en SQLite o MongoDB.
 */
object KuraSerializer {

    /** Convierte un solo ítem a texto */
    fun itemToBase64(item: ItemStack): String {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use { it.writeObject(item) }
        return Base64.getEncoder().encodeToString(outputStream.toByteArray())
    }

    /** Restaura un ítem desde un texto */
    fun itemFromBase64(base64: String): ItemStack? {
        return try {
            val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(base64))
            BukkitObjectInputStream(inputStream).use { it.readObject() as ItemStack }
        } catch (e: Exception) { null }
    }

    /** Convierte TODOS los ítems de un inventario a texto */
    fun inventoryToBase64(inventory: Inventory): String {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use { dataOutput ->
            dataOutput.writeInt(inventory.size)
            for (i in 0 until inventory.size) {
                dataOutput.writeObject(inventory.getItem(i))
            }
        }
        return Base64.getEncoder().encodeToString(outputStream.toByteArray())
    }

    /** Restaura un inventario entero a partir de un texto */
    fun inventoryFromBase64(base64: String, title: Component = Component.text("Inventario Restaurado")): Inventory? {
        return try {
            val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(base64))
            BukkitObjectInputStream(inputStream).use { dataInput ->
                val size = dataInput.readInt()
                val inventory = Bukkit.createInventory(null, size, title)
                for (i in 0 until size) {
                    inventory.setItem(i, dataInput.readObject() as? ItemStack)
                }
                inventory
            }
        } catch (e: Exception) { null }
    }
}