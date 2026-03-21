package com.kuraky.tasks

import com.kuraky.api.Api
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import kotlin.coroutines.CoroutineContext

/**
 * Dispatcher personalizado que obliga a la corrutina a ejecutarse en el HILO PRINCIPAL de Minecraft.
 * Seguro para modificar el mundo, abrir inventarios o spawnear entidades.
 */
val BukkitMainDispatcher = object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (Bukkit.isPrimaryThread()) {
            block.run() // Si ya estamos en el hilo principal, ejecutamos de inmediato
        } else {
            // Si venimos de un hilo asíncrono, le pedimos permiso a Bukkit para entrar al principal
            Bukkit.getScheduler().runTask(Api.plugin, block)
        }
    }
}

/**
 * Dispatcher personalizado para tareas asíncronas (Base de datos, HTTP, matemáticas pesadas).
 * NUNCA modificar el mundo desde aquí.
 */
val BukkitAsyncDispatcher = object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Api.plugin, block)
    }
}

/**
 * Motor central de Tareas de KuraCore. Reemplaza por completo a BukkitRunnable.
 */
object KuraTasks {
    // El "Scope" o espacio de trabajo donde vivirán nuestras corrutinas
    private val scope = CoroutineScope(SupervisorJob() + BukkitMainDispatcher)

    /** Ejecuta código en el hilo principal (Seguro para la API de Bukkit) */
    fun launchSync(block: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch(BukkitMainDispatcher, block = block)
    }

    /** Ejecuta código en segundo plano (Ideal para bases de datos o KuraRest) */
    fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch(BukkitAsyncDispatcher, block = block)
    }

    /** Tarea repetitiva (Timer) */
    fun timer(periodTicks: Long, delayTicks: Long = 0, async: Boolean = false, block: suspend CoroutineScope.() -> Unit): Job {
        val dispatcher = if (async) BukkitAsyncDispatcher else BukkitMainDispatcher
        return scope.launch(dispatcher) {
            delayTicks(delayTicks)
            while (isActive) {
                block()
                delayTicks(periodTicks)
            }
        }
    }
}

// --- EXTENSIONES ÚTILES ---

/** * Pausa la corrutina por una cantidad de Ticks de Minecraft (1 tick = 50ms).
 * No congela el servidor, solo pausa esta tarea específica.
 */
suspend fun delayTicks(ticks: Long) {
    delay(ticks * 50L)
}