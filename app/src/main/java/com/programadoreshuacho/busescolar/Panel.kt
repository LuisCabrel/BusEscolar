package com.programadoreshuacho.busescolar.Conductor

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.programadoreshuacho.busescolar.R
import com.programadoreshuacho.busescolar.R.id.nav_header


//class Panel : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
class Panel : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        //asignamos thema principal
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)

        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val navegacion: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        //toggle = ActionBarDrawerToggle(this,drawer,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        //toggle.syncState()

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);//cambiamos el icono para menu lateral
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        //val navigationView: NavigationView  = findViewById(R.id.nav_view)
        navegacion.setNavigationItemSelectedListener(this)
        /*    navegacion.setNavigationItemSelectedListener{
            it.isChecked=true

            when(it.itemId){
                R.id.nav_alumno -> replaceFragment(PasajerosFragment(),it.title.toString())
            }
            true
        }*/
        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.commit()

        //redireccion de card
        val cardChofer = findViewById<CardView>(R.id.card_chofer)
        val cardBus = findViewById<CardView>(R.id.card_bus)
        val cardAlumno = findViewById<CardView>(R.id.card_alumnos)
        val cardIda = findViewById<CardView>(R.id.card_rutaida)
        val cardRetorno = findViewById<CardView>(R.id.card_rutaretorno)

        cardChofer.setOnClickListener {
            val fragmentoPerfilConducto = PerfilconductorFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragmentoPerfilConducto)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        cardBus.setOnClickListener {
            val fragmentoBus = BusFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragmentoBus)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        cardAlumno.setOnClickListener {
            val fragmentoListarPasajeros = ListarpasajerosFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragmentoListarPasajeros)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        cardIda.setOnClickListener {
            val fragmentoRutaIda = RutaidaFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragmentoRutaIda)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        cardRetorno.setOnClickListener {
            val fragmentoRutaRetorno = RutaretornoFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragmentoRutaRetorno)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        when (item.itemId) {
            R.id.nav_panel -> startActivity(Intent(this, Panel::class.java))
            R.id.nav_perfil -> replaceFragment(PerfilconductorFragment(),item.title.toString())
            //R.id.nav_panel -> startActivity(Intent(this, Panel::class.java))
            //R.id.nav_perfil -> startActivity(Intent(this, PerfilconductorActivity::class.java))
             R.id.nav_bus -> replaceFragment(BusFragment(),item.title.toString())
            R.id.nav_alumno -> replaceFragment(PasajerosFragment(),item.title.toString())
            R.id.nav_ida ->  replaceFragment(RutaidaFragment(),item.title.toString())
            R.id.nav_retorno ->  replaceFragment(RutaretornoFragment(),item.title.toString())

        }
        drawer.closeDrawer(GravityCompat.START)
        return true

    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun replaceFragment(fragment:Fragment,title:String){
        Log.e("titulo",title.toString())
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameContainer,fragment)
        fragmentTransaction.commit()
        drawer.closeDrawer()
        setTitle(title)
    }

}

fun DrawerLayout.closeDrawer() {

}





