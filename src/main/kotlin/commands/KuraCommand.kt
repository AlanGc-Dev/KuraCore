package com.kuraky.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class KuraCommand(
    name: String,
    aliases: List<String>,
    private val rootNode: CommandNode
) : Command(name) {
    init {
        this.aliases = aliases
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        return rootNode.execute(sender, args.toList().toTypedArray(), name)
    }
    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val completions = rootNode.getTableCompletions(args.toList().toTypedArray())
        return completions?.toMutableList() ?: super.tabComplete(sender, alias, args)
    }

}