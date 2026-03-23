# 🌌 KuraCore

![Kotlin](https://img.shields.io/badge/Kotlin-17-orange)
![Minecraft](https://img.shields.io/badge/Minecraft-API-green)
![Version](https://img.shields.io/badge/version-1.1-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

**Lightweight core library for Kuraky Studios plugins.**

KuraCore provides a simple API for commands, events, configuration management, and chat formatting, allowing developers to build Minecraft plugins faster with cleaner code. Designed for modular plugin ecosystems.

---

## ✨ Features

* **⚡ Simple command registration:** Forget about boilerplate code.
* **🎯 Easy event registration:** Listen to Bukkit events in seconds.
* **📁 Automatic configuration loader:** Manage multiple `.yml` files effortlessly.
* **💬 Chat formatting utilities:** Built-in Hex and Gradient support.
* **🔠 SmallCaps text formatting:** Aesthetic text generation on the fly.
* **🧩 Lightweight modular API:** Everything accessible through `Api`.
* **🚀 Designed for scalable plugin ecosystems:** Build your network faster.
* **💾 Database integration:** Support for MongoDB and SQL databases.
* **🎨 Item and menu builders:** Easy creation of custom items and GUIs.
* **🌐 Network utilities:** REST API integration.
* **🎵 Atmosphere effects:** Particle, sound, and AoE engines.
* **⏰ Task management:** Cooldowns and scheduled tasks.

---

## 🏗️ Architecture

KuraCore is organized into modular packages for easy integration:

- **api/**: Core API access point.
- **atmosphere/**: Particle effects, sounds, and area-of-effect engines.
- **chat/**: Chat formatting and language management.
- **commands/**: Command registration and management.
- **config/**: Configuration file handling and watchers.
- **database/**: ORM for MongoDB and SQL, caching, and connection pools.
- **entities/**: Entity extensions, displays, and loot tables.
- **events/**: Event registration system.
- **items/**: Item builders and serializers.
- **menus/**: GUI menus and paginated menus.
- **network/**: REST API client.
- **tasks/**: Cooldown management and task scheduling.

---

## 📦 Installation

**1. Add JitPack to your repositories:**
```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

**2. Add the dependency:**
```kotlin
dependencies {
    compileOnly("com.github.kuraky-oficial:KuraCore:v1.0.1")
}
```

**3. Build your plugin:**
```bash
./gradlew build
```

---

## 🚀 API Usage

### ⌨️ Register Commands
KuraCore uses annotations to register commands. Create a class with annotated methods and register the package.

**Command Class Example:**
```kotlin
package com.example.commands

import com.kuraky.api.Api
import com.kuraky.commands.Kuracmd
import com.kuraky.commands.SenderType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MyCommands {

    @Kuracmd(main = "hello", aliases = ["hi"], permissions = "myplugin.hello", senderType = SenderType.Player)
    fun hello(sender: CommandSender, args: Array<String>) {
        sender.sendMessage(Api.chat.color("&aHello from KuraCore!"))
    }

    @Kuracmd(main = "teleport", sub = "here", permissions = "myplugin.tp")
    fun teleportHere(sender: CommandSender, args: Array<String>) {
        if (sender is Player && args.isNotEmpty()) {
            val target = sender.server.getPlayer(args[0])
            target?.teleport(sender.location)
        }
    }
}
```

**Registration in Plugin:**
```kotlin
class ExamplePlugin : JavaPlugin() {
    override fun onEnable() {
        Api.commands.registerPackage("com.example.commands", debug = true)
    }
}
```

### 🎯 Register Events
KuraCore supports inline event registration or scanning listener classes.

**Inline Event Registration:**
```kotlin
Api.event<PlayerJoinEvent> { event ->
    event.player.sendMessage(Api.chat.color("&aWelcome!"))
}
```

**Listener Class Registration:**
```kotlin
package com.example.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class MyListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.sendMessage("Welcome!")
    }
}
```

**Registration in Plugin:**
```kotlin
Api.events.register("com.example.listeners", debug = true)
```

### 📁 Configuration System
Load custom configuration files automatically.
> 💡 **Tip:** If the file does not exist, it will be extracted from resources or created automatically!
```kotlin
val config = Api.config(this, "config.yml")
val value = config.get().getString("some.key", "default")
config.save()
config.reload()
```

### 💬 Chat Formatting
KuraCore includes a built-in chat formatter that supports Hex, gradients, and SmallCaps.

**Color formatting:**
```kotlin
val message = Api.chat.color("&#FF0000Hello &aworld!")
```

**SmallCaps formatting:**
```kotlin
val smallCapsText = Api.chat.smallCaps("Hello World")
// Output: ʜᴇʟʟᴏ ᴡᴏʀʟᴅ
```

**Language System:**
```kotlin
val lang = Api.lang(this, "lang.yml")
val message = lang.get("welcome.message")
// Automatically reloads when file changes
```

### 💾 Database Integration
KuraCore supports MongoDB and SQL databases with ORM. First, set up global connections.

**SQL Setup:**
```kotlin
val sqlPool = KuraSqlPool("jdbc:mysql://localhost:3306/database", "user", "pass")
Api.setupGlobalSql(sqlPool)
```

**SQL Usage:**
```kotlin
// Create table
Api.orm.createTable("players", "uuid" to "VARCHAR(36) PRIMARY KEY", "level" to "INT")

// Insert
Api.orm.insert("players", "uuid" to player.uniqueId.toString(), "level" to 1)

// Get
val data = Api.orm.get("players", "uuid" to player.uniqueId.toString())
val level = data?.get("level") as Int

// Update
Api.orm.update("players", "uuid" to player.uniqueId.toString(), "level" to 2)

// Delete
Api.orm.delete("players", "uuid" to player.uniqueId.toString())
```

**MongoDB Setup:**
```kotlin
val mongoPool = KuraMongoPool("mongodb://localhost:27017", "database")
Api.setupGlobalMongo(mongoPool)
```

**MongoDB Usage:**
```kotlin
// Similar ORM methods for MongoDB
Api.mongoOrm.insert("collection", "key" to "value")
val data = Api.mongoOrm.get("collection", "key" to "value")
```

**Cache System:**
```kotlin
val cache = Api.createCache<String, PlayerData>(expireMinutes = 30, maxSize = 1000)
cache.put("player123", playerData)
val data = cache.get("player123")
```

### 🎨 Item Builders
Create custom items easily with the fluent builder.
```kotlin
val sword = Api.item(Material.DIAMOND_SWORD)
    .name("&bEpic Sword", smallCaps = true)
    .lore("&7A powerful weapon", "&cDamage: +10")
    .enchant(Enchantment.DAMAGE_ALL, 5)
    .hideAttributes()
    .build()

val skull = Api.item(Material.PLAYER_HEAD)
    .name("&ePlayer Head")
    .skullOwner(player.name)
    .build()
```

### 🖼️ Menus
Build interactive GUIs by extending KuraMenu.
```kotlin
class MyMenu : KuraMenu("&aMy Menu", 3) { // 3 rows = 27 slots

    init {
        // Decorative item
        setItem(13, Api.item(Material.DIAMOND).name("&bDiamond").build())

        // Interactive button
        setButton(11, Api.item(Material.EMERALD).name("&aClick Me").build()) { event ->
            event.whoClicked.sendMessage("You clicked the emerald!")
            event.whoClicked.closeInventory()
        }

        // Base64 skull button
        setBase64Button(15, "base64texture", "&cSpecial Button", "Click for surprise!") { event ->
            // Action here
        }
    }
}

// Open menu
val menu = MyMenu()
player.openInventory(menu.inventory)
```

### 🌐 Network
Make asynchronous REST API calls.
```kotlin
// GET request
Api.rest.get("https://api.example.com/data").thenAccept { response ->
    if (response != null) {
        val value = response.get("key").asString
        Bukkit.getLogger().info("Received: $value")
    }
}

// POST request
val jsonBody = "{\"name\":\"value\"}"
Api.rest.post("https://api.example.com/endpoint", jsonBody).thenAccept { response ->
    // Handle response
}
```

### 🎵 Atmosphere Effects
Manage particles, sounds, and area-of-effect.
```kotlin
// Spawn particles
Api.particles.spawnParticles(location, Particle.FLAME, 20)

// Play sound
Api.sound.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f)

// Area of effect
Api.aoe.createSphere(location, 5.0) { loc ->
    loc.world.spawnParticle(Particle.HEART, loc, 1)
}
```

### ⏰ Tasks and Cooldowns
Schedule tasks and manage cooldowns with coroutines.
```kotlin
// Synchronous task (safe for Bukkit API)
Api.tasks.launchSync {
    delay(1000) // 1 second
    player.sendMessage("Delayed message!")
}

// Asynchronous task (for heavy operations)
Api.tasks.launchAsync {
    val result = heavyComputation()
    // Switch back to sync for Bukkit operations
    launchSync {
        player.sendMessage("Result: $result")
    }
}

// Cooldowns
if (!Api.cooldowns.hasCooldown(player, "command")) {
    Api.cooldowns.setCooldown(player, "command", 60) // 60 seconds
    // Execute command
} else {
    val remaining = Api.cooldowns.getRemainingTime(player, "command")
    player.sendMessage("Cooldown: ${remaining}s")
}
```

### 📦 Serialization
Serialize and deserialize items and data.
```kotlin
// Serialize item to Base64
val itemString = Api.base64.serialize(itemStack)

// Deserialize from Base64
val restoredItem = Api.base64.deserialize(itemString)
```

---

## 🧩 Example Plugin using KuraCore

```kotlin
package com.example.plugin

import com.kuraky.api.Api
import com.kuraky.commands.Kuracmd
import com.kuraky.commands.SenderType
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin() {

    override fun onEnable() {
        // Initialize KuraCore
        Api.init(this)

        // Load configuration
        val config = Api.config(this, "config.yml")

        // Register commands package
        Api.commands.registerPackage("com.example.plugin.commands")

        // Register events inline
        Api.event<PlayerJoinEvent> { event ->
            val message = Api.chat.color("&aWelcome to the server, ${event.player.name}!")
            event.joinMessage = message
        }

        // Or register listener classes
        Api.events.register("com.example.plugin.listeners")

        // Example: Create and open a menu
        // (This would be called when a player runs a command)

        Bukkit.getConsoleSender().sendMessage(Api.chat.color("&aExamplePlugin enabled with KuraCore!"))
    }
}

// Example command class
class ExampleCommands {

    @Kuracmd(main = "hello", aliases = ["hi"], senderType = SenderType.Player)
    fun hello(sender: CommandSender, args: Array<String>) {
        sender.sendMessage(Api.chat.color("&aHello from KuraCore!"))
    }
}

// Example listener class
class ExampleListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        // Additional join logic
    }
}
```

---

## 🧠 Philosophy

KuraCore was designed with three main goals:
1. **Keep plugins simple**
2. **Avoid repeating code**
3. **Support modular ecosystems**

Instead of re-implementing systems across plugins, KuraCore centralizes common utilities so you can focus on making unique features.

---

## 🌐 Kuraky Studios Ecosystem

KuraCore is the base for all Kuraky Studios plugins.
**Planned ecosystem:**
- 🟢 KuraWelcome

---

## 🤝 Contributing

Contributions are welcome! If you'd like to improve KuraCore:
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

---

## 📜 License & Credits

* **License:** [MIT License](LICENSE)
* **Developed by:** [Kuraky Studios](https://github.com/kuraky-oficial)

*Future goal: build a modern ecosystem of Minecraft plugins.*