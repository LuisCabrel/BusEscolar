package com.programadoreshuacho.busescolar.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.programadoreshuacho.busescolar.PasajerosFragment
import com.programadoreshuacho.busescolar.RegistraralumnoFragment
import com.programadoreshuacho.busescolar.RegistrarpadreFragment

//internal class AdapterTab(var context: PasajerosFragment, fm:FragmentManager, var totalTabs:Int):FragmentPagerAdapter(fm) {
class AdapterTab(fm: FragmentManager):FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return when(position){
            0 ->{
                RegistrarpadreFragment()
            }
            /*1 ->{
                RegistraralumnoFragment()
            }
            else -> getItem(position)*/
            else -> RegistraralumnoFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Padre"
            else -> {
                return "Alumno"
            }
        }
    }

}