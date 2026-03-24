# 🎮 KuraCore - Plugin Template Básico

Este es un template que puedes copiar y modificar para crear tu primer plugin con KuraCore.

## 📁 Estructura de Carpetas

```
TuPlugin/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
└── src/
    └── main/
        ├── kotlin/
        │   └── tu/
        │       └── paquete/
        │           ├── TuPlugin.kt
        │           ├── commands/
        │           │   └── Comandos.kt
        │           ├── listeners/
        │           │   └── Eventos.kt
        │           ├── menus/
        │           │   └── MiMenu.kt
        │           └── config/
        │               └── Config.kt
        └── resources/
            └── plugin.yml
```

---

## 📝 Archivos de Ejemplo

### `src/main/resources/plugin.yml`
```yaml
name: "TuPlugin"
main: tu.paquete.TuPlugin
version: "1.0.0"
description: "Mi primer plugin con KuraCore"
authors: ["Tu Nombre"]
api-version: "1.20"
depend:
  - KuraCore
```

### `build.gradle.kts`
```kotlin
plugins {
    kotlin("jvm") version "2.2.21"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "tu.paquete"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // KuraCore desde JitPack
    compileOnly("com.github.kuraky-oficial:KuraCore:v1.1")
}

kotlin {
    jvmToolchain(17)
}

tasks.build {
    dependsOn("shadowJar")
}
```

### `src/main/kotlin/tu/paquete/TuPlugin.kt`
```kotlin
package tu.paquete

import org.bukkit.plugin.java.JavaPlugin
import com.kuraky.api.Api

class TuPlugin : JavaPlugin() {
    
    override fun onEnable() {
        // Inicializar KuraCore
        Api.init(this)
        logger.info("§aHola desde TuPlugin")
        
        // Registrar comandos
        Api.commands.registerPackage("tu.paquete.commands", debug = true)
        
        // Registrar eventos
        Api.events.register("tu.paquete.listeners", debug = true)
        
        // Cargar configuración
        val config = Api.config(this, "config.yml")
        logger.info("§aConfig cargada: ${config.getInt("settings.nivel-maximo", 100)}")
    }
    
    override fun onDisable() {
        logger.info("§cTuPlugin deshabilitado")
    }
}
```

### `src/main/kotlin/tu/paquete/commands/Comandos.kt`
```kotlin
package tu.paquete.commands

import com.kuraky.api.Api
import com.kuraky.commands.Kuracmd
import com.kuraky.commands.SenderType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Comandos {
    
    @Kuracmd(
        main = "ejemplo",
        aliases = ["ej", "test"],
        permissions = "tuplugin.ejemplo",
        senderType = SenderType.Player
    )
    fun comandoEjemplo(sender: CommandSender, args: Array<String>) {
        if (sender is Player) {
            sender.sendMessage(Api.chat.color("&a¡Comando funcionando!"))
            sender.sendMessage(Api.chat.color("&#FF5733Tu nombre es: &b${sender.name}"))
        }
    }
    
    @Kuracmd(
        main = "menu",
        aliases = ["gui"],
        permissions = "tuplugin.menu"
    )
    fun abrirMenu(sender: CommandSender, args: Array<String>) {
        if (sender is Player) {
            sender.openInventory(MiPrimerMenu().inventory)
        }
    }
}
```

### `src/main/kotlin/tu/paquete/menus/MiMenu.kt`
```kotlin
package tu.paquete.menus

import com.kuraky.api.Api
import com.kuraky.menus.KuraMenu
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

class MiPrimerMenu : KuraMenu("&b&lMi Primer Menú", 3) {
    
    init {
        // Item decorativo
        val decoracion = Api.item(Material.STAINED_GLASS_PANE, 1)
            .name("&7")
            .build()
        
        // Rellenar los bordes
        for (i in 0..26) {
            if (i % 9 == 0 || i % 9 == 8 || i < 9 || i > 17) {
                setItem(i, decoracion)
            }
        }
        
        // Item interactivo
        val botonDiamante = Api.item(Material.DIAMOND)
            .name("&a&lPresiona aquí")
            .lore("&7Haz clic para ver un mensaje")
            .build()
        
        setButton(13, botonDiamante) { event: InventoryClickEvent ->
            event.whoClicked.sendMessage(Api.chat.color("&a¡Presionaste el botón!"))
            event.isCancelled = true
        }
    }
}
```

### `src/main/kotlin/tu/paquete/listeners/Eventos.kt`
```kotlin
package tu.paquete.listeners

import com.kuraky.api.Api
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class Eventos : Listener {
    
    @EventHandler
    fun alUnirse(event: PlayerJoinEvent) {
        val mensaje = Api.chat.format(
            "&a&lBienvenido &b${event.player.name}",
            useSmallCaps = true
        )
        event.player.sendMessage(mensaje)
    }
    
    @EventHandler
    fun alSalir(event: PlayerQuitEvent) {
        println("[TuPlugin] El jugador ${event.player.name} ha salido")
    }
}
```

### `src/main/kotlin/tu/paquete/config/Config.kt`
```kotlin
package tu.paquete.config

import com.kuraky.api.Api

object Config {
    
    private val config by lazy {
        Api.config(Api.plugin, "config.yml")
    }
    
    fun obtenerNivelMaximo(): Int {
        return config.getInt("settings.nivel-maximo", 100)
    }
    
    fun guardarNivel(uuid: String, nivel: Int) {
        config.set("jugadores.$uuid.nivel", nivel)
        config.save()
    }
    
    fun obtenerNivel(uuid: String): Int {
        return config.getInt("jugadores.$uuid.nivel", 1)
    }
}
```

---

## 🚀 Cómo Usar Este Template

1. **Copia los archivos** a tu nuevo proyecto
2. **Reemplaza `tu.paquete`** con tu paquete real (ej: `com.miservidor.miplugin`)
3. **Reemplaza `TuPlugin`** con el nombre de tu plugin
4. Ejecuta: `./gradlew build`
5. ¡Copia el JAR a tu servidor!

---

## ✅ Checklist

- [ ] Cambiaste el paquete en todos los archivos
- [ ] Actualizaste `plugin.yml` con tu nombre
- [ ] Corriste `./gradlew build` sin errores
- [ ] Copiaste el JAR a `plugins/`
- [ ] KuraCore está en `plugins/` también
- [ ] El servidor compila sin errores

---

## 🎉 ¡Éxito!

Tu primer plugin debería funcionar. Ahora:
- Añade más comandos
- Crea menús más complejos
- Integra bases de datos
- ¡Diviértete! 🚀

