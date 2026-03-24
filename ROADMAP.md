# 🗺️ Roadmap de KuraCore - Planes Futuros

## Estado Actual (v1.1) ✅

```
Funcionalidades Implementadas:
├── ⚡ Sistema de Comandos Avanzado
├── 📡 Registro de Eventos
├── 🎨 Formateo de Chat (Hex, Gradientes, SmallCaps)
├── 📦 Gestión de Configuración con Auto-Reload
├── 💾 Integración SQL + MongoDB
├── 🏗️ Builders para Items y Menús
├── 🎯 Menús GUI Simples y Paginados
├── 📊 Sistema de Caché (Caffeine)
├── ⏱️ Cooldowns y Gestión de Tareas
├── 🎆 Partículas y Efectos de Sonido
└── 🌐 Cliente REST integrado
```

---

## 🟢 FASE 1 - MEJORAS INMEDIATAS (v1.2)

### Código
- [ ] Agregar validación automática de comandos
  - Tipos seguros para argumentos
  - Validadores customizables
  - Mensajes de error automáticos
  
- [ ] Sistema de decoradores para comandos
  ```kotlin
  @Kuracmd(main = "test")
  @RequirePermission("test.perm")
  @RequireCooldown(5)
  @RequirePlayer
  fun testCommand(sender: CommandSender, args: Array<String>) { }
  ```

- [ ] Mejorar sistema de eventos
  - Priority system para listeners
  - Auto-cancel en ciertos eventos

### Documentación ✅ (YA HECHO)
- [x] Guía rápida para principiantes
- [x] Ejemplos avanzados
- [x] Template de plugin
- [x] Revisión del proyecto

### Tests
- [ ] Tests unitarios para:
  - `ChatManagerV1` (colores, gradientes, smallcaps)
  - `ItemBuilder` (creación y modificación)
  - `CommandManagerV1` (registro y ejecución)
  - `KuraCache` (acceso y expiración)

---

## 🟡 FASE 2 - CARACTERÍSTICAS NUEVAS (v1.3)

### Sistema de Permisos Mejorado
```kotlin
// Actualmente: solo strings de permisos
// Nuevo: Sistema jerárquico

@Kuracmd(permissions = "perms.edit.config")
// Debería también aceptar: "perms.edit.*" o "perms.*"
```

### Validation Framework
```kotlin
class MiComando {
    @Kuracmd(main = "tp")
    fun teleport(
        sender: CommandSender,
        @Range(0.0, 1000.0) x: Double,
        @Range(0.0, 1000.0) y: Double,
        @Range(0.0, 1000.0) z: Double
    ) { }
}
```

### Logger Customizable
```kotlin
val miLogger = Api.createLogger(
    name = "MiPlugin",
    level = LogLevel.DEBUG,
    handlers = listOf(
        ConsoleHandler(),
        FileHandler("logs/miplugin.log")
    )
)
```

### Sistema de Hooks/Callbacks
```kotlin
Api.onPlayerLevelUp { player, nivel ->
    // Código ejecutado cuando sube de nivel
}
```

---

## 🔴 FASE 3 - ECOSISTEMA (v1.4 - v2.0)

### Maven/Gradle Archetype
```bash
mvn archetype:generate -DarchetypeGroupId=com.kuraky \
  -DarchetypeArtifactId=kuracore-plugin-archetype \
  -DgroupId=com.ejemplo -DartifactId=miplugin
```

### Plugin Repository Centralizado
- [ ] Sistema de descarga de plugins desde Kuraky Hub
- [ ] Gestión de versiones y dependencias automática
- [ ] Repositorio público (similar a Maven Central)

### Dashboard Web
- [ ] Panel de control para gestionar plugins
- [ ] Logs en tiempo real
- [ ] Estadísticas de uso
- [ ] Editor de configuración GUI

### Marketplace de Plugins
- [ ] Publicar plugins para otros usuarios
- [ ] Sistema de ratings/reviews
- [ ] Instalación con un comando

---

## 💡 FASE 4 - AVANZADO (v2.0+)

### Inyección de Dependencias
```kotlin
@KuraPlugin
class MiPlugin {
    @Inject
    private lateinit var database: JugadorDAO
    
    @Inject
    private lateinit var logger: KuraLogger
    
    fun onEnable() {
        // database y logger ya están inyectados
    }
}
```

### ORM Automático
```kotlin
@Entity("jugadores")
data class Jugador(
    @Id val uuid: UUID,
    val nombre: String,
    val nivel: Int
)

// Se genera automáticamente el CRUD:
Api.orm.find<Jugador>(uuid) // SELECT
Api.orm.save<Jugador>(jugador) // INSERT/UPDATE
Api.orm.delete<Jugador>(uuid) // DELETE
```

### Sistema de Eventos Reactivo (RxKotlin)
```kotlin
Api.events.rxPlayerJoin
    .buffer(5)
    .throttleTime(1000, TimeUnit.MILLISECONDS)
    .subscribe { players -> 
        println("Se unieron ${players.size} jugadores")
    }
```

### GraphQL API
```kotlin
@GraphQLQuery
fun obtenerJugador(uuid: String): JugadorDTO {
    return Api.orm.find<Jugador>(UUID.fromString(uuid))
}
```

---

## 📊 Comparación: Antes vs Después

```
                    ANTES           DESPUÉS         FUTURO
Complejidad         ⭐⭐⭐⭐         ⭐⭐⭐          ⭐
Documentación       ⭐⭐             ⭐⭐⭐⭐⭐      ⭐⭐⭐⭐⭐
Ejemplos            ⭐               ⭐⭐⭐⭐⭐      ⭐⭐⭐⭐⭐
Tests               ❌              ❌             ⭐⭐⭐
Developer UX        ⭐⭐⭐           ⭐⭐⭐⭐        ⭐⭐⭐⭐⭐
Community           ⭐              ⭐⭐           ⭐⭐⭐⭐
```

---

## 🎯 Cómo Contribuir

Si quieres ayudar con el roadmap:

1. **Issues**: Reporta bugs o sugiere features
2. **PRs**: Envía código mejorado
3. **Documentación**: Añade ejemplos o tutoriales
4. **Tests**: Escribe pruebas unitarias
5. **Feedback**: Comenta sobre tu experiencia

---

## 📅 Timeline Estimado

```
Q1 2026: FASE 1 (v1.2)     ✅ EN PROGRESO
Q2 2026: FASE 2 (v1.3)     ⏳ PLANIFICADO
Q3 2026: FASE 3 (v1.4)     ⏳ PLANIFICADO
Q4 2026: FASE 4 (v2.0)     🔮 FUTURO
```

---

## 🚀 Versión Objetivo (v2.0)

```
KuraCore v2.0 será:
✨ El framework más simple para crear plugins de Minecraft
🏆 Con documentación y ejemplos de clase mundial
⚡ Super rápido de aprender y usar
🎯 Ideal para principiantes y expertos
💪 Capaz de manejar proyectos grandes
🌍 Con comunidad activa
```

---

## 📞 Feedback

- **GitHub Issues**: Para bugs y features
- **Discussions**: Para preguntas y ideas
- **Wiki**: Para documentación comunitaria

---

**¿Alguna sugerencia? ¡Abre un issue o contacta a los desarrolladores!** 🎉

