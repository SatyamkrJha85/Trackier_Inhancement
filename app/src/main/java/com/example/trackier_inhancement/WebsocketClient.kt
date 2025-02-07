package com.example.trackier_inhancement

import android.util.Log
import okhttp3.*
import java.util.concurrent.TimeUnit

class WebSocketClient(private val userId: String) : WebSocketListener() {
    private val TAG = "WebSocketClient"
    private var webSocket: WebSocket? = null

    // Initialize the WebSocket connection
    fun connect() {
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("ws://your-server-ip:5001?userId=$userId") // Replace with your server IP
            .build()

        webSocket = client.newWebSocket(request, this)
    }

    // Close the WebSocket connection
    fun disconnect() {
        webSocket?.close(1000, "Closing connection")
    }

    // Handle WebSocket connection opened
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(TAG, "WebSocket connection opened")
    }

    // Handle incoming messages
    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d(TAG, "Received message: $text")
        // Handle the incoming notification here
        // For example, display a notification or update the UI
    }

    // Handle WebSocket connection closed
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "WebSocket connection closed")
    }

    // Handle WebSocket connection failure
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(TAG, "WebSocket connection failed", t)
    }
}