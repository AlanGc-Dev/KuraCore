package com.kuraky.atmosphere

import com.kuraky.api.Api
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable


/**
 * Gestor centralizado de sonidos.
 */
object SoundManager {

    fun play(player: Player, sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f) {
        player.playSound(player.location, sound, volume, pitch)
    }

    fun playAt(location: Location, sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f) {
        location.world?.playSound(location, sound, volume, pitch)
    }


}

/**
 * Creador de secuencias musicales o de efectos (SoundTracks).
 * Perfecto para animaciones de cajas fuertes, level-ups o muertes de Bosses.
 */

class SoundTrack {
    private val sequence = mutableListOf<SoundFrame>()

    data class SoundFrame(val delayTicks: Long, val sound: Sound, val volume: Float, val pitch: Float)

    /**
     * Añade un sonido a la secuencia.
     * @param delayTicks Cuántos ticks esperar DESPUÉS del sonido anterior (20 ticks = 1 seg).
     */

    fun append(delayTicks: Long, sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f): SoundTrack {
        sequence.add(SoundFrame(delayTicks, sound, volume, pitch))
        return this
    }

    fun play (player: Player) {
        var currentAccumulatedDelay = 0L

        for (frame in sequence) {
            currentAccumulatedDelay += frame.delayTicks

            if (currentAccumulatedDelay == 0L) {
                player.playSound(player.location, frame.sound, frame.volume, frame.pitch)
                continue
            }

            object : BukkitRunnable() {
                override fun run() {
                    player.playSound(player.location, frame.sound, frame.volume, frame.pitch)
                }
            }.runTaskLater(Api.plugin, currentAccumulatedDelay)

        }
    }

}