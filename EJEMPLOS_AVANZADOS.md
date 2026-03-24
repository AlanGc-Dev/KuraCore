# 🔥 Ejemplos Avanzados - KuraCore

## 📌 Tabla de Contenidos
1. [Comandos Complejos](#comandos-complejos)
2. [Menús Paginados](#menús-paginados)
3. [Integración de Bases de Datos](#integración-de-bases-de-datos)
4. [Caché y Cooldowns](#caché-y-cooldowns)
5. [Sistemas de Atributos](#sistemas-de-atributos)
6. [Partículas y Efectos](#partículas-y-efectos)

---

## Comandos Complejos

### Comando con Subcomandos

```kotlin
import com.kuraky.commands.Kuracmd
import com.kuraky.commands.SenderType
import com.kuraky.api.Api
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EconomyCommands {
    
    @Kuracmd(
        main = "dinero",
        sub = "ver",
        permissions = "tuplugin.dinero.ver",
        senderType = SenderType.Player
    )
    fun verDinero(sender: CommandSender, args: Array<String>) {
        if (sender is Player) {
            val dinero = ObtenerDinero(sender.uniqueId)
            sender.sendMessage(Api.chat.color("&aTienes: &b$$dinero"))
        }
    }
    
    @Kuracmd(
        main = "dinero",
        sub = "dar",
        permissions = "tuplugin.dinero.dar"
    )
    fun darDinero(sender: CommandSender, args: Array<String>) {
        if (args.size < 2) {
            sender.sendMessage(Api.chat.color("&cUso: /dinero dar <jugador> <cantidad>"))
            return
        }
        
        val target = sender.server.getPlayer(args[0])
        val amount = args[1].toIntOrNull() ?: return
        
        if (target != null) {
            AñadirDinero(target.uniqueId, amount)
            sender.sendMessage(Api.chat.color("&aGave $amount to ${target.name}"))
        }
    }
}
```

### Comando con Argumentos Variables

```kotlin
@Kuracmd(main = "teletransporte", aliases = ["tp"], permissions = "tuplugin.tp.usar")
fun teletransporte(sender: CommandSender, args: Array<String>) {
    if (sender !is Player) return
    
    when {
        args.isEmpty() -> {
            sender.sendMessage(Api.chat.color("&cUso: /tp <x> <y> <z> [mundo]"))
        }
        args.size >= 3 -> {
            try {
                val x = args[0].toDouble()
                val y = args[1].toDouble()
                val z = args[2].toDouble()
                val world = args.getOrNull(3)?.let { 
                    sender.server.getWorld(it) 
                } ?: sender.world
                
                val location = org.bukkit.Location(world, x, y, z)
                sender.teleport(location)
                sender.sendMessage(Api.chat.color("&a¡Teletransportado!"))
            } catch (e: NumberFormatException) {
                sender.sendMessage(Api.chat.color("&cCoordenadas inválidas"))
            }
        }
    }
}
```

---

## Menús Paginados

### Tienda de Items

```kotlin
import com.kuraky.api.Api
import com.kuraky.menus.KuraPaginatedMenu
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class TiendaMenu(val player: Player, val page: Int = 1) : 
    KuraPaginatedMenu("&c&lTienda de Items", 6, 
                      itemsPerPage = 28, 
                      initialPage = page) {
    
    init {
        // Definir items disponibles en la tienda
        val items = listOf(
            ItemData(Material.DIAMOND, 100),
            ItemData(Material.EMERALD, 50),
            ItemData(Material.GOLD_INGOT, 25),
            ItemData(Material.IRON_INGOT, 10)
        )
        
        // Agregar items al menú
        for (item in items) {
            val itemStack = Api.item(item.material)
                .name("&a${item.material.name}")
                .lore("&7Precio: &b$${item.precio}")
                .build()
            
            addItem(itemStack) { event: InventoryClickEvent ->
                val dinero = ObtenerDinero(player.uniqueId)
                if (dinero >= item.precio) {
                    QuitarDinero(player.uniqueId, item.precio)
                    player.inventory.addItem(ItemStack(item.material, 1))
                    player.sendMessage(Api.chat.color("&a¡Compra exitosa!"))
                } else {
                    player.sendMessage(Api.chat.color("&cDinero insuficiente"))
                }
                event.isCancelled = true
            }
        }
    }
    
    data class ItemData(val material: Material, val precio: Int)
}

// Usar:
fun abrirTienda(player: Player) {
    player.openInventory(TiendaMenu(player).inventory)
}
```

---

## Integración de Bases de Datos

### Guardar Datos de Jugador en SQL

```kotlin
import com.kuraky.api.Api
import com.kuraky.database.KuraSqlPool
import java.util.UUID

object JugadorDAO {
    
    fun crearJugador(uuid: UUID, nombre: String, nivel: Int = 1) {
        Api.orm.execute(
            """
            INSERT INTO jugadores (uuid, nombre, nivel, fecha_creacion) 
            VALUES (?, ?, ?, NOW())
            ON CONFLICT (uuid) DO NOTHING
            """,
            uuid.toString(),
            nombre,
            nivel
        )
    }
    
    fun obtenerNivel(uuid: UUID): Int {
        val resultado = Api.orm.query(
            "SELECT nivel FROM jugadores WHERE uuid = ?",
            uuid.toString()
        )
        return resultado.getOrNull(0)?.getInt("nivel") ?: 1
    }
    
    fun aumentarNivel(uuid: UUID) {
        Api.orm.execute(
            "UPDATE jugadores SET nivel = nivel + 1 WHERE uuid = ?",
            uuid.toString()
        )
    }
    
    fun obtenerTop10(): List<Map<String, Any>> {
        return Api.orm.query(
            "SELECT nombre, nivel FROM jugadores ORDER BY nivel DESC LIMIT 10"
        )
    }
}
```

### Guardar Datos en MongoDB

```kotlin
import com.kuraky.api.Api
import java.util.UUID

object JugadorMongoDAO {
    
    fun crearJugador(uuid: UUID, nombre: String, nivel: Int = 1) {
        val documento = mapOf(
            "_id" to uuid.toString(),
            "nombre" to nombre,
            "nivel" to nivel,
            "fecha_creacion" to System.currentTimeMillis()
        )
        Api.mongoOrm.insertOne("jugadores", documento)
    }
    
    fun obtenerJugador(uuid: UUID): Map<String, Any>? {
        return Api.mongoOrm.findOne(
            "jugadores",
            mapOf("_id" to uuid.toString())
        )
    }
    
    fun aumentarNivel(uuid: UUID) {
        Api.mongoOrm.updateOne(
            "jugadores",
            mapOf("_id" to uuid.toString()),
            mapOf("\$inc" to mapOf("nivel" to 1))
        )
    }
}
```

---

## Caché y Cooldowns

### Sistema de Caché

```kotlin
import com.kuraky.api.Api
import java.util.UUID

object CacheUsuarios {
    
    private val cache = Api.createCache<UUID, DatosUsuario>(
        expireMinutes = 30,
        maxSize = 1000
    )
    
    fun obtener(uuid: UUID): DatosUsuario {
        return cache.getOrCompute(uuid) {
            // Si no está en caché, cargarlo de la BD
            DatosUsuario(
                uuid = uuid,
                nivel = JugadorDAO.obtenerNivel(uuid),
                dinero = 1000
            )
        }
    }
    
    fun actualizar(uuid: UUID, datos: DatosUsuario) {
        cache.put(uuid, datos)
        // Sincronizar con BD
        JugadorDAO.actualizarDatos(uuid, datos)
    }
    
    data class DatosUsuario(
        val uuid: UUID,
        val nivel: Int,
        val dinero: Int
    )
}
```

### Sistema de Cooldowns

```kotlin
import com.kuraky.api.Api
import java.util.UUID

object CooldownsGlobales {
    
    private val cooldowns = Api.cooldowns
    
    fun puedeUsarComando(uuid: UUID, comando: String): Boolean {
        val key = "$uuid:$comando"
        return !cooldowns.isInCooldown(key)
    }
    
    fun establecerCooldown(uuid: UUID, comando: String, segundos: Int) {
        val key = "$uuid:$comando"
        cooldowns.startCooldown(key, (segundos * 1000).toLong())
    }
    
    fun obtenerTiempoRestante(uuid: UUID, comando: String): Long {
        val key = "$uuid:$comando"
        return cooldowns.getRemaining(key)
    }
}

// Usar en comando:
@Kuracmd(main = "atacar")
fun atacar(sender: CommandSender, args: Array<String>) {
    if (sender !is Player) return
    
    if (!CooldownsGlobales.puedeUsarComando(sender.uniqueId, "atacar")) {
        val tiempo = CooldownsGlobales.obtenerTiempoRestante(sender.uniqueId, "atacar") / 1000
        sender.sendMessage(Api.chat.color("&cCooldown: &b$tiempo&cs"))
        return
    }
    
    // Lógica del comando...
    
    CooldownsGlobales.establecerCooldown(sender.uniqueId, "atacar", 5)
}
```

---

## Sistemas de Atributos

### Atributos de Items

```kotlin
import com.kuraky.api.Api
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType

class ArmaEspecial {
    
    fun crear(): ItemStack {
        return Api.item(Material.DIAMOND_SWORD)
            .name("&c&lEspada del Caos")
            .lore("&7Daño: &c50%", "&7Ataque especial disponible")
            .addAttribute("daño_extra", "50")
            .addAttribute("especial", "activado")
            .build()
    }
    
    fun obtenerAtributo(item: ItemStack, clave: String): String? {
        val meta = item.itemMeta ?: return null
        val key = NamespacedKey.fromString("tuplugin:$clave") ?: return null
        return meta.persistentDataContainer.get(key, PersistentDataType.STRING)
    }
}

// Extension para ItemBuilder:
fun ItemBuilder.addAttribute(key: String, value: String): ItemBuilder {
    // Este método debería agregarse a ItemBuilder en KuraCore
    return this
}
```

---

## Partículas y Efectos

### Efectos de Ataque

```kotlin
import com.kuraky.api.Api
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object EfectosDeAtaque {
    
    fun ondasDeChoque(player: Player) {
        val location = player.location
        
        Api.particles.spawnParticle(
            particle = Particle.EXPLOSION_HUGE,
            location = location,
            count = 5,
            offsetX = 1.0,
            offsetY = 1.0,
            offsetZ = 1.0
        )
    }
    
    fun trazo(origen: Player, destino: Player) {
        val loc1 = origen.location.add(0.0, 1.5, 0.0)
        val loc2 = destino.location.add(0.0, 1.5, 0.0)
        
        val distancia = loc1.distance(loc2)
        val pasos = (distancia * 2).toInt()
        
        for (i in 0..pasos) {
            val progreso = i.toDouble() / pasos
            val x = loc1.x + (loc2.x - loc1.x) * progreso
            val y = loc1.y + (loc2.y - loc1.y) * progreso
            val z = loc1.z + (loc2.z - loc1.z) * progreso
            
            Api.particles.spawnParticle(
                particle = Particle.FLAME,
                location = org.bukkit.Location(loc1.world, x, y, z),
                count = 1
            )
        }
    }
}
```

### Efecto AoE (Área de Efecto)

```kotlin
import com.kuraky.api.Api
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object HabilidadesAoE {
    
    fun explotar(player: Player, radio: Double) {
        val ubicacion = player.location
        
        // Efecto visual
        Api.aoe.spawnParticles(
            particleType = "EXPLOSION",
            center = ubicacion,
            radius = radio,
            count = 50
        )
        
        // Daño a entidades cercanas
        ubicacion.world?.getNearbyLivingEntities(ubicacion, radio, radio, radio)?.forEach { entidad ->
            if (entidad !is Player || !entidad.isTeammate(player)) {
                entidad.damage(10.0, player)
            }
        }
    }
    
    fun curacion(player: Player, radio: Double) {
        val ubicacion = player.location
        
        // Efecto visual
        Api.particles.spawnParticle(
            particle = org.bukkit.Particle.HEART,
            location = ubicacion,
            count = 20,
            offsetX = radio,
            offsetY = radio,
            offsetZ = radio
        )
        
        // Curar aliados
        ubicacion.world?.getNearbyLivingEntities(ubicacion, radio, radio, radio)?.forEach { entidad ->
            if (entidad is LivingEntity) {
                entidad.health = entidad.maxHealth.coerceAtMost(entidad.health + 5)
                if (entidad is Player) {
                    entidad.addPotionEffect(
                        PotionEffect(PotionEffectType.REGENERATION, 100, 1)
                    )
                }
            }
        }
    }
}
```

---

## 🎉 Más Ejemplos

¡Abre un issue o PR en GitHub con tus ejemplos favoritos! 

**Tips:**
- Mantén el código limpio y documentado
- Usa tipos seguros (non-null cuando posible)
- Aprovecha las extensiones de Kotlin
- Cache datos cuando sea necesario

