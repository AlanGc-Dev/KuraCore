package com.kuraky.config

import com.kuraky.api.Api
import com.kuraky.tasks.delayTicks
import java.io.File
import java.nio.file.*
import kotlin.io.path.name

/**
 * Observador de archivos. Detecta cuando guardas un archivo con Ctrl+S
 * y lo recarga en el servidor automáticamente.
 */
object ConfigWatcher {

    /**
     * Pone un archivo YAML bajo vigilancia.
     * @param file El archivo a vigilar (ej: config.file o lang.file)
     * @param onReload Bloque de código a ejecutar cuando se detecta un cambio (ej: config.reload())
     */
    fun watch(file: File, onReload: () -> Unit) {
        val path = file.parentFile.toPath()

        // Usamos nuestro motor de Corrutinas (Épica 5) para que vigile en segundo plano
        Api.tasks.launchAsync {
            val watchService = FileSystems.getDefault().newWatchService()
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)

            while (true) {
                val key = watchService.take()
                for (event in key.pollEvents()) {
                    val changedFile = event.context() as Path

                    if (changedFile.name == file.name) {
                        // Pequeño delay porque algunos editores de texto (como VSCode)
                        // disparan el evento de guardado dos veces muy rápido.
                        delayTicks(10)

                        // Volvemos al hilo principal para recargar con seguridad
                        Api.tasks.launchSync {
                            onReload()
                            Api.plugin.logger.info("§e[KuraCore] Archivo ${file.name} recargado en vivo.")
                        }
                    }
                }
                key.reset()
            }
        }
    }
}