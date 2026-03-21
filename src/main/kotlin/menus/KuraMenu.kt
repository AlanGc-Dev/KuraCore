package com.kuraky.menus

import com.kuraky.api.Api
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


/**
 * Clase base que representará todos los menús del servidor.
 * En la Fase 2 añadiremos la lógica de creación de botones e items.
 */

abstract class KuraMenu(private val title: String, private val rows: Int) {

    // Validamos que las filas sean correctas para Minecraft
    init {
        require(rows in 1..6) { "Un menú de Minecraft solo puede tener entre 1 y 6 filas (9 a 54 slots)."}
    }

    // Creamos el inventario nativo de Bukkit inyectando nuestro KuraHolder
    val inventory: Inventory = Bukkit.createInventory(
        KuraHolder(this),
        rows * 9,
        Api.chat.format(title)
    )

    // Diccionario donde guardaremos qué código ejecutar en cada slot
    protected val buttons = mutableMapOf<Int, (InventoryClickEvent) -> Unit>()

    /**
     * Coloca un ítem decorativo en el menú (No hace nada al hacer clic).
     *
     * @param slot El slot (0 a 53).
     * @param item El ItemStack a colocar.
     */

    fun setItem(slot: Int, item: ItemStack?) {
        inventory.setItem(slot, item)
        buttons.remove(slot)
    }

    /**
     * Coloca un botón interactivo en el menú.
     *
     * @param slot El slot (0 a 53).
     * @param item El ItemStack a colocar.
     * @param action El código (Lambda) que se ejecutará al hacer clic.
     */

    fun setButton(slot: Int, item: ItemStack?, action: (InventoryClickEvent) -> Unit) {
        inventory.setItem(slot, item)
        buttons[slot] = action
    }

    // --- SOPORTE NATIVO PARA CABEZAS (SKULLS) ---

    /**
     * Coloca un botón de cabeza usando una textura Base64.
     * Ideal para iconos customizados (flechas, monedas, cofres).
     *
     * @param slot El espacio en el inventario.
     * @param base64 El código de la textura.
     * @param name El nombre a mostrar del ítem.
     * @param lore Líneas de descripción (Opcional).
     * @param action Lo que sucede al hacer clic.
     */
    fun setBase64Button(
        slot: Int,
        base64: String,
        name: String,
        vararg lore: String,
        action: (InventoryClickEvent) -> Unit
    ) {
        val head = Api.item(org.bukkit.Material.PLAYER_HEAD)
            .name(name)
            .lore(*lore)
            .skullBase64(base64)
            .build()

        setButton(slot, head, action)
    }

    /**
     * Coloca un botón de cabeza usando el nombre de un jugador.
     * Ideal para menús de "Mi Perfil" o "Estadísticas".
     */
    fun setPlayerSkullButton(
        slot: Int,
        playerName: String,
        name: String,
        vararg lore: String,
        action: (InventoryClickEvent) -> Unit
    ) {
        val head = Api.item(org.bukkit.Material.PLAYER_HEAD)
            .name(name)
            .lore(*lore)
            .skullOwner(playerName)
            .build()

        setButton(slot, head, action)
    }

    /**
     * Coloca una cabeza decorativa Base64 (Sin botón/Sin acción).
     */
    fun setBase64Item(slot: Int, base64: String, name: String, vararg lore: String) {
        val head = Api.item(org.bukkit.Material.PLAYER_HEAD)
            .name(name)
            .lore(*lore)
            .skullBase64(base64)
            .build()

        setItem(slot, head)
    }

    /**
     * Llena los espacios vacíos del menú con un ítem decorativo (ej: Paneles de cristal).
     */

    fun fillEmpty(item: ItemStack?) {
        for (i in 0 until inventory.size) {
            if (inventory.getItem(i) == null) {
                setItem(i, item)
            }
        }
    }

    /**
     * Abre este menú a un jugador.
     */

    fun open(player: Player) {
        player.openInventory(inventory)
    }

    /**
     * Limpia todo el inventario y borra los botones registrados.
     * Útil para actualizar menús o cambiar de página.
     */

    fun clearMenu() {
        inventory.clear()
        buttons.clear()
    }

    // --- MÉTODOS INTERNOS LLAMADOS POR EL MenuListener ---

    open fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) return

        if (event.clickedInventory == inventory) {
            val action = buttons[event.slot]
            action?.invoke(event)
        }
    }

    // Dejamos estos abiertos por si un menú específico necesita lógica especial al arrastrar/cerrar
    open fun onDrag(event: InventoryDragEvent) {}
    open fun onClose(event: InventoryCloseEvent) {}

}