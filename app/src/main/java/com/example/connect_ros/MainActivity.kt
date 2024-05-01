package com.example.connect_ros

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okio.ByteString
import java.util.logging.Handler
import java.io.IOException

class MainActivity : AppCompatActivity(){

    private lateinit var webSocket : WebSocket
    private lateinit var editTextRosIP : EditText
    private lateinit var textStatus : TextView
    private lateinit var buttonStart : Button
    private lateinit var buttonSend : Button
    private lateinit var buttonFinish : Button
    private var isConnected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextRosIP = findViewById(R.id.input_ros_ip)
        textStatus = findViewById(R.id.textView)

        
        buttonStart = findViewById<Button>(R.id.button_start)
        buttonStart.setOnClickListener{
            val rosIP = editTextRosIP.text.toString().trim()
            connectToRosWebSocket(rosIP)
        }

        buttonSend = findViewById<Button>(R.id.button_send)
        buttonSend.setOnClickListener {
            if(isConnected){
                sendMessage("Hello")
                textStatus.text = "Send"
            }
            else{
                textStatus.text = "Cannot Send"
            }
        }

        buttonFinish = findViewById<Button>(R.id.button_finish)
        buttonFinish.setOnClickListener {
            webSocket.cancel()
            isConnected = false
            textStatus.text = "Not Connect"
        }

    }

    private fun connectToRosWebSocket(rosIP : String){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(rosIP)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                updateStatus("Connect")
                isConnected = true
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                updateStatus("Connection Closed")
                isConnected = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                updateStatus("Connection Failed")
                isConnected = false
            }
        })
    }

    private fun updateStatus(status: String){
        runOnUiThread{
            textStatus.text = status
        }
    }

    private fun sendMessage(message: String){
        webSocket.send(message)
    }
}