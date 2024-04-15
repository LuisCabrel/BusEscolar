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
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
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
import java.io.IOException




class MapaIdaFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
//class MapaIdaFragment : Fragment(), OnMapReadyCallback{
    private lateinit var map: GoogleMap
    private var LongIni: String?=""
    private var LatIni: String?=""
    private lateinit var placesClient: PlacesClient
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



    //METODOS PARA BUSCAR DIRECCION
    //https://www.youtube.com/watch?v=QcyaICJ9CNA
    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar Places API
        Places.initialize(requireContext(), "AIzaSyA5Fsqqadin4ByFxxPM_KsQlFJm3s5jPQ4")
        placesClient = Places.createClient(this)

        // Inicializar vistas
        val editTextDireccion = view.findViewById<EditText>(R.id.txtIdaDireccion)
        val buttonBuscar: Button = view.findViewById(R.id.button_buscar)

        // Configurar clic en el botón de búsqueda
        buttonBuscar.setOnClickListener {
            val direccion = editTextDireccion.text.toString()
            buscarUbicacion(direccion)
        }
    }
*/
    //@SuppressLint("MissingPermission")
    /*private fun buscarUbicacion(direccion: String) {
        // Definir los campos de la ubicación que deseas obtener
        val fields = listOf(Place.Field.LAT_LNG, Place.Field.NAME,Place.Field.ADDRESS)

        // Crear una solicitud para buscar el lugar
        Log.e("direccion",direccion.toString())
        val request = FindCurrentPlaceRequest.newInstance(direccion, fields)

        // Ejecutar la solicitud de búsqueda
        placesClient.findCurrentPlace(request).addOnSuccessListener { response ->
            val place = response.placeLikelihoods[0].place
            val latLng = place.latLng
            if (latLng != null) {
                val latitud = latLng.latitude
                val longitud = latLng.longitude
                mostrarUbicacion(latitud, longitud)
            } else {
                Toast.makeText(requireContext(), "No se pudo encontrar la ubicación para la dirección: $direccion", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Error al buscar la ubicación: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }*/

    /*private fun buscarUbicacion(direccion: String) {
        // Obtener las coordenadas de latitud y longitud a partir de la dirección
        val latLng = obtenerCoordenadasDesdeDireccion(direccion)
        if (latLng != null) {
            // Definir los campos de la ubicación que deseas obtener
            val fields = listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)

            // Crear una solicitud para buscar el lugar con bias en la ubicación obtenida
            val bounds = RectangularBounds.newInstance(latLng, latLng)
            val bias = FindPlaceFromTextRequest.LocationBias.bounds(bounds)
            val request = FindCurrentPlaceRequest.newInstance(fields).locationBias(bias)

            // Ejecutar la solicitud de búsqueda
            placesClient.findCurrentPlace(request)
                .addOnSuccessListener { response ->
                    // Manejar la respuesta exitosa aquí
                }
                .addOnFailureListener { exception ->
                    // Manejar la falla aquí
                }
        } else {
            // Mostrar mensaje de error indicando que la dirección no es válida
        }
    }*/

  /*  private fun obtenerCoordenadasDesdeDireccion(direccion: String): LatLng? {
        val geocoder = Geocoder(requireContext()) // Si estás en un fragmento, utiliza requireContext(), si estás en una actividad, utiliza this
        val resultados = geocoder.getFromLocationName(direccion, 1)
        return if (resultados.isNotEmpty()) {
            LatLng(resultados[0].latitude, resultados[0].longitude)
        } else {
            null
        }
    }

    private fun mostrarUbicacion(latitud: Double, longitud: Double) {
        val mensaje = "Latitud: $latitud, Longitud: $longitud"
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }
    */



    /*
    private fun assignToMap() {
        googleMap?.clear()

        val options = MarkerOptions()
                .position(latLng)
                .title("My Location")
        googleMap?.apply {
            addMarker(options)
            moveCamera(CameraUpdateFactory.newLatLng(latLng))
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()?.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    location = task.getResult()
                    latLng = LatLng(location.latitude, location.longitude)
                    assignToMap()

                } else {
                    Log.w("Location", "Failed to get location.")
                }
            }
        } catch (unlikely: SecurityException) {
            Log.e("Location", "Lost location permission." + unlikely)
        }

    }

    private fun initLocation() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            getLastLocation()
            try {

                mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                        .addOnSuccessListener(this, object : OnSuccessListener<LocationSettingsResponse> {
                            override fun onSuccess(p0: LocationSettingsResponse?) {
                                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                        mLocationCallback, Looper.myLooper());
                            }

                        }).addOnFailureListener(this, object : OnFailureListener {
                            override fun onFailure(p0: java.lang.Exception) {
                                val statusCode = (p0 as ApiException).getStatusCode();
                                when (statusCode) {
                                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                        Log.i("Location", "Location settings are not satisfied. Attempting to upgrade " +
                                                "location settings ");
                                        try {
                                            // Show the dialog by calling startResolutionForResult(), and check the
                                            // result in onActivityResult().
                                            val rae = p0 as ResolvableApiException
                                            rae.startResolutionForResult(requireContext() as Activity, REQUEST_CHECK_SETTINGS);
                                        } catch (sie: IntentSender.SendIntentException) {
                                            Log.i("Location", "PendingIntent unable to execute request.");
                                        }
                                    }

                                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                                        Toast.makeText(requireContext(), "Location settings are inadequate, and cannot be \"+\n" +
                                                "                                    \"fixed here. Fix in Settings.", Toast.LENGTH_LONG).show();


                                }
                            }

                        })

            } catch (unlikely: SecurityException) {
                Log.e("Location", "Lost location permission. Could not request updates. " + unlikely)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onMapReady(p0: GoogleMap?) {
        Log.v("googleMap", "googleMap==" + googleMap)
        googleMap = p0
        googleMap?.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        googleMap?.getUiSettings()?.apply {
            isZoomControlsEnabled = false
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
        }
    }


    /* To hide Keyboard */
    fun hideKeyboard() {
        try {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initLocation()
            } else {
                Log.e("permisos","permiso denegado onRequestPermissionsResult")
                //Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)

    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            initLocation()
        } else {
            requestPermissions();
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        TODO("Not yet implemented")
    }

    override fun onMyLocationClick(p0: Location) {
        TODO("Not yet implemented")
    }
*/

}