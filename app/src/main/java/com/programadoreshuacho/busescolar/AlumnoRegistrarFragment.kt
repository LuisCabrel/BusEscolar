package com.programadoreshuacho.busescolar

import android.Manifest
import android.animation.LayoutTransition
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.programadoreshuacho.busescolar.Variables.VariablesGlobales
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class RegistraralumnoFragment : Fragment(), OnMapReadyCallback , GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener{
    private var idConductor:String? =""
    private var idMovilidad:String? =""
    private var URrlGlobal: String=""
    private var cbotipodoc:String? =null
    private var codigo: String?=""
    private var idPadre: String?=""
    private var imagenbaseperfil: String? = ""

    /*VARIABLES DE MAPA RECOJO*/
    private lateinit var map: GoogleMap
    private var LongIni: String?=""
    private var LatIni: String?=""
    private var dirIni: String?=""
    /*VARIABLES DE MAPA COLEGIO*/
    private lateinit var mapColegio: GoogleMap
    private var LongColegio: String?=""
    private var LatColegio: String?=""
    private var dirColegio: String?=""
    private var paramMap:Int?=0

    companion object {
        const val LOCATION_REQUEST_CODE = 100
        const val REQUEST_CODE_LOCATION = 0
        private const val AUTOCOMPLETE_REQUEST_CODE = 123
    }



    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        var varGlobal =requireContext().applicationContext as VariablesGlobales
        URrlGlobal = varGlobal.urlApi
        var variablesSession = requireActivity().getSharedPreferences("sessionData", Context.MODE_PRIVATE)
        idConductor = variablesSession.getString("idusuario", "");
        codigo = variablesSession.getString("codigo", "");
        var variablesSessionPadre = requireActivity().getSharedPreferences("sessionDataPadre", Context.MODE_PRIVATE)
        idPadre = variablesSessionPadre.getString("idPadre", "");
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_registraralumno, container, false)
        val view = inflater.inflate(R.layout.fragment_registraralumno, container, false)
        val spinner = view.findViewById<Spinner>(R.id.cbo_tipoDocAlumno)
        val lista = resources.getStringArray(R.array.opciones)
        val adaptador = ArrayAdapter(requireContext(), R.layout.spinner_lista_style, lista)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptador
        spinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("POSITION: ", lista[p2])
                cbotipodoc = lista[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        /**************************** CARD COLLAPSE DIRECCION RECOJO **********************************************/
        var detalleCardRecojo = view.findViewById<LinearLayout>(R.id.layoutFragmentRecojo)
        var layout = view.findViewById<LinearLayout>(R.id.layoutRecojo)
        var expandIda = view.findViewById<CardView>(R.id.cardRecojoExpandable)
        layout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        expandIda.setOnClickListener {
            paramMap=1
            createMapFragment()
            val v = if (detalleCardRecojo.visibility == View.GONE) View.VISIBLE else View.GONE
            detalleCardRecojo.visibility = v
        }

        var mapSearchViewRecojo = view.findViewById<SearchView>(R.id.mapSearchRecojo)

        mapSearchViewRecojo.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = mapSearchViewRecojo.query.toString()
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
                    Log.e("address  --> ", address.toString())
                    Log.e("latitude  --> ", address.latitude.toString())
                    Log.e("longitude  --> ", address.longitude.toString())
                    Log.e("mapSearchView  --> ", mapSearchViewRecojo.query.toString())
                    dirIni = mapSearchViewRecojo.query.toString()
                    LatIni = address.latitude.toString()
                    LongIni = address.longitude.toString()

                    map.addMarker(MarkerOptions().position(latlng).title(location))
                    map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(latlng, 18f),
                            4000,
                            null
                    )


                    /*var sessionLocalizacionIda = requireActivity().getSharedPreferences("sessionLocalizacionDireccion", Context.MODE_PRIVATE)
                    var editor = sessionLocalizacionIda.edit()
                    editor.clear()
                    editor.putString("address", address.toString())
                    editor.putString("latitud", address.latitude.toString())
                    editor.putString("longitud", address.longitude.toString())
                    editor.putString("direccion", mapSearchViewRecojo.query.toString())
                    editor.commit()*/
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        /************************** FIN CARD COLLAPSE DIRECCION RECOJO *******************************************/

        /************************* CARD COLLAPSE DIRECCION COLEGIO ***********************************************/
        var detalleCardColegio = view.findViewById<LinearLayout>(R.id.layoutFragmentColegio)
        var layoutColegio = view.findViewById<LinearLayout>(R.id.layoutColegio)
        var expandColegio = view.findViewById<CardView>(R.id.cardColegioExpandable)
        layoutColegio.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        expandColegio.setOnClickListener {
            paramMap=2
            createMapFragmentColegio()
            val v = if (detalleCardColegio.visibility == View.GONE) View.VISIBLE else View.GONE
            detalleCardColegio.visibility = v
        }

        var mapSearchViewColegio = view.findViewById<SearchView>(R.id.mapSearchColegio)

        mapSearchViewColegio.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val locationColegio = mapSearchViewColegio.query.toString()
                var addressListColegio: List<Address>? = null
                if (!locationColegio.isNullOrEmpty()) {
                    val geocoderColegio = Geocoder(context)
                    try {
                        addressListColegio = geocoderColegio.getFromLocationName(locationColegio, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    mapColegio.clear()
                    val addressColegio = addressListColegio?.get(0)
                    val latlngColegio = LatLng(addressColegio!!.latitude, addressColegio.longitude)
                    Log.e("address  --> ", addressColegio.toString())
                    Log.e("latitude  --> ", addressColegio.latitude.toString())
                    Log.e("longitude  --> ", addressColegio.longitude.toString())
                    Log.e("mapSearchView  --> ", mapSearchViewColegio.query.toString())
                    dirColegio = mapSearchViewColegio.query.toString()
                    LatColegio = addressColegio.latitude.toString()
                    LongColegio = addressColegio.longitude.toString()
                    mapColegio.addMarker(MarkerOptions().position(latlngColegio).title(locationColegio))
                    mapColegio.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(latlngColegio, 18f),
                            4000,
                            null
                    )

                    /*var sessionLocalizacionIda = requireActivity().getSharedPreferences("sessionLocalizacionDireccion",Context.MODE_PRIVATE)
                    var editor = sessionLocalizacionIda.edit()
                    editor.clear()
                    editor.putString("address",address.toString())
                    editor.putString("latitud",address.latitude.toString())
                    editor.putString("longitud",address.longitude.toString())
                    editor.putString("direccion",mapSearchViewRecojo.query.toString())
                    editor.commit()
                    */
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


        /******************************FIN CARD COLLAPSE DIRECCION COLEGIO ***************************************/

        /*
        val txtDireccionRecojoAlumno = view.findViewById<EditText>(R.id.txtDireccionRecojoAlumno)
        txtDireccionRecojoAlumno.setOnClickListener {
            // Aquí puedes llevar a cabo la acción que desees, como levantar un fragment

            val fragmento2 = MapaIdaFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragmento2)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        val txtDireccionColegioAlumno = view.findViewById<EditText>(R.id.txtDireccionDestinoAlumno)
        txtDireccionColegioAlumno.setOnClickListener {
            // Aquí puedes llevar a cabo la acción que desees, como levantar un fragment

            val fragmento2 = MapaColegioFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragmento2)
            transaction.addToBackStack(null)
            transaction.commit()

        }
        */
        val btnGuardarAlumno = view.findViewById<Button>(R.id.btnGuardarAlumno)
        btnGuardarAlumno.setOnClickListener {
            guardarDatosAlumno(view)
        }

        //TOMADO FOTO
        val btnCamaraPerfil = view.findViewById<Button>(R.id.btnFotoAlumno)
        btnCamaraPerfil.setOnClickListener {
            startForResultPerfilAlumno.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
        return view

    }

    private fun guardarDatosAlumno(view: View): Unit  {
        val url =URrlGlobal+"alumno/register"
        val txtNombreAlumno = view.findViewById<EditText>(R.id.txtNombreAlumno).text.toString()
        val txtApellidoAlumno = view.findViewById<EditText>(R.id.txtApellidoAlumno).text.toString()
        val cbo_tipoDocAlumno = cbotipodoc.toString()
        val txtDocumentoAlumno = view.findViewById<EditText>(R.id.txtDocumentoAlumno).text.toString()
        val txtNombreColegioAlumno = view.findViewById<EditText>(R.id.txtNombreColegioAlumno).text.toString()
        //val txtDireccionDestinoAlumno = view.findViewById<EditText>(R.id.txtDireccionDestinoAlumno).text.toString()


        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Guardando datos de Padre...")
        progressDialog.show();

        val queue= Volley.newRequestQueue(requireContext())
        var resultadoPost = object : StringRequest(Request.Method.POST, url,
                Response.Listener<String> { response ->
                    Log.d("RESPONSE", response.toString())
                    try {
                        var resp: Boolean;
                        val jsonResponse = JSONObject(response)

                        resp = jsonResponse.getBoolean("response")

                        Log.e("RESPONSE RESP:::::::", resp.toString());
                        if (resp) {
                            val message = jsonResponse.getString("message")
                            modalSuccess("${message}")
                        } else {
                            val messageObject = jsonResponse.getJSONObject("message")
                            val errorMessages = StringBuilder()
                            val keys = messageObject.keys()
                            while (keys.hasNext()) {
                                val key = keys.next()
                                val errorsArray = messageObject.getJSONArray(key)
                                for (i in 0 until errorsArray.length()) {
                                    val errorMessage = errorsArray.getString(i)
                                    errorMessages.append(" * $errorMessage\n")
                                }
                            }

                            progressDialog.dismiss();
                            modalAlerta(errorMessages.toString())

                        }

                    } catch (e: Exception) {
                        e.printStackTrace();
                        Log.e("RESPONSE", "Error al analizar la respuesta JSON-PADRE: " + e.message);
                        modalError("Error al analizar la respuesta JSON: " + e.message)
                    }
                    progressDialog.dismiss();
                }, Response.ErrorListener { error ->
            Log.d("ERROR EXCEPTION", error.toString())
            progressDialog.dismiss();
            modalError(error.toString())
        }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()

                parametros.put("idConductor", idConductor.toString())
                parametros.put("idPadre", idPadre.toString())
                parametros.put("nombreAlumno", txtNombreAlumno)
                parametros.put("apellidoAlumno", txtApellidoAlumno)
                parametros.put("tipoDocAlumno", cbo_tipoDocAlumno)
                parametros.put("documentoAlumno", txtDocumentoAlumno)
                parametros.put("direccionRecojoAlumno", dirIni.toString())
                parametros.put("latInicio", LatIni.toString())
                parametros.put("longInicio", LongIni.toString())
                parametros.put("nombreColegioAlumno", txtNombreColegioAlumno.toString())
                parametros.put("direccionDestinoAlumno", dirColegio.toString())
                parametros.put("latColegio", LatColegio.toString())
                parametros.put("longColegio", LongColegio.toString())
                parametros.put("codigo", codigo.toString())
                parametros.put("fotoPerfil", imagenbaseperfil.toString())
                Log.d("DATOS ENVIADOS", parametros.toString())
                return parametros
            }
        }
        queue.add(resultadoPost)
    }

    fun modalAlerta(message: String): Unit{
        val layoutInflater = LayoutInflater.from(requireContext())

        //Load Layout custom_alertdialog.xml

        //Load Layout custom_alertdialog.xml
        val promptView: View = layoutInflater.inflate(R.layout.modal_alerta, null)
        val alertDialog = AlertDialog.Builder(requireContext()).create()

        val btnCancelar = promptView.findViewById<View>(R.id.btn_alerta) as Button
        val txtAlerta = promptView.findViewById<TextView>(R.id.txt_warning)
        txtAlerta.setText(message);
        //val btnPositive = promptView.findViewById<View>(R.id.AlertDialog_Positivo) as Button

        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }

        //btnPositive.setOnClickListener { Log.d("AlertDialog", "btnPositive") }

        alertDialog.setView(promptView)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun modalError(message: String): Unit{
        val layoutInflater = LayoutInflater.from(requireContext())
        val promptView: View = layoutInflater.inflate(R.layout.modal_error, null)
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val btnCancelar = promptView.findViewById<View>(R.id.btn_error) as Button
        val txtError = promptView.findViewById<TextView>(R.id.txt_error)
        txtError.setText(message);
        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.setView(promptView)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun modalSuccess(message: String): Unit{
        val layoutInflater = LayoutInflater.from(requireContext())
        val promptView: View = layoutInflater.inflate(R.layout.modal_exito, null)
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val btnCerrar = promptView.findViewById<View>(R.id.btn_success) as Button
        val txtError = promptView.findViewById<TextView>(R.id.txt_success)
        txtError.setText(message);
        btnCerrar.setOnClickListener {
            alertDialog.dismiss()
            //fragmentBuses()
        }

        alertDialog.setView(promptView)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private val startForResultPerfilAlumno = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val resultMap = HashMap<String, Any>()

        if (result.resultCode == Activity.RESULT_OK) {

            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            // Convierte el Bitmap a un array de bytes
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Log.d("decodificacionImagen", byteArray.toString())
            Log.d("imageBitmap", imageBitmap.toString())
            // Codifica el array de bytes a Base64
            imagenbaseperfil = Base64.encodeToString(byteArray, Base64.DEFAULT)
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d("decodificacionImagen", base64Image)
            //val perfil = view?.findViewById<EditText>(R.id.txtImgbase64Perfil)
            //perfil?.setText(base64Image)

            val imageView = view?.findViewById<ImageView>(R.id.imgAlumno)
            imageView?.setImageBitmap(imageBitmap)
        }

    }

    /* FUNCIONES PARA MAPA DE DIRECCION RECOJO*/
    private fun createMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.e("paramMap  ", paramMap.toString())
        if(paramMap == 1){
            map = googleMap
            enableMyLocation()
            createMarker()
            map.setOnMyLocationClickListener(this)
            map.setOnMyLocationButtonClickListener(this)
            //map.setOnMapClickListener(this)
            //map.setOnMapLongClickListener(this)
            map.uiSettings.isZoomControlsEnabled = true
            map.setOnMapClickListener { latLng ->
                Log.e("lat_nuevadirclick  ", latLng.latitude.toString())
                Log.e("longitude_nuevadir  ", latLng.longitude.toString())
                map.clear()
                LongIni = latLng.latitude.toString()
                LatIni = latLng.latitude.toString()
                val nuevaUbicacion = LatLng(latLng.latitude, latLng.longitude)
                map.addMarker(MarkerOptions().position(nuevaUbicacion).title(""))
            }

            map.setOnMapLongClickListener { latLng ->
                Log.e("latitude_nuevadirlong  ", latLng.latitude.toString())
                Log.e("longitude_nuevadir  ", latLng.longitude.toString())
                map.clear()
                LongIni = latLng.latitude.toString()
                LatIni = latLng.latitude.toString()
                val nuevaUbicacion = LatLng(latLng.latitude, latLng.longitude)
                map.addMarker(MarkerOptions().position(nuevaUbicacion).title(""))
            }
        }else{

            mapColegio = googleMap
            enableMyLocationColegio()
            createMarkerColegio()
            mapColegio.setOnMyLocationClickListener(this)
            mapColegio.setOnMyLocationButtonClickListener(this)
            //mapColegio.setOnMapClickListener(this)
            //mapColegio.setOnMapLongClickListener(this)
            mapColegio.uiSettings.isZoomControlsEnabled = true
            mapColegio.setOnMapClickListener { latLng ->
                Log.e("lat_nuevacoleclick ", latLng.latitude.toString())
                Log.e("lon_nuevacole  ", latLng.longitude.toString())
                mapColegio.clear()
                LatColegio = latLng.latitude.toString()
                LongColegio = latLng.longitude.toString()
                val nuevaUbicacionCole = LatLng(latLng.latitude, latLng.longitude)
                mapColegio.addMarker(MarkerOptions().position(nuevaUbicacionCole).title(""))
            }

            mapColegio.setOnMapLongClickListener { latLng ->
                Log.e("latitude_nuevacolelong ", latLng.latitude.toString())
                Log.e("longitude_nuevacole  ", latLng.longitude.toString())
                mapColegio.clear()
                LatColegio = latLng.latitude.toString()
                LongColegio = latLng.longitude.toString()
                val nuevaUbicacion = LatLng(latLng.latitude, latLng.longitude)
                mapColegio.addMarker(MarkerOptions().position(nuevaUbicacion).title(""))
            }
        }
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

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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
                    mapColegio.isMyLocationEnabled = true
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
        if (::map.isInitialized && ::mapColegio.isInitialized) {
            if (!isLocationPermissionGranted()) {
                map.isMyLocationEnabled = false
                mapColegio.isMyLocationEnabled = false
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
        val location1 = map.myLocation ?: return
        val location2 = mapColegio.myLocation ?: return

        if (paramMap == 1) {
            LongIni = p0.longitude.toString()
            LatIni = p0.latitude.toString()
            Log.e("LONGITUD[1]: ", LongIni.toString())
            Log.e("LATITUD: ", LatIni.toString())
            Toast.makeText(
                    requireContext(),
                    "Estás en ${p0.latitude}, ${p0.longitude}",
                    Toast.LENGTH_SHORT
            ).show()
        } else if (paramMap == 2) {
            LongColegio = p0.longitude.toString()
            LatColegio = p0.latitude.toString()
            Log.e("LONGITUD[2]: ", LongIni.toString())
            Log.e("LATITUD: ", LatIni.toString())
            Toast.makeText(
                    requireContext(),
                    "Estás en ${p0.latitude}, ${p0.longitude}",
                    Toast.LENGTH_SHORT
            ).show()
        }


    }

    override fun onMapClick(p0: LatLng) {
        Log.e("onMapClick --> ", p0.toString())
        if (paramMap == 1) {
            Log.e("latitude [map1] --> ", p0.toString())
            Log.e("latitude  nuevamc --> ", p0.latitude.toString())
            Log.e("longitude nuevamc --> ", p0.longitude.toString())
            LongIni = p0.longitude.toString()
            LatIni = p0.latitude.toString()
            map.clear()
            val nuevaUbicacion = LatLng(p0.latitude, p0.longitude)
            map.addMarker(MarkerOptions().position(nuevaUbicacion).title(""))
        } else if (paramMap == 2) {
            Log.e("latitude [map2] --> ", p0.toString())
            Log.e("latitude  nuevamc --> ", p0.latitude.toString())
            Log.e("longitude nuevamc --> ", p0.longitude.toString())
            LongColegio = p0.longitude.toString()
            LatColegio = p0.latitude.toString()
            mapColegio.clear()
            val nuevaUbicacion = LatLng(p0.latitude, p0.longitude)
            mapColegio.addMarker(MarkerOptions().position(nuevaUbicacion).title(""))
        }

    }

    override fun onMapLongClick(p0: LatLng) {
        if (paramMap == 1) {
            Log.e("latitude oooxxx --> ", p0.toString())
            Log.e("latitude  nuevamlc --> ", p0.latitude.toString())
            Log.e("longitude nuevamlc --> ", p0.longitude.toString())
            LongIni = p0.longitude.toString()
            LatIni = p0.latitude.toString()
            map.clear()
            val nuevaUbicacion = LatLng(p0.latitude, p0.longitude)
            map.addMarker(MarkerOptions().position(nuevaUbicacion).title(""))
        }else{
            LongColegio = p0.longitude.toString()
            LatColegio = p0.latitude.toString()
            mapColegio.clear()
            val nuevaUbicacion = LatLng(p0.latitude, p0.longitude)
            mapColegio.addMarker(MarkerOptions().position(nuevaUbicacion).title(""))
        }
    }

    /***************** FUNCIONES PARA MAPA DE COLEGIO *************/
    private fun createMapFragmentColegio() {
        val mapFragmentColegio = childFragmentManager.findFragmentById(R.id.fragmentMapColegio) as SupportMapFragment
        mapFragmentColegio.getMapAsync(this)

    }
    private fun createMarkerColegio() {
        mapColegio.setOnMyLocationClickListener(this)
        val favoritePlace = LatLng(-12.0450845, -77.0301167)
        mapColegio.addMarker(MarkerOptions().position(favoritePlace).title("Mi Peru!"))
        mapColegio.animateCamera(
                CameraUpdateFactory.newLatLngZoom(favoritePlace, 10f),
                4000,
                null
        )
    }

    private fun enableMyLocationColegio() {
        if (!::mapColegio.isInitialized) return
        if (isLocationPermissionGranted()) {
            mapColegio.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }




}