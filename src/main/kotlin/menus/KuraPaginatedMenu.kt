package com.kuraky.menus

import com.kuraky.api.Api
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil


/**
 * Clase profesional para crear menús con múltiples páginas automáticamente.
 * * @param T El tipo de objeto que vamos a listar (Ej: Player, String, MiDatoSQL).
 * @param title Título base del menú.
 * @param rows Cantidad de filas (recomendado de 3 a 6).
 */

abstract class KuraPaginatedMenu<T>(title: String, private val rows: Int) : KuraMenu(title, rows) {

    protected var page = 0

    protected open val itemSlots : List<Int> = (0 until (rows - 1) * 9).toList()

    abstract fun getElements(): List<T>

    abstract fun formatElement(element: T): ItemStack

    abstract fun onClickElement(event: InventoryClickEvent, element: T)


    // --- MÉTODOS DE RENDERIZADO AUTOMÁTICO ---

    /**
     * Dibuja la página actual. Calcula los elementos y coloca los botones de navegación.
     */

    fun render() {
        clearMenu() // Borramos lo de la página anterior

        val elements = getElements()
        val maxItemsPerPage = itemSlots.size

        // Matemáticas para saber cuántas páginas hay en total
        val totalPages = ceil(elements.size.toDouble() / maxItemsPerPage).toInt()

        // Evitar que la página se salga de los límites
        if (page < 0) page = 0
        if (page >= totalPages && totalPages > 0) page = totalPages - 1

        // Calcular qué porción de la lista toca mostrar en esta página
        val startIndex = page * maxItemsPerPage
        val endIndex = minOf(startIndex + maxItemsPerPage, elements.size)
        val currentElements = if (elements.isEmpty()) emptyList() else elements.subList(startIndex, endIndex)

        // Colocar los elementos iterando sobre los slots disponibles
        for (i in currentElements.indices) {
            val element = currentElements[i]
            val slot = itemSlots[i]

            setButton(slot, formatElement(element)) { event ->
                onClickElement(event, element)
            }
        }

        // --- BOTONES DE NAVEGACIÓN (Última fila por defecto) ---
        val lastRowStart = (rows - 1) * 9

        // Botón "Anterior" (Solo si no estamos en la página 0)
        if (page > 0) {
            val prevItem = Api.item(Material.ARROW).name("&e⬅ Página Anterior").build()
            setButton(lastRowStart, prevItem) {
                page--
                render() // Recargar menú
            }
        }

        // Botón "Siguiente" (Solo si hay más elementos adelante)
        if (page < totalPages - 1) {
            val nextItem = Api.item(Material.ARROW).name("&ePágina Siguiente ➡").build()
            setButton(lastRowStart + 8, nextItem) {
                page++
                render() // Recargar menú
            }
        }

        // Aquí podrías agregar un método open decorativo, ej: paneles de cristal abajo
        decorateBottomBar(lastRowStart)
    }

    /**
     * Método opcional para decorar la barra inferior donde van los botones.
     */

    open fun decorateBottomBar(lastRowStart: Int) {
        val glass = Api.item(Material.BLACK_STAINED_GLASS_PANE).name(" ").build()
        for (i in 1..7) {
            setItem(lastRowStart + i, glass)
        }
    }

    /**
     * Sobrescribimos el open para asegurarnos de que siempre se renderice antes de abrir.
     */

    fun openPaginated(player: Player) {
        render()
        super.open(player)
    }


}