package com.kuraky.entities

import com.kuraky.api.Api
import org.apache.commons.lang3.concurrent.BackgroundInitializer
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.ItemStack
import org.joml.Vector3f

object KuraDisplay {

    /**
     * Spawnea un holograma de texto (TextDisplay) moderno.
     * * @param location Ubicación exacta.
     * @param text El texto (soporta colores y Hex de KuraCore).
     * @param billboard Cómo mira al jugador (CENTER = Siempre mira al jugador).
     * @param scale Escala del texto (1.0 es el tamaño normal).
     * @param transparentBackground Si es true, quita el fondo negro semitransparente clásico del texto.
     */

    fun spawnText (
        location: Location,
        text: String,
        billboard: Billboard = Billboard.CENTER,
        scale: Float = 1.0f,
        transparentBackground: Boolean = true
    ): TextDisplay {
        return location.world.spawn(location, TextDisplay::class.java) { display ->
            display.text = Api.chat.format(text)
            display.billboard = billboard

            // Fondo transparente si se solicita (ARGB = 0 Alpha)
            if (transparentBackground) {
                display.backgroundColor = Color.fromARGB(0, 0, 0, 0)
            }

            // Aplicamos la escala usando Vectores JOML (Estándar 1.19.4+)
            val transform = display.transformation
            transform.scale.set(Vector3f(scale, scale, scale))
            display.transformation = transform
        }
    }

    /**
     * Spawnea un ítem flotante 3D (ItemDisplay).
     * Ideal para "Drops" visuales o decoración.
     */
    fun spawnItem(
        location: Location,
        item: ItemStack,
        transformType: ItemDisplayTransform = ItemDisplayTransform.GROUND,
        billboard: Billboard = Billboard.CENTER,
        scale: Float = 1.0f
    ): ItemDisplay {
        return location.world.spawn(location, ItemDisplay::class.java) { display ->
            display.itemStack = item
            display.itemDisplayTransform = transformType
            display.billboard = billboard

            val transform = display.transformation
            transform.scale.set(Vector3f(scale, scale, scale))
            display.transformation = transform
        }
    }


    // --- EXTENSIONES MÁGICAS PARA ANIMACIONES ---

    /**
     * Anima el tamaño de cualquier Display suavemente a lo largo del tiempo.
     * * @param targetScale El tamaño final al que debe llegar.
     * @param durationTicks Duración de la animación (20 ticks = 1 segundo).
     */
    fun Display.animateScale(targetScale: Float, durationTicks: Int) {
        this.interpolationDuration = durationTicks
        this.interpolationDelay = 0

        val transform = this.transformation
        transform.scale.set(Vector3f(targetScale, targetScale, targetScale))
        this.transformation = transform
    }

    /**
     * Mueve un Display suavemente desde su posición actual hacia una nueva coordenada relativa.
     * * @param addX Movimiento en el eje X.
     * @param addY Movimiento en el eje Y (ej. 1.0 para que flote hacia arriba).
     * @param addZ Movimiento en el eje Z.
     * @param durationTicks Duración de la animación.
     */
    fun Display.animateTranslation(addX: Float, addY: Float, addZ: Float, durationTicks: Int) {
        this.interpolationDuration = durationTicks
        this.interpolationDelay = 0

        val transform = this.transformation
        transform.translation.set(Vector3f(addX, addY, addZ))
        this.transformation = transform
    }

}