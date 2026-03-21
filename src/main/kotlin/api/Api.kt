package com.kuraky.api

import com.kuraky.atmosphere.AoeEngine
import com.kuraky.atmosphere.ParticleEngine
import com.kuraky.atmosphere.SoundManager
import com.kuraky.chat.ChatManagerV1
import com.kuraky.commands.CommandManagerV1
import com.kuraky.commands.SenderType
import com.kuraky.config.KuraConfig
import com.kuraky.database.MongoManager
import com.kuraky.database.SqlManager
import com.kuraky.entities.LootManager
import com.kuraky.events.EventManagerV1
import com.kuraky.items.ItemBuilder
import jdk.jfr.DataAmount
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.plugin.Plugin

object Api {

    lateinit var plugin: Plugin private set

    val commands = CommandManagerV1()
    val events = EventManagerV1()

    val chat = ChatManagerV1()
    val display = com.kuraky.entities.KuraDisplay
    val loot = LootManager
    val sound = SoundManager
    val particles = ParticleEngine
    val aoe = AoeEngine

    val sql = SqlManager
    val mongo = MongoManager



    fun item(material: org.bukkit.Material, amount: Int = 1): com.kuraky.items.ItemBuilder {
        return com.kuraky.items.ItemBuilder(material, amount)
    }

    fun init (coreplugin: Plugin) {
        this.plugin = coreplugin
    }

    inline fun <reified T : Event> event(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline action: (T) -> Unit
    ) {
        events.event(priority, ignoreCancelled, action )
    }

    fun config(targetPlugin: Plugin, fileName: String): KuraConfig {
        return KuraConfig(targetPlugin, fileName)
    }

}