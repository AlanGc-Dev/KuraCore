package com.kuraky.api

import com.kuraky.atmosphere.AoeEngine
import com.kuraky.atmosphere.ParticleEngine
import com.kuraky.atmosphere.SoundManager
import com.kuraky.chat.ChatManagerV1
import com.kuraky.commands.CommandManagerV1
import com.kuraky.config.KuraConfig
import com.kuraky.database.KuraMongoOrm
import com.kuraky.database.KuraMongoPool
import com.kuraky.database.KuraOrm
import com.kuraky.database.KuraSqlPool
import com.kuraky.database.MongoManager
import com.kuraky.database.SqlManager
import com.kuraky.entities.LootManager
import com.kuraky.events.EventManagerV1
import com.kuraky.tasks.CooldownManager
import com.kuraky.tasks.KuraTasks
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.plugin.Plugin
import java.io.File

@Suppress("UNUSED")
object Api {

    lateinit var plugin: Plugin private set

    val commands = CommandManagerV1()
    val events = EventManagerV1()

    val chat = ChatManagerV1()
    val display = com.kuraky.entities.KuraDisplay
    val loot = LootManager
    val sound = SoundManager
    val particles = ParticleEngine
    val aoe = AoeEngine

    // Fábricas de conexiones
    val sql = SqlManager
    val mongo = MongoManager

    // --- VARIABLES GLOBALES DEL ORM ---
    lateinit var orm: KuraOrm private set
    lateinit var mongoOrm: KuraMongoOrm private set

    val rest = com.kuraky.network.KuraRest
    val tasks = KuraTasks
    val cooldowns = CooldownManager
    val base64 = com.kuraky.items.KuraSerializer

    /**
     * Crea un gestor de idiomas para un plugin y automáticamente lo pone bajo vigilancia (Auto-Reload).
     */
    fun lang(targetPlugin: Plugin, fileName: String = "lang.yml"): com.kuraky.chat.KuraLang {
        val lang = com.kuraky.chat.KuraLang(targetPlugin, fileName)

        // ¡Magia! Si alguien edita el lang.yml, se recarga solo.
        com.kuraky.config.ConfigWatcher.watch(File(targetPlugin.dataFolder, fileName)) {
            lang.reload()
        }

        return lang
    }
    // Crea un atajo elegante para crear Cachés en tus plugins
    fun <K : Any, V : Any> createCache(expireMinutes: Long = 30, maxSize: Long = 2000): com.kuraky.database.KuraCache<K, V> {
        return com.kuraky.database.KuraCache(expireMinutes, maxSize)
    }

    fun item(material: org.bukkit.Material, amount: Int = 1): com.kuraky.items.ItemBuilder {
        return com.kuraky.items.ItemBuilder(material, amount)
    }

    fun init(coreplugin: Plugin) {
        this.plugin = coreplugin
    }

    /**
     * Inicializa la base de datos SQL global para todos los plugins de la network.
     */
    fun setupGlobalSql(pool: KuraSqlPool) {
        this.orm = KuraOrm(pool)
    }

    /**
     * Inicializa la base de datos MongoDB global para todos los plugins de la network.
     */
    fun setupGlobalMongo(pool: KuraMongoPool) {
        this.mongoOrm = KuraMongoOrm(pool)
    }

    inline fun <reified T : Event> event(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline action: (T) -> Unit
    ) {
        events.event(priority, ignoreCancelled, action)
    }

    fun config(targetPlugin: Plugin, fileName: String): KuraConfig {
        return KuraConfig(targetPlugin, fileName)
    }
}