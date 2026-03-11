package com.kuraky.commands

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender

class CommandManagerV1 {

    private val commandsMap: CommandMap by lazy {
        val server = Bukkit.getServer()
        val commandMapField = server.javaClass.getDeclaredField("commandMap")
        commandMapField.isAccessible = true
        commandMapField.get(server) as CommandMap
    }

    fun command (
        nameAndUsage: String,
        permission: String? = null,
        senderType: SenderType = SenderType.Both,
        action: (CommandSender, Array<out String>) -> Unit
    ) {
        val parts = nameAndUsage.trim().split(' ')
        val name = parts.first().lowercase()
        val usage = if (parts.size > 1) parts.drop(1).joinToString(" ") else ""

        val kuraCmd = KuraCommand(name, usage, permission, senderType, action)
        commandsMap.register("kuracore", kuraCmd)
    }



}