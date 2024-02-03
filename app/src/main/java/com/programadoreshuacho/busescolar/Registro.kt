package com.programadoreshuacho.busescolar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import java.util.*

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //ocultamos el nabvar para esta vista
        supportActionBar?.hide()
        //https://www.youtube.com/watch?v=nzQVzIHIzUg&ab_channel=Codingraph
        val spinner = findViewById<Spinner>(R.id.cbo_tipoDoc)
        //val lista = listOf("Dni","CE","Pasaporte")
        val lista = resources.getStringArray(R.array.opciones)

        Log.d("LISTA: ", Arrays.toString(lista))

        //val adaptador = ArrayAdapter(this,android.R.layout.simple_spinner_item,lista)
        val adaptador = ArrayAdapter(this,R.layout.spinner_lista_style,lista)
        spinner.adapter = adaptador

        spinner.onItemSelectedListener= object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Log.d("PARENT: ", Arrays.toString(p0))
                //Log.d("VIEW: ", lista[p1])
                Log.d("POSITION: ", lista[p2])
                //Log.d("ID: ", lista[p3])

                Toast.makeText(applicationContext,lista[p2],9000).show()
                Toast.makeText(this@Registro,"mensaje TOAST",Toast.LENGTH_LONG).show()


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }


}