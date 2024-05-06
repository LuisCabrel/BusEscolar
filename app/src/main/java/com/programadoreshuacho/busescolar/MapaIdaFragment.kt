package com.programadoreshuacho.busescolar

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
//import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.FindPlaceFromTextRequest
import com.programadoreshuacho.busescolar.Clases.ListaLocalizacion
import com.programadoreshuacho.busescolar.Clases.ListaLocalizacionProvider
import java.io.IOException




class MapaIdaFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
//class MapaIdaFragment : Fragment(), OnMapReadyCallback{
    private lateinit var map: GoogleMap
    private var LongIni: String?=""
    private var LatIni: String?=""
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private SearchView mapSearchView;


    /*var googleMap: GoogleMap? = null
    lateinit var placesAdapter: PlacesAdapter
    lateinit var mGeoDataClient: GeoDataClient

    class GeoDataClient {

    }

    lateinit var mLocationCallback: LocationCallback
    lateinit var location: Location
    lateinit var latLng: LatLng
    lateinit var mLocationRequest: LocationRequest
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mSettingsClient: SettingsClient
    lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private val REQUEST_CHECK_SETTINGS = 0x1
    var isAutoCompleteLocation = false
    val REQUEST_LOCATION = 1011
    var selectedLocation = ""
    val BOUNDS_INDIA = LatLngBounds(LatLng(23.63936, 68.14712), LatLng(28.20453, 97.34466))
*/
    companion object {
        const val LOCATION_REQUEST_CODE = 100
        const val REQUEST_CODE_LOCATION = 0
        private const val AUTOCOMPLETE_REQUEST_CODE = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_mapa_ida, container, false)
        //val apiKey = getStringFromResource(requireContext(), R.string.google_maps_key)
        //Places.initialize(requireContext(), apiKey)
        //placesClient = Places.createClient(requireContext())
        createMapFragment()
        // autocompleta direccion
      /*  mGeoDataClient = Places.getGeoDataClient(this, null);
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val loc = locationResult!!.lastLocation
                if(!isAutoCompleteLocation) {
                    location = loc
                    latLng = LatLng(location.latitude, location.longitude)
                    assignToMap()
                }
            }

        }
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((10 * 1000).toLong())        // 10 seconds, in milliseconds
                .setFastestInterval((6 * 1000).toLong()) // 1 second, in milliseconds

        mSettingsClient = LocationServices.getSettingsClient(requireActivity())
        val builder = LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()

        placesAdapter = PlacesAdapter(requireActivity(), android.R.layout.simple_list_item_1, mGeoDataClient, null, BOUNDS_INDIA)
        enter_place.setAdapter(placesAdapter)
        enter_place.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    cancel.visibility = View.VISIBLE
                } else {
                    cancel.visibility = View.GONE
                }
            }
        })

        enter_place.setOnItemClickListener({ parent, view, position, id ->
            //getLatLong(placesAdapter.getPlace(position))
            hideKeyboard()
            val item = placesAdapter.getItem(position)
            val placeId = item?.getPlaceId()
            val primaryText = item?.getPrimaryText(null)

            Log.i("Autocomplete", "Autocomplete item selected: " + primaryText)


            val placeResult = mGeoDataClient.getPlaceById(placeId)
            placeResult.addOnCompleteListener(object : OnCompleteListener<PlaceBufferResponse> {
                override fun onComplete(task: Task<PlaceBufferResponse>) {
                    val places = task.getResult();
                    val place = places.get(0)

                    isAutoCompleteLocation = true
                    latLng = place.latLng
                    assignToMap()

                    places.release()
                }

            })

            Toast.makeText(requireContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show()
        })
        cancel.setOnClickListener {
            enter_place.setText("")
        }
        */

        //*************/
        /*
        // Initialize the AutocompleteSupportFragment.
        //val autocompleteFragment =supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as? AutocompleteSupportFragment
        // Specify the types of place data to return.
        Log.i("TAG002", autocompleteFragment.toString())
        autocompleteFragment?.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))
        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("TAG01", "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("TAG02", "An error occurred: $status")
            }
        })
        */




        //val fields = listOf(Place.Field.ID, Place.Field.NAME)
        //val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
         //   .build(requireContext())
        //startAutocomplete.launch(intent)

        // Inicializar vistas
        /*val editTextDireccion = view.findViewById<EditText>(R.id.txtIdaDireccion)
        val buttonBuscar = view.findViewById<Button>(R.id.btnBuscarIda)

        // Configurar clic en el botón de búsqueda
        buttonBuscar.setOnClickListener {
            val direccion = editTextDireccion.text.toString()
            buscarUbicacion(direccion)
        }*/

        var mapSearchView = view.findViewById<SearchView>(R.id.mapSearch)

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

                    var sessionLocalizacionIda = requireActivity().getSharedPreferences("sessionLocalizacionDireccion",Context.MODE_PRIVATE)
                    var editor = sessionLocalizacionIda.edit()
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

        val btnAtras = view.findViewById<TextView>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            val fragmento2 = RutaidaFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragmento2)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view

    }

    private fun getStringFromResource(context: Context, @StringRes resId: Int): String {
        return try {
            context.getString(resId)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private val startAutocomplete = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        //Log.e("TAG0001", result.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            Log.e("TAG001", intent.toString())
            if (intent != null) {
                val place = Autocomplete.getPlaceFromIntent(intent)
                Log.i("TAG1", "Place: ${place.name}, ${place.id}")
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // The user canceled the operation.
            Log.i("TAG2", "User canceled autocomplete")
        }
    }


    private fun createMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        onMyLocationButtonClick()
        enableMyLocation()
        createMarker()
        map.setOnMyLocationClickListener(this)
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMapClickListener(this)
        map.setOnMapLongClickListener(this)
        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun createMarker() {
        map.setOnMyLocationClickListener(this)
        val favoritePlace = LatLng(LatIni?.toDoubleOrNull() ?: 0.0, LongIni?.toDoubleOrNull() ?: 0.0)
        map.addMarker(MarkerOptions().position(favoritePlace).title("Mi Peru!"))


        for (ubicacion in ListaLocalizacionProvider.listaLocalizacionList) {
            //Log.e("datalist " ,ubicacion)
            val latitud = ubicacion.latitud.toDoubleOrNull() ?: 0.0
            val longitud = ubicacion.longitud.toDoubleOrNull() ?: 0.0
            val latLng = LatLng(latitud, longitud)
            map.addMarker(MarkerOptions().position(latLng).title(ubicacion.nombre))
        }

        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(favoritePlace, 15f),
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
                REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> {
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