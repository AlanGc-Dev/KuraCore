package com.kuraky.atmosphere

import org.bukkit.Location
import org.bukkit.Particle
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object ParticleEngine {/**
 * Dibuja una línea recta entre dos puntos.
 */
fun drawLine(loc1: Location, loc2: Location, particle: Particle, points: Int = 15) {
    val world = loc1.world ?: return
    val vector = loc2.toVector().subtract(loc1.toVector())
    val step = vector.clone().multiply(1.0 / points)

    val currentLoc = loc1.clone()
    for (i in 0..points) {
        world.spawnParticle(particle, currentLoc, 1, 0.0, 0.0, 0.0, 0.0)
        currentLoc.add(step)
    }
}

    /**
     * Dibuja un anillo horizontal (Círculo) alrededor de un centro.
     */
    fun drawCircle(center: Location, particle: Particle, radius: Double, points: Int = 30) {
        val world = center.world ?: return
        val increment = (2 * Math.PI) / points

        for (i in 0 until points) {
            val angle = i * increment
            val x = center.x + (radius * cos(angle))
            val z = center.z + (radius * sin(angle))
            world.spawnParticle(particle, Location(world, x, center.y, z), 1, 0.0, 0.0, 0.0, 0.0)
        }
    }

    /**
     * Dibuja una espiral ascendente. (Típico efecto de "Level Up" o Teletransporte).
     */
    fun drawSpiral(center: Location, particle: Particle, radius: Double, height: Double, points: Int = 50, loops: Int = 3) {
        val world = center.world ?: return
        val yStep = height / points
        val angleStep = (2 * Math.PI * loops) / points

        var currentY = center.y
        for (i in 0..points) {
            val angle = i * angleStep
            val x = center.x + (radius * cos(angle))
            val z = center.z + (radius * sin(angle))

            world.spawnParticle(particle, Location(world, x, currentY, z), 1, 0.0, 0.0, 0.0, 0.0)
            currentY += yStep
        }
    }

    /**
     * Dibuja una esfera perfecta 3D usando el algoritmo de la Esfera de Fibonacci.
     * Genera una distribución uniforme de partículas.
     */
    fun drawSphere(center: Location, particle: Particle, radius: Double, points: Int = 100) {
        val world = center.world ?: return
        val phi = Math.PI * (3.0 - sqrt(5.0)) // Ángulo dorado

        for (i in 0 until points) {
            val y = 1 - (i / (points - 1.0)) * 2 // va de 1 a -1
            val r = sqrt(1 - y * y) * radius // Radio en la altura Y
            val theta = phi * i

            val x = cos(theta) * r
            val z = sin(theta) * r

            world.spawnParticle(particle, center.clone().add(x, y * radius, z), 1, 0.0, 0.0, 0.0, 0.0)
        }
    }
}