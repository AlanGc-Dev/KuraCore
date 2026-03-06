package com.kuraky.events.v1

import com.kuraky.api.KuraAPI
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class EventManagerV1 {
    @PublishedApi
    internal val dummyListener = object : Listener {}

    inline fun <reified T : Event> listen(
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
            KuraAPI.plugin, // Usamos la instancia de tu core
            ignoreCancelled
        )
    }

}