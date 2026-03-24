# 🚀 Guía Rápida de Inicio - KuraCore

## ⚡ 5 MINUTOS: Primer Comando

### 1. Crea una clase de comandos:
```kotlin
package tu.paquete.commands

import com.kuraky.api.Api
import com.kuraky.commands.Kuracmd
import com.kuraky.commands.SenderType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MisComandos {
    
    @Kuracmd(
        main = "saludar",
        aliases = ["hello", "hi"],
        permissions = "tuserver.saludar",
        senderType = SenderType.Player
    )
    fun saludar(sender: CommandSender, args: Array<String>) {
        if (sender is Player) {
            sender.sendMessage(Api.chat.color("&a¡Hola &b${sender.name}&a!"))
        }
    }
}
```

### 2. Registra los comandos en tu plugin:
```kotlin
import org.bukkit.plugin.java.JavaPlugin
import com.kuraky.api.Api

class MiPlugin : JavaPlugin() {
    
    override fun onEnable() {
        Api.init(this)
        Api.commands.registerPackage("tu.paquete.commands", debug = true)
        logger.info("&aPlugin activado!")
    }
}
```

✅ **¡Listo!** Ya tienes el comando `/saludar`.

---

## 🎨 10 MINUTOS: Crear un Menú GUI

```kotlin
import com.kuraky.api.Api
import com.kuraky.menus.KuraMenu
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class MiPrimerMenu : KuraMenu("&c&lMi Menú", 3) {
    
    init {
        // Crear un item
        val item = Api.item(Material.DIAMOND)
            .name("&a&lDiamante Especial")
            .lore("&7Este es un diamante mágico", "&b¡Haz clic aquí!")
            .build()
        
        // Ponerlo en el slot 13 (centro)
        setButton(13, item) { event: InventoryClickEvent ->
            event.whoClicked.sendMessage(Api.chat.color("&a¡Hiciste clic!"))
            event.isCancelled = true
        }
    }
}

// Abrir el menú para un jugador:
fun abrirMenu(player: Player) {
    player.openInventory(MiPrimerMenu().inventory)
}
```

---

## 🗄️ 15 MINUTOS: Guardar Datos en Config

```kotlin
val config = Api.config(this, "datos.yml")

// Guardar
config.set("jugador.${player.uniqueId}.nivel", 5)
config.save()

// Cargar
val nivel = config.getInt("jugador.${player.uniqueId}.nivel", 0)
player.sendMessage("Tu nivel es: $nivel")
```

**Bonus:** Si editas `datos.yml`, ¡se recarga automáticamente!

---

## 🎯 20 MINUTOS: Escuchar Eventos

```kotlin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class MisEventos : Listener {
    
    @EventHandler
    fun alUnirse(event: PlayerJoinEvent) {
        event.player.sendMessage(
            Api.chat.format("&a&lBienvenido &b${event.player.name}", useSmallCaps = true)
        )
    }
}

// En tu plugin:
Api.events.register("tu.paquete.listeners", debug = true)
```

---

## 💾 25 MINUTOS: Usar Base de Datos

### SQL:
```kotlin
import com.kuraky.api.Api
import com.kuraky.database.KuraSqlPool

// Configurar (una sola vez):
val pool = KuraSqlPool(
    host = "localhost",
    port = 5432,
    database = "mibase",
    user = "admin",
    password = "password",
    maxConnections = 10
)
Api.setupGlobalSql(pool)

// Usar:
Api.orm.execute(
    "INSERT INTO jugadores (uuid, nombre, nivel) VALUES (?, ?, ?)",
    player.uniqueId.toString(),
    player.name,
    10
)

val resultado = Api.orm.query(
    "SELECT nivel FROM jugadores WHERE uuid = ?",
    player.uniqueId.toString()
)
```

### MongoDB:
```kotlin
val pool = KuraMongoPool(
    uri = "mongodb://localhost:27017",
    database = "mibase"
)
Api.setupGlobalMongo(pool)

// Usar:
val doc = mapOf("_id" to player.uniqueId.toString(), "nivel" to 10)
Api.mongoOrm.insertOne("jugadores", doc)

val jugador = Api.mongoOrm.findOne("jugadores", mapOf("_id" to player.uniqueId.toString()))
```

---

## 🎨 COLORES Y GRADIENTES

```kotlin
// Códigos de color:
Api.chat.color("&aVerde &cRojo &bAzul")

// Colores Hex:
Api.chat.color("&#FF5733Naranja personalizado")

// Gradientes:
Api.chat.color("<g:#FF0000:#0000FF>ARCOÍRIS</g>")

// Small Caps (letras pequeñas):
Api.chat.format("TEXTO BONITO", useSmallCaps = true)
```

---

## 🛠️ TROUBLESHOOTING

### "No se registraron mis comandos"
- Verifica que el paquete sea correcto
- Usa `debug = true` para ver mensajes
- Asegúrate de que `Api.init(this)` se ejecuta primero

### "El menú no abre"
- Llama a `Api.init(this)` en onEnable()
- El plugin debe depender de KuraCore en plugin.yml

### "No se guardan mis datos"
- Llama a `config.save()` después de cambios
- Usa `config.getInt()` con un valor por defecto

---

## 📚 MÁS RECURSOS

- [Documentación Completa](../README.md)
- [Ejemplos de Comandos Complejos](./EJEMPLOS_AVANZADOS.md)
- [API de Bases de Datos](./DATABASE.md)

---

**¿Preguntas?** Abre un issue en GitHub. 🎉

