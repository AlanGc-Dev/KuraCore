package com.kuraky.events

import com.google.common.reflect.ClassPath
import com.kuraky.api.Api
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class EventManagerV1 {
    @PublishedApi
    internal val dummyListener = object : Listener {}

    /**
     * Registra un evento en línea deduciendo el tipo a través del parámetro (e: NombreEvento).
     */
    inline fun <reified T : Event> event(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline action: (T) -> Unit
    ) {
        Bukkit.getPluginManager().registerEvent(
            T::class.java,
            dummyListener,
            priority,
            { _, event ->
                // Verificamos que el evento sea del tipo correcto y lo ejecutamos
                if (event is T) {
                    action(event)
                }
            },
            Api.plugin, // Usamos la instancia de tu core
            ignoreCancelled
        )
    }

    /**
     * Escanea un paquete y registra automáticamente todas las clases Listener.
     */
    fun register(packageName: String, debug: Boolean = false) {
        val pluginClassLoader = Api.plugin.javaClass.classLoader
        val classPath = ClassPath.from(pluginClassLoader)

        var listenersFound = 0

        for (classInfo in classPath.getTopLevelClassesRecursive(packageName)) {
            try {
                val clazz = Class.forName(classInfo.name, true, pluginClassLoader)

                // Verificamos que sea un Listener válido
                if (Listener::class.java.isAssignableFrom(clazz) && !clazz.isInterface && !clazz.isEnum) {
                    val instance = clazz.getDeclaredConstructor().newInstance() as Listener
                    Bukkit.getPluginManager().registerEvents(instance, Api.plugin)
                    listenersFound++

                    if (debug) Bukkit.getLogger().info("[KuraCore] -> Listener registrado: ${clazz.simpleName}")
                }
            } catch (e: Exception) {
                if (debug) Bukkit.getLogger().warning("[KuraCore] Error cargando Listener ${classInfo.name}: ${e.message}")
            }
        }

        if (debug) Bukkit.getLogger().info("[KuraCore] Se registraron $listenersFound clases de eventos en total.")
    }

    /**
     * Llama a un evento personalizado
     */
    fun call(event: Event) {
        Bukkit.getPluginManager().callEvent(event)
    }
}