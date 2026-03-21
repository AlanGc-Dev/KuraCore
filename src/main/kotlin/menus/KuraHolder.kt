package com.kuraky.menus

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder


/**
 * Holder personalizado para identificar los menús de KuraCore de forma segura.
 * Enlaza el inventario nativo de Bukkit con nuestra clase KuraMenu.
 * * @param menu La instancia del menú al que pertenece este inventario.
 */

class KuraHolder(val menu: KuraMenu) : InventoryHolder {

// Bukkit nos obliga a devolver el inventario, pero lo sacaremos de nuestro menú

    override fun getInventory(): Inventory {
        return menu.inventory
    }

}