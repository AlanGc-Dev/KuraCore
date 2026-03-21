package com.kuraky

import com.kuraky.api.Api
import com.kuraky.menus.MenuListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class KuraCore : JavaPlugin() {

    override fun onEnable() {
        Api.init(this)
        Bukkit.getPluginManager().registerEvents(MenuListener(), this)
        logger.info("§aKuraCore ha sido habilitado. ¡API lista para otros plugins!")
    }

    override fun onDisable() {
        logger.info("§cKuraCore ha sido Desabilitado .")
        Api.sql.close()
        Api.mongo.close()
        logger.info("§cKuraCore ha sido Deshabilitado.")

    }


}