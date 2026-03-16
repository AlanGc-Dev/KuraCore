package com.kuraky.api

import com.kuraky.chat.ChatManagerV1
import com.kuraky.commands.CommandManagerV1
import com.kuraky.commands.SenderType
import com.kuraky.config.KuraConfig
import com.kuraky.events.EventManagerV1
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.plugin.Plugin

object Api {

    lateinit var plugin: Plugin private set

    @PublishedApi internal var command = CommandManagerV1()
    @PublishedApi internal var events = EventManagerV1()

    val chat = ChatManagerV1()

    fun init (coreplugin: Plugin) {
        this.plugin = coreplugin
    }

    fun command(
        nameAndUsage: String,
        permission: String? = null,
        senderType: SenderType = SenderType.Both,
        action: (CommandSender, Array<out String>) -> Unit
    ) {
        command.command(nameAndUsage, permission, senderType, action)
    }

    inline fun <reified T : Event> event(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline action: (T) -> Unit
    ) {
        events.event(priority, ignoreCancelled, action)
    }

    fun config(targetPlugin: Plugin, fileName: String): KuraConfig {
        return KuraConfig(targetPlugin, fileName)
    }


}