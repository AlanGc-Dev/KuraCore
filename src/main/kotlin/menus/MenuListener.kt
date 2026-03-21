package com.kuraky.menus

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import java.awt.Menu


/**
 * El motor central de eventos para los inventarios.
 * Atrapa los clics globales y los redirige inteligentemente a las clases KuraMenu.
 */

class MenuListener : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        // Obtenemos el holder del inventario superior (el menú que está abierto)
        val holder = event.inventory.holder

        // Si el holder es nuestro KuraHolder, ¡significa que es un menú nuestro!
        if (holder is KuraHolder) {
            // 1. Por seguridad EXTREMA, cancelamos el evento.
            // Así es imposible que los jugadores roben ítems del menú por algún bug.
            event.isCancelled = true

            // 2. Delegamos la acción a la clase del menú específico
            holder.menu.onClick(event)
        }
    }

    @EventHandler
    fun onMenuDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is KuraHolder) {

            event.isCancelled = true
            holder.menu.onDrag(event)
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {

        val holder = event.inventory.holder
        if (holder is KuraHolder) {
            holder.menu.onClose(event)
        }
    }
}