# ⚙️ CONFIGURACIÓN RECOMENDADA PARA KURACORE

## 📋 Checklist de Configuración para Nuevos Usuarios

### Paso 1: Verificar Dependencias en build.gradle.kts

```kotlin
// ✅ Recomendado mantener
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // SQL - Solo si usas base de datos SQL
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.2")
    
    // MongoDB - Solo si usas MongoDB
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
    
    // Caché (recomendado)
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    
    // Corrutinas (para async/await)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

**Nota:** Si no usas SQL o MongoDB, puedes comentar esas dependencias para reducir tamaño del JAR.

---

### Paso 2: Configurar plugin.yml Correctamente

```yaml
name: "TuPlugin"
main: tu.paquete.TuPlugin
version: "1.0.0"
description: "Descripción breve de tu plugin"
authors: ["Tu Nombre"]
api-version: "1.20"

# IMPORTANTE: Declara las dependencias de KuraCore
depend:
  - KuraCore

# Opcional: Soft-depend si quieres que funcione sin KuraCore
#softdepend:
#  - KuraCore

# Comandos disponibles (opcional pero recomendado)
commands:
  ejemplo:
    description: "Mi primer comando"
    usage: "/ejemplo"
    permission: "tuplugin.ejemplo"
  menu:
    description: "Abre el menú"
    usage: "/menu"
    permission: "tuplugin.menu"

# Permisos (opcional pero recomendado)
permissions:
  tuplugin.*:
    description: "Acceso total a TuPlugin"
    children:
      tuplugin.ejemplo: true
      tuplugin.menu: true
  tuplugin.ejemplo:
    description: "Usar el comando /ejemplo"
    default: op
  tuplugin.menu:
    description: "Usar el comando /menu"
    default: true
```

---

### Paso 3: Estructura de Carpetas Recomendada

```
TuPlugin/
├── src/main/
│   ├── kotlin/
│   │   └── tu/paquete/
│   │       ├── TuPlugin.kt                 # Clase principal
│   │       ├── commands/
│   │       │   ├── AdminCommands.kt
│   │       │   └── UserCommands.kt
│   │       ├── listeners/
│   │       │   ├── PlayerListeners.kt
│   │       │   └── EntityListeners.kt
│   │       ├── menus/
│   │       │   ├── MainMenu.kt
│   │       │   └── SettingsMenu.kt
│   │       ├── database/
│   │       │   ├── UserDAO.kt
│   │       │   └── ItemDAO.kt
│   │       ├── config/
│   │       │   └── ConfigManager.kt
│   │       ├── utils/
│   │       │   ├── Extensions.kt
│   │       │   └── Constants.kt
│   │       └── tasks/
│   │           └── SaveTask.kt
│   └── resources/
│       ├── plugin.yml
│       ├── config.yml
│       ├── messages.yml
│       └── data/
│           └── (archivos de datos)
└── (resto de archivos de gradle)
```

---

### Paso 4: Clase Principal Recomendada

```kotlin
package tu.paquete

import org.bukkit.plugin.java.JavaPlugin
import com.kuraky.api.Api

class TuPlugin : JavaPlugin() {
    
    companion object {
        lateinit var instance: TuPlugin private set
    }
    
    override fun onEnable() {
        instance = this
        
        // 1. Inicializar KuraCore
        Api.init(this)
        logger.info("§a=== TuPlugin Habilitado ===")
        
        // 2. Cargar configuración
        try {
            ConfigManager.load()
            logger.info("§aConfiguración cargada correctamente")
        } catch (e: Exception) {
            logger.severe("§cError al cargar configuración: ${e.message}")
        }
        
        // 3. Registrar comandos
        try {
            Api.commands.registerPackage("tu.paquete.commands", debug = false)
            logger.info("§aComandos registrados")
        } catch (e: Exception) {
            logger.severe("§cError registrando comandos: ${e.message}")
        }
        
        // 4. Registrar listeners
        try {
            Api.events.register("tu.paquete.listeners", debug = false)
            logger.info("§aListeners registrados")
        } catch (e: Exception) {
            logger.severe("§cError registrando listeners: ${e.message}")
        }
        
        // 5. Inicializar tareas
        try {
            // Aquí registra tus tareas scheduladas
            logger.info("§aTareas iniciadas")
        } catch (e: Exception) {
            logger.severe("§cError iniciando tareas: ${e.message}")
        }
        
        logger.info("§a=== TuPlugin Listo (${Bukkit.getPluginManager().getPlugin("TuPlugin")?.description?.version}) ===")
    }
    
    override fun onDisable() {
        logger.info("§c=== TuPlugin Deshabilitado ===")
        
        // 1. Guardar datos
        try {
            ConfigManager.save()
        } catch (e: Exception) {
            logger.warning("§cError guardando configuración: ${e.message}")
        }
        
        // 2. Cerrar recursos
        try {
            Api.sql.close()
        } catch (e: Exception) {
            logger.warning("§cError cerrando SQL: ${e.message}")
        }
        
        try {
            Api.mongo.close()
        } catch (e: Exception) {
            logger.warning("§cError cerrando MongoDB: ${e.message}")
        }
        
        logger.info("§c=== TuPlugin Cerrado ===")
    }
}
```

---

### Paso 5: Gestor de Configuración Recomendado

```kotlin
package tu.paquete.config

import com.kuraky.api.Api
import org.bukkit.Bukkit

object ConfigManager {
    
    private val mainConfig by lazy { Api.config(Api.plugin, "config.yml") }
    private val messagesConfig by lazy { Api.config(Api.plugin, "messages.yml") }
    
    fun load() {
        // Cargar config principal
        val maxPlayers = mainConfig.getInt("server.max-players", 100)
        Bukkit.getLogger().info("Max players: $maxPlayers")
        
        // Mensajes
        val welcomeMsg = messagesConfig.getString("join.welcome", "§aWelcome!")
    }
    
    fun save() {
        mainConfig.save()
        messagesConfig.save()
    }
    
    // Acceso a valores
    fun getString(key: String, default: String): String {
        return mainConfig.getString(key, default)
    }
    
    fun getInt(key: String, default: Int): Int {
        return mainConfig.getInt(key, default)
    }
}
```

---

### Paso 6: Extensions de Utilidad

```kotlin
package tu.paquete.utils

import com.kuraky.api.Api
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// Extensión para enviar mensajes coloreados fácilmente
fun Player.sendColoredMessage(text: String) {
    this.sendMessage(Api.chat.color(text))
}

// Extensión para dar items
fun Player.giveItem(item: ItemStack) {
    if (this.inventory.firstEmpty() >= 0) {
        this.inventory.addItem(item)
    } else {
        this.world.dropItem(this.location, item)
    }
}

// Extensión para crear item fácilmente
fun Player.giveNamedItem(material: org.bukkit.Material, name: String) {
    val item = Api.item(material)
        .name(name)
        .build()
    this.giveItem(item)
}
```

---

## 🚨 PROBLEMAS COMUNES Y SOLUCIONES

### Problema: "ClassNotFoundException: com.kuraky.api.Api"
**Solución:** 
- Verifica que KuraCore.jar esté en `plugins/`
- Verifica que `depend: [KuraCore]` esté en plugin.yml
- Reinicia el servidor completo (no solo carga de plugin)

### Problema: "Command not registering"
**Solución:**
```kotlin
// MAL:
Api.commands.registerPackage("com.example", debug = true)

// BIEN: El paquete debe existir y contener clases con @Kuracmd
Api.commands.registerPackage("com.example.commands", debug = true)
```

### Problema: "Events not firing"
**Solución:**
```kotlin
// Asegúrate de que tu listener extends Listener
class MyListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) { }
}

// Y registra correctamente:
Api.events.register("com.example.listeners")
```

### Problema: "Memory leak en caché"
**Solución:**
```kotlin
// Usa expireMinutes apropiadamente
val cache = Api.createCache<String, Data>(
    expireMinutes = 30,  // ← Importante
    maxSize = 1000       // ← También importante
)
```

---

## ✨ TIPS DE PERFORMANCE

1. **Usa debug = false en producción**
   ```kotlin
   Api.commands.registerPackage("com.example.commands", debug = false)
   Api.events.register("com.example.listeners", debug = false)
   ```

2. **Implementa caché para datos frecuentes**
   ```kotlin
   private val userCache = Api.createCache<UUID, UserData>(expireMinutes = 30)
   ```

3. **Usa async para operaciones de BD**
   ```kotlin
   Bukkit.getScheduler().runTaskAsynchronously(plugin) {
       // Consulta a BD aquí
   }
   ```

4. **Limpia recursos en onDisable()**
   ```kotlin
   override fun onDisable() {
       Api.sql.close()
       Api.mongo.close()
   }
   ```

---

## 📦 VERSION RECOMENDADA

**KuraCore:** v1.1 (última estable)
**Paper/Spigot:** 1.20.4+
**Java:** 17+
**Kotlin:** 2.2.21+

---

**¿Necesitas ayuda? Consulta los otros archivos de documentación.** 📚

