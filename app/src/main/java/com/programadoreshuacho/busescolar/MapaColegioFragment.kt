package com.programadoreshuacho.busescolar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.net.PlacesClient
import java.io.IOException


class MapaColegioFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {
    private lateinit var map: GoogleMap
    private var LongIni: String?=""
    private var LatIni: String?=""
    private lateinit var placesClient: PlacesClient
    companion object {
        const val LOCATION_REQUEST_CODE = 100
        const val REQUEST_CODE_LOCATION = 0
        private const val AUTOCOMPLETE_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_mapa_colegio, container, false)
        val view= inflater.inflate(R.layout.fragment_mapa_colegio, container, false)
        createMapFragment()
        var mapSearchView = view.findViewById<SearchView>(R.id.mapSearchColegio)

        mapSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = mapSearchView.query.toString()
                var addressList: List<Address>? = null
                if (!location.isNullOrEmpty()) {
                    val geocoder = Geocoder(context)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    map.clear()
                    val address = addressList?.get(0)
                    val latlng = LatLng(address!!.latitude, address.longitude)
                    Log.e("address  --> ",address.toString())
                    Log.e("latitude  --> ",address.latitude.toString())
                    Log.e("longitude  --> ",address.longitude.toString())
                    Log.e("mapSearchView  --> ",mapSearchView.query.toString())
                    map.addMarker(MarkerOptions().position(latlng).title(location))
                    map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(latlng, 18f),
                            4000,
                            null
                    )

                    var sessionLocalizacionDireccionColegio = requireActivity().getSharedPreferences("sessionLocalizacionDireccionColegio", Context.MODE_PRIVATE)
                    var editor = sessionLocalizacionDireccionColegio.edit()
                    editor.clear()
                    editor.putString("address",address.toString())
                    editor.putString("latitud",address.latitude.toString())
                    editor.putString("longitud",address.longitude.toString())
                    editor.putString("direccion",mapSearchView.query.toString())
                    editor.commit()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return view
    }

    private fun createMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMapColegio) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)
        map.setOnMapLongClickListener(this)
        enableMyLocation()
        createMarker()
        map.setOnMyLocationClickListener(this)
        map.setOnMyLocationButtonClickListener(this)

        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun createMarker() {
        map.setOnMyLocationClickListener(this)
        val favoritePlace = LatLng(-12.0450845, -77.0301167)
        map.addMarker(MarkerOptions().position(favoritePlace).title("Mi Peru!"))
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(favoritePlace, 10f),
                4000,
                null
        )
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                )) {
            Toast.makeText(
                    requireContext(),
                    "Ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MapaIdaFragment.REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when(requestCode){
            MapaIdaFragment.REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.isMyLocationEnabled = true
                } else {
                    Toast.makeText(
                            requireContext(),
                            "Para activar la localización ve a ajustes y acepta los permisos",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "Boton pulsado", Toast.LENGTH_SHORT).show()
        Log.e("MyLocationButtonClick: ", "ENTRA A onMyLocationButtonClick")
        //colocar true si no quieres que te lleve a tu ubicacion
        return false
    }


    override fun onResume() {
        super.onResume()
        if (::map.isInitialized) {
            if (!isLocationPermissionGranted()) {
                map.isMyLocationEnabled = false
                Log.e(
                        "onResume: ",
                        "Para activar la localización ve a ajustes y acepta los permisos"
                )
                Toast.makeText(
                        requireContext(),
                        "Para activar la localización ve a ajustes y acepta los permisos",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onMyLocationClick(p0: Location) {
        LongIni = p0.longitude.toString()
        LatIni = p0.latitude.toString()
        Log.e("LONGITUD: ", LongIni.toString())
        Log.e("LATITUD: ", LatIni.toString())
        Toast.makeText(
                requireContext(),
                "Estás en ${p0.latitude}, ${p0.longitude}",
                Toast.LENGTH_SHORT
        ).show()

    }

    override fun onMapClick(p0: LatLng) {
        Log.e("latitude  nuevamc --> ",p0.latitude.toString())
        Log.e("longitude nuevamc --> ",p0.longitude.toString())
        map.clear()
        val nuevaUbicacion = LatLng(p0.latitude, p0.longitude)
        map.addMarker(MarkerOptions().position(nuevaUbicacion).title(""))
    }

    override fun onMapLongClick(p0: LatLng) {
        Log.e("latitude  nuevamlc --> ",p0.latitude.toString())
        Log.e("longitude nuevamlc --> ",p0.longitude.toString())
        map.clear()
        val nuevaUbicacion = LatLng(p0.latitude, p0.longitude)
        map.addMarker(MarkerOptions().position(nuevaUbicacion).title(""))
    }



}