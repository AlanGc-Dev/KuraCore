package com.kuraky.chat

import net.md_5.bungee.api.ChatColor
import java.util.regex.Pattern

class ChatManagerV1 {

    private val hexPatter = Pattern.compile("&#([A-Fa-f0-9]{6})")
    private val gradientPattern = Pattern.compile("<g:(#[A-Fa-f0-9]{6}):(#[A-Fa-f0-9]{6})>(.*?)</g>")
    private val colorCodeProtector = Pattern.compile("<g:#[A-Fa-f0-9]{6}:#[A-Fa-f0-9]{6}>|</g>|&#[A-Fa-f0-9]{6}|&[0-9a-fk-orA-FK-OR]")

    fun color(text: String): String {
        var message = text

        var gradientMatcher = gradientPattern.matcher(message)
        while (gradientMatcher.find()) {
            val startHex = gradientMatcher.group(1)
            val endHex = gradientMatcher.group(2)
            val context = gradientMatcher.group(3)

            val gradientText = createGrafient(context, startHex, endHex)
            message = message.replace(gradientMatcher.group(), gradientText)
            gradientMatcher = gradientPattern.matcher(message)

        }

        var hexMatcher = hexPatter.matcher(message)
        while (hexMatcher.find()) {
            val color = ChatColor.of("#" + hexMatcher.group(1))
            message = message.replace(hexMatcher.group(), color.toString())
            hexMatcher = hexPatter.matcher(message)
        }

        return ChatColor.translateAlternateColorCodes('&', message)

    }

    fun smallCaps(text: String): String {
        val matcher = colorCodeProtector.matcher(text)
        val sb = java.lang.StringBuilder()
        var lastEnd = 0

        // Separa el texto de los códigos de color
        while (matcher.find()) {
            sb.append(convertToSmallCaps(text.substring(lastEnd, matcher.start())))
            sb.append(matcher.group()) // Agrega el código de color SIN tocarlo
            lastEnd = matcher.end()
        }
        sb.append(convertToSmallCaps(text.substring(lastEnd)))

        return sb.toString()
    }

    private fun createGrafient(text: String, hexStart: String, hexEnd: String): String {
        val start = java.awt.Color.decode(hexStart)
        val end = java.awt.Color.decode(hexEnd)

        val length = text.length
        if (length == 0) return ""

        val builder = StringBuilder()
        for (i in 0 until length) {
            val ratio = if (length == 1) 0f else i.toFloat() / (length - 1).toFloat()
            val red = (start.red + ratio * (end.red - start.red)).toInt()
            val green = (start.green + ratio * (end.green - start.green)).toInt()
            val blue = (start.blue + ratio * (end.blue - start.blue)).toInt()

            val stepColor = java.awt.Color(red, green, blue)
            val hex = String.format("#%02x%02x%02x", stepColor.red, stepColor.green, stepColor.blue)

            builder.append(ChatColor.of(hex).toString())
            builder.append(text[i])
        }
        return builder.toString()
    }

    private fun convertToSmallCaps(text: String): String {
        val normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val smalls = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏzᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏz"
        val result = java.lang.StringBuilder()
        for (char in text) {
            val index = normal.indexOf(char)
            result.append(if (index != -1) smalls[index] else char)
        }
        return result.toString()
    }

    fun format(text: String, useSmallCaps: Boolean = false): String {
        val processedText = if (useSmallCaps) smallCaps(text) else text
        return color(processedText)
    }

}