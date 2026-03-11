package com.kuraky.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class KuraConfig(private val plugin: Plugin, fileName: String) {

    private val name = if (fileName.endsWith(".yml")) fileName else "$fileName.yml"
    private val file  = File(plugin.dataFolder, name)
    private var config: FileConfiguration

    init {
        if (!file.exists()) {
            file.parentFile.mkdirs()

            if (plugin.getResource(name) != null) {
                plugin.saveResource(name, false)
            } else {
                file.createNewFile()
            }
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun get(): FileConfiguration {
        return config
    }

    fun save() {
        try {
            config.save(file)
        } catch (e: IOException) {
            plugin.logger.severe("¡No se pudo guardar el archivo $name!")
            e.printStackTrace()
        }
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
    }


}