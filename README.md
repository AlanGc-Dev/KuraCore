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

---

## 🚀 API Usage

### ⌨️ Register a Command
Registers commands using the KuraCore command system.
```kotlin
Api.command("name", "permission", SenderType.BOTH) { sender, args ->
    // Command logic here
}
```

### 🎯 Register Events
Simplifies event registration for Bukkit events.
```kotlin
Api.event<PlayerJoinEvent> { event ->
    // Event logic here
}
```

### 📁 Configuration System
Load custom configuration files automatically.
> 💡 **Tip:** If the file does not exist, it will be extracted from resources or created automatically!
```kotlin
val lang = Api.config(this, "mensajes.yml")
```

### 💬 Chat Formatting
KuraCore includes a built-in chat formatter that supports Hex and normal color codes.

**Basic formatting:**
```kotlin
Api.chat.format("&#FF0000Hello world")
```

**SmallCaps formatting:**
```kotlin
Api.chat.format("&aHello world", true)
```
> **Example output:** ʜᴇʟʟᴏ ᴡᴏʀʟᴅ

---

## 🧩 Example Plugin using KuraCore

```kotlin
package com.example.plugin

import com.kuraky.api.Api
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin() {

    override fun onEnable() {

        // Load configuration
        Api.config(this, "mensajes.yml")

        // Register commands
        Api.command("hello", null, SenderType.PLAYER) { sender, _ ->
            sender.sendMessage(Api.chat.format("&aHello from KuraCore!"))
        }

        // Register events
        Api.event<PlayerJoinEvent> { event ->
            // Format message with SmallCaps
            val message = Api.chat.format("&aWelcome to the server!", true)
            event.joinMessage = message
        }
        
        Bukkit.getConsoleSender().sendMessage(Api.chat.format("&aPlugin enabled!"))
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