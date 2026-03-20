package com.kuraky

import com.kuraky.api.Api
import org.bukkit.plugin.java.JavaPlugin


class KuraCore : JavaPlugin() {

    override fun onEnable() {
        Api.init(this)
        logger.info("§aKuraCore ha sido habilitado. ¡API lista para otros plugins!")
    }

    override fun onDisable() {
        logger.info("§cKuraCore ha sido Desabilitado .")

    }


}