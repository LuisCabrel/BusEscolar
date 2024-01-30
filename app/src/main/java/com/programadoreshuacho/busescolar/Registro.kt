package com.programadoreshuacho.busescolar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        //ocultamos el nabvar para esta vista
        supportActionBar?.hide()
        //https://www.youtube.com/watch?v=nzQVzIHIzUg&ab_channel=Codingraph
        val spinner = findViewById<Spinner>(R.id.cbo_spinner)
        //val lista = listOf("Dni","CE","Pasaporte")
        val lista = resources.getStringArray(R.array.opciones)
        val adaptador = ArrayAdapter(this,android.R.layout.simple_spinner_item,lista)
        spinner.adapter = adaptador

        spinner.onItemSelectedListener= object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //TODO("Not yet implemented")
                Toast.makeText(this@Registro,lista[p2],Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }


}