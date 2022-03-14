package com.example.se2_ebsp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.net.Socket
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendRequestBtn = findViewById<Button>(R.id.button_getResponse)
        sendRequestBtn.setOnClickListener {
               onClickSend()
        }
        val calculateButton = findViewById<Button>(R.id.button_calculate)
        calculateButton.setOnClickListener {
                calculate()
        }

    }


    fun calculate(){
        val matrNrString = this.getMatrikelNr()

        var index = 0
        var sum = 0
        while(index < matrNrString.length){
            sum += Integer.parseInt(matrNrString.get(index).toString())
            index++
        }

        val textView : TextView = findViewById(R.id.text_response)
       writeSomething(Integer.toBinaryString(sum))

    }

     fun onClickSend() = runBlocking {
            createConnection()
    }

    fun getMatrikelNr():String{
          val matNr: EditText = findViewById(R.id.field_number)
        return matNr.text.toString()
    }




    suspend fun createConnection() = coroutineScope {

        val job = launch {
            val socket = Socket("se2-isys.aau.at",53212)
            socket.isConnected
            delay(100L)
        }
        job.join()

    }

     fun writeSomething(something:String) {
         val textView = findViewById<TextView>(R.id.text_response)
         textView.text = something
     }
}