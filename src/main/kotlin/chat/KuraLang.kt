package com.kuraky.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File

/**
 * Gestor avanzado de Idiomas usando MiniMessage de Paper.
 */
class KuraLang(private val plugin: Plugin, private val fileName: String = "lang.yml") {

    private val file = File(plugin.dataFolder, fileName)
    private lateinit var config: YamlConfiguration
    private val mm = MiniMessage.miniMessage()

    init {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            plugin.saveResource(fileName, false)
        }
        reload()
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
    }

    /**
     * Obtiene un mensaje del lang.yml y lo parsea con colores, gradientes y variables.
     * Ejemplo: lang.get("mensajes.bienvenida", "jugador" to player.name)
     */
    fun get(path: String, vararg placeholders: Pair<String, String>): Component {
        val rawMessage = config.getString(path) ?: "<red>Error: Mensaje '$path' no encontrado en $fileName</red>"

        // Convertimos nuestras variables ("jugador" to "Alan") en Placeholders de MiniMessage (<jugador>)
        val resolvers = placeholders.map { Placeholder.parsed(it.first, it.second) }

        // Deserializamos el texto crudo a un Componente de Bukkit moderno
        return mm.deserialize(rawMessage, TagResolver.resolver(*resolvers.toTypedArray()))
    }

    /**
     * Obtiene una lista de mensajes (Para Lore de ítems o mensajes multilinea).
     */
    fun getList(path: String, vararg placeholders: Pair<String, String>): List<Component> {
        val rawList = config.getStringList(path)
        if (rawList.isEmpty()) return listOf(mm.deserialize("<red>Error: Lista '$path' vacía</red>"))

        val resolvers = placeholders.map { Placeholder.parsed(it.first, it.second) }
        return rawList.map { mm.deserialize(it, TagResolver.resolver(*resolvers.toTypedArray())) }
    }
}