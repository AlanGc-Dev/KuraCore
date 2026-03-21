package com.kuraky.items

import com.destroystokyo.paper.profile.ProfileProperty
import com.google.common.io.BaseEncoding
import com.kuraky.api.Api
import org.apache.maven.artifact.versioning.Restriction
import org.apache.maven.model.Profile
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.UUID


/**
 * Constructor fluido y profesional para crear ItemStacks en Bukkit/Paper.
 *
 * Esta clase facilita la creación de ítems complejos mediante el encadenamiento
 * de métodos (Builder Pattern), integrando automáticamente el sistema de
 * formato de chat de KuraCore.
 *
 * @param material El tipo de material base del ítem.
 * @param amount La cantidad inicial de ítems (Por defecto 1).
 */
class ItemBuilder(material : Material, amount: Int = 1) {

    private val itemStack: ItemStack = ItemStack(material, amount)
    private val meta: ItemMeta? = itemStack.itemMeta

    /**
     * Establece el nombre a mostrar del ítem.
     * Soporta automáticamente códigos de color, Hex y gradientes de KuraCore.
     *
     * @param name El texto a mostrar.
     * @param smallCaps Si se debe aplicar la fuente SmallCaps al nombre.
     * @return La instancia actual de ItemBuilder.
     */

    fun name( name: String, smallCaps: Boolean = false): ItemBuilder {
        meta?.setDisplayName(Api.chat.format(name, smallCaps))
        return this
    }

    /**
     * Establece la descripción (Lore) del ítem.
     * Acepta múltiples líneas y aplica el formato de color de KuraCore a cada una.
     *
     * @param lines Líneas de texto para el lore.
     * @return La instancia actual de ItemBuilder.
     */

    fun lore(vararg lines: String): ItemBuilder {
        val coloredLore = lines.map { Api.chat.format(it) }
        meta?.lore = coloredLore
        return this
    }

    /**
     * Añade un encantamiento al ítem.
     *
     * @param enchantment El tipo de encantamiento.
     * @param level El nivel del encantamiento.
     * @param ignoreLevelRestriction Si es true, permite niveles por encima del máximo vanilla (ej: Filo 10).
     * @return La instancia actual de ItemBuilder.
     */

    fun enchant(enchantment: Enchantment, level: Int = 1, ignoreLevelRestriction: Boolean = true): ItemBuilder {
        meta?.addEnchant(enchantment, level, ignoreLevelRestriction)
        return this
    }


    /**
     * Oculta o muestra todos los atributos del ítem (Daño, Velocidad de ataque, etc).
     *
     * @param hidden True para ocultar, false para mostrar.
     * @return La instancia actual de ItemBuilder.
     */

    fun hideAtributes(hidden: Boolean = true): ItemBuilder {
        if (hidden) {
            meta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        } else {
            meta?.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }
        return this
    }

    /**
     * Oculta o muestra los encantamientos en el lore del ítem.
     * Muy útil para crear ítems "brillantes" sin mostrar el texto del encantamiento.
     *
     * @param hidden True para ocultar, false para mostrar.
     * @return La instancia actual de ItemBuilder.
     */

    fun hideEnchants(hidden: Boolean = true): ItemBuilder {
        if (hidden) {
            meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        } else {
            meta?.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
        return this
    }

    /**
     * Hace que el ítem brille (como si estuviera encantado) sin mostrar
     * ningún encantamiento real en su lore.
     *
     * @return La instancia actual de ItemBuilder.
     */

    fun glow(): ItemBuilder {
        enchant(Enchantment.DURABILITY, 1, true)
        hideAtributes(true)
        return this
    }

    /**
     * Asigna un CustomModelData para texturas personalizadas (Resource Packs).
     *
     * @param data El ID del modelo numérico.
     * @return La instancia actual de ItemBuilder.
     */

    fun customModelData(data: Int): ItemBuilder {
        meta?.setCustomModelData(data)
        return this
    }

    /**
     * Guarda un texto invisible (Tag/PDC) en el ítem.
     * Útil para identificar ítems únicos (ej: id_arma = "excalibur").
     */

    fun withTag(key: String, value: Int): ItemBuilder {
        val namespacedKey = NamespacedKey(Api.plugin, key)
        meta?.persistentDataContainer?.set(namespacedKey, PersistentDataType.INTEGER, value)
        return this
    }

    /**
     * Guarda un número decimal invisible.
     */

    fun withTag(key: String, value: Double): ItemBuilder {
        val namespacedKey = NamespacedKey(Api.plugin, key)
        meta?.persistentDataContainer?.set(namespacedKey, PersistentDataType.DOUBLE, value)
        return this
    }

    /**
     * Guarda un valor booleano (True/False) invisible.
     * Internamente se guarda como un Byte (1 = true, 0 = false) para máxima compatibilidad.
     * Útil para banderas lógicas (ej: es_intercambiable = false).
     */

    fun withTag(key: String, value: Boolean): ItemBuilder {
        val namespacedKey = NamespacedKey(Api.plugin, key)
        val byteBalue: Byte = if (value) 1 else 0
        meta?.persistentDataContainer?.set(namespacedKey, PersistentDataType.BYTE, byteBalue)
        return this
    }

    /**
     * Aplica una textura Base64 a una cabeza de jugador (PLAYER_HEAD).
     * Utiliza la API nativa de Paper Profile, evitando reflection.
     *
     * @param base64 El string en Base64 de la textura.
     */

    fun skullBase64(base64: String): ItemBuilder {
        if (meta is SkullMeta) {
            val profile = Bukkit.getServer().createProfile(UUID.randomUUID())
            profile.setProperty(ProfileProperty("textures", base64))
            meta.playerProfile = profile
        }
        return this
    }

    /**
     * Establece el dueño de una cabeza usando el nombre del jugador.
     * Útil para cabezas de estadísticas ("Estadísticas de Notch").
     */

    fun skullOwner(playerName: String): ItemBuilder {
        if (meta is SkullMeta) {
            meta.owningPlayer = Bukkit.getOfflinePlayer(playerName)
        }
        return this
    }

    /**
     * Tiñe una armadura de cuero o una poción con un color de Bukkit.
     */

    fun color (color: Color): ItemBuilder {
        when (meta) {
            is LeatherArmorMeta -> meta.setColor(color)
            is PotionMeta -> meta.color = color
        }
        return this
    }

    /**
     * Tiñe armaduras de cuero o pociones usando valores RGB directos.
     */

    fun color(red: Int, green: Int, blue: Int): ItemBuilder {
        return color(Color.fromRGB(red, green, blue))
    }

    /**
     * Tiñe armaduras de cuero o pociones usando un código Hexadecimal (ej: "#FF0000").
     */

    fun color(hex: String): ItemBuilder {
        val parsedHex = if (hex.startsWith("#")) hex else "#$hex"
        val javaColor = java.awt.Color.decode(parsedHex)
        return color(Color.fromRGB(javaColor.red, javaColor.green, javaColor.blue))
    }

    /**
     * Finaliza la construcción y devuelve el ItemStack listo para usar.
     *
     * @return ItemStack generado.
     */

    fun build(): ItemStack {
        itemStack.itemMeta = meta
        return itemStack
    }


}