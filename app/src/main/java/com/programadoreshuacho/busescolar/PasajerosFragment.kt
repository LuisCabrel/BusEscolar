package com.programadoreshuacho.busescolar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.programadoreshuacho.busescolar.Adapter.AdapterTab


class PasajerosFragment() : Fragment() {
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var viewPager2: ViewPager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pasajeros, container, false)
        //val view: View = inflater.inflate(R.layout.fragment_pasajeros, container, false)
        viewPager = view.findViewById(R.id.viewMaster)
        //viewPager2 = view.findViewById(R.id.viewMaster)
        tabs = view.findViewById(R.id.tab_registro)

        //val fragmentAdapter = AdapterTab(this@PasajerosFragment,supportFragmentManager,tabs.tabCount)
        /*Log.e("xxxx",childFragmentManager.toString())
        val fragmentAdapter = AdapterTab(childFragmentManager)
        viewPager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewPager)
        */
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar adaptador del ViewPager y tabs.setupWithViewPager(viewPager)
        val fragmentAdapter = AdapterTab(childFragmentManager)
        viewPager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewPager)

        // Llamar a la función tabulador() después de que viewPager haya sido inicializado
        //getViewPager()
    }

    fun getViewPager(): ViewPager {
        Log.e("Ingresa a getViewPager ",viewPager.toString())
        return viewPager
    }

    fun tabulador(){
        Log.e("Ingresa a tabulador ",viewPager.toString())
        viewPager.setCurrentItem(1, true)
    }

}





object supportFragmentManager : FragmentManager() {

}
