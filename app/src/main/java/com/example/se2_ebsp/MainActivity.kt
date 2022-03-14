package com.example.se2_ebsp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.isDigitsOnly
import java.net.Socket
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendRequestBtn = findViewById<Button>(R.id.button_getResponse)
        sendRequestBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                onClickSend()
            }
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

       writeSomething(Integer.toBinaryString(sum))

    }

     suspend  fun onClickSend() {
            //1. first get the text from the matrikel number field
            val matrikelNr = getMatrikelNr()
            //2. check input value for length (empty string is no symbolic matrikelnumber, further validation is done on the server level
            //and given an appropriate response)
            //the UI field is already constrained to numeric input , but usually we could check
            if(matrikelNr.isEmpty() || !matrikelNr.isDigitsOnly()){
                //Either do nothing
                //return

                //or inform the user, that the feature is only available for nonEmpty values within the outputfield -> way to go
                writeSomethingOnMainThread("Bitte geben sie (oben) eine gültige Matrikelnummer ein, welche eine Kette " +
                        "von numerischen Werten ohne Leer- oder Sonderzeichen ist")
            }
            //check if the value returned is actually a socket , otherwise we do not want to act on it
            val connection = createConnection()
            if(!(connection is Socket))
            {
                return
            }

            try{
                val out = connection.getOutputStream()
                var msg =  ""
                val input = connection.getInputStream()
                val inputStreamReader = BufferedReader(InputStreamReader(input))
                var temp :String? = null


                out.write(matrikelNr.toByteArray())
                out.flush() //make sure the message is not saved in any buffers


                //UTF-8 Encoding for numeric string and integer will be the same as it is internally converted
                //check for response

                do{
                    temp = inputStreamReader.read().toString()
                    if(!temp.isNullOrBlank()){
                        msg += temp
                    }
                }while(!temp.isNullOrBlank())

                writeSomethingOnMainThread(msg.toString())
            }catch(ex:Exception){
               handle(ex)
            }

    }

    /**
     * createCOnnection(): a function that takes no argument and opens a connection to the aau server and port specified in the exercise
     * @return socket : Socket?
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     * @throws SecurityException –
     * @throws IllegalArgumentException
     *
     * No special exception handling needed
     * @caller: needs to check is actual Socket is returned
     */
     fun createConnection() :Socket? {
        try {
            val socket :Socket? = Socket("se2-isys.aau.at",53212)
            println("Socket is Connected: ${(socket as Socket).isConnected}")
            return socket
        }
        catch (ex : Exception){
            handle(ex)
        }
        return null
    }

    fun getMatrikelNr():String{
          val matNr: EditText = findViewById(R.id.field_number)
          return matNr.text.toString()
    }


    private suspend fun writeSomethingOnMainThread(something:String){
        withContext(Main){
            writeSomething(something)
        }
    }

     fun writeSomething(something:String) {

             val textView = findViewById<TextView>(R.id.text_response)
             textView.text = something

     }

    private  fun handle(e:Exception){
        println(e.message)
        throw  e
    }
}