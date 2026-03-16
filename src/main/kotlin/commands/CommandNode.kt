package com.kuraky.commands

import com.kuraky.api.Api
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.Console
import java.lang.reflect.Method

class CommandNode(val name: String) {

    var executorInstance: Any? = null
    var method: Method? = null
    var info: Kuracmd? = null
    val children = mutableMapOf<String, CommandNode>()

    val aliases = mutableSetOf<String>()

    fun execute(sender: CommandSender, args: Array<String>, currentPath: String): Boolean {
        if (args.isNotEmpty()) {
            val childName = args[0].lowercase()
            val childNode = children[childName]
            if (childNode != null) {
                return childNode.execute(sender, args.drop(1).toTypedArray(), "$currentPath $childName")
            }
        }

        if (method != null && executorInstance != null && info != null) {
            if (info!!.permissions.isNotEmpty() && !sender.hasPermission(info!!.permissions)) {
                sender.sendMessage("§cNo tienes permiso para ejecutar este comando.")
                return true
            }

            when (info!!.senderType) {
                SenderType.Player -> if (sender !is Player) {
                    sender.sendMessage("§cEste comando solo puede ser ejecutado por jugadores.")
                    return true
                }
                SenderType.Console -> if (sender is Player) {
                    sender.sendMessage("§cEste comando solo puede ser ejecutado desde la consola.")
                    return true
                }
                SenderType.Both -> {}
            }

            method!!.isAccessible = true
            method!!.invoke(executorInstance, sender, args)
            return true

        } else {
            sender.sendMessage("§cUso incorrecto. Subcomandos disponibles: §f${children.keys.joinToString(", ")}")
            return true
        }
    }

    fun getTableCompletions(args: Array<String>): List<String>? {
        if ( args.size == 1 ){
            val suggestions = children.keys.filter { it.startsWith(args[0], ignoreCase = true) }
            return if (suggestions.isEmpty() && children.isEmpty()) null else suggestions
        }

        val childName = args[0].lowercase()
        val childNode = children[childName]
        if (childNode != null){
            return childNode.getTableCompletions(args.drop(1).toTypedArray())
        }

        return if (children.isEmpty()) null else emptyList()
    }

}