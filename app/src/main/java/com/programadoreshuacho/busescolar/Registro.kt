package com.programadoreshuacho.busescolar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        //ocultamos el nabvar para esta vista
        supportActionBar?.hide()
        //https://www.youtube.com/watch?v=nzQVzIHIzUg&ab_channel=Codingraph
        val spinner = findViewById<Spinner>(R.id.cbo_spinner)
        val lista = listOf("Dni","CE","Pasaporte")
        val adaptador = ArrayAdapter(this,android.R.layout.simple_spinner_item,lista)
        spinner.adapter = adaptador
    }


}