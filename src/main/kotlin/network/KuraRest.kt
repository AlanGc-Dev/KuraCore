package com.kuraky.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

/**
 * Cliente HTTP Asíncrono para consumir APIs REST, Firebase o Webhooks.
 */
object KuraRest {

    // Usamos el cliente nativo de Java (Ultra rápido y sin dependencias extra)
    private val client = HttpClient.newHttpClient()
    private val gson = Gson() // Incluido en PaperMC

    /**
     * Realiza una petición GET asíncrona a una API externa.
     * Retorna un CompletableFuture (No congela el servidor).
     */
    fun get(url: String, headers: Map<String, String> = emptyMap()): CompletableFuture<JsonObject?> {
        val builder = HttpRequest.newBuilder().uri(URI.create(url)).GET()
        headers.forEach { (k, v) -> builder.header(k, v) }

        return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
            .thenApply { response ->
                if (response.statusCode() in 200..299) {
                    gson.fromJson(response.body(), JsonObject::class.java)
                } else {
                    null // O manejar el error según prefieras
                }
            }
    }

    /**
     * Realiza una petición POST asíncrona (Para enviar datos a Firebase/Supabase).
     */
    fun post(url: String, jsonBody: String, headers: Map<String, String> = emptyMap()): CompletableFuture<JsonObject?> {
        val builder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json") // Importante para REST
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))

        headers.forEach { (k, v) -> builder.header(k, v) }

        return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
            .thenApply { response ->
                if (response.statusCode() in 200..299) {
                    gson.fromJson(response.body(), JsonObject::class.java)
                } else {
                    null
                }
            }
    }
}