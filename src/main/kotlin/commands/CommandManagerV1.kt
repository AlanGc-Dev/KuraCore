package com.kuraky.commands

import com.google.common.reflect.ClassPath
import com.kuraky.api.Api
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import java.lang.reflect.Method

class CommandManagerV1 {

    private val commandsMap: CommandMap by lazy {
        val server = Bukkit.getServer()
        val commandMapField = server.javaClass.getDeclaredField("commandMap")
        commandMapField.isAccessible = true
        commandMapField.get(server) as CommandMap
    }

    private val rootNodes = mutableMapOf<String, CommandNode>()

    fun registerPackage(packageName: String, debug: Boolean = false) {
        val pluginClassLoader = Api.plugin.javaClass.classLoader
        val classPath = ClassPath.from(pluginClassLoader)

        var commandsFound = 0

        for (classInfo in classPath.getTopLevelClassesRecursive(packageName)) {
            try {
                val clazz = Class.forName(classInfo.name, true, pluginClassLoader)
                var instance: Any? = null

                for (method in clazz.declaredMethods) {
                    val annotation = method.getAnnotation(Kuracmd::class.java)
                    if (annotation != null) {
                        if (instance == null) {
                            instance = clazz.getDeclaredConstructor().newInstance()
                        }

                        registerRoute(annotation, method, instance, debug)
                        commandsFound++
                    }
                }
            } catch (e: Exception) {
                if (debug) Bukkit.getLogger().warning("[KuraCore] Error cargando clase ${classInfo.name}: ${e.message}")
            }
        }

        // Inyectamos al Bukkit CommandMap pasando los alias recolectados
        for ((rootName, node) in rootNodes) {
            commandsMap.register("kuracore", KuraCommand(rootName, node.aliases.toList(), node))
            if (debug) Bukkit.getLogger().info("[KuraCore] Comando raíz '/$rootName' (Alias: ${node.aliases}) registrado.")
        }

        if (debug) Bukkit.getLogger().info("[KuraCore] Se registraron $commandsFound rutas de comandos en total.")
    }

    private fun registerRoute(info: Kuracmd, method: Method, instance: Any, debug: Boolean) {
        // 1. Unimos 'main' y 'sub', pasamos a minúsculas y limpiamos espacios dobles
        val rawPath = "${info.main} ${info.sub}".lowercase().trim()
        val fullPath = rawPath.replace(Regex("\\s+"), " ")
        val parts = fullPath.split(" ")

        if (parts.isEmpty() || parts[0].isEmpty()) return

        // 2. La primera palabra SIEMPRE es el comando raíz para Bukkit (ej: "eco")
        val rootName = parts[0]
        val rootNode = rootNodes.getOrPut(rootName) { CommandNode(rootName) }

        // 3. Agregamos los alias al comando raíz
        rootNode.aliases.addAll(info.aliases.map { it.lowercase().trim() })

        var currentNode = rootNode

        // 4. Construimos los nodos infinitos con el resto de las palabras
        for (i in 1 until parts.size) {
            val partName = parts[i]
            currentNode = currentNode.children.getOrPut(partName) { CommandNode(partName) }
        }

        // 5. Asignamos la ejecución al último nodo de la ruta
        currentNode.info = info
        currentNode.method = method
        currentNode.executorInstance = instance

        if (debug) Bukkit.getLogger().info("[KuraCore] -> Ruta montada: /$fullPath")
    }
}