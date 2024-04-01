package com.programadoreshuacho.busescolar

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.util.*
import com.bumptech.glide.Glide
import com.programadoreshuacho.busescolar.Variables.VariablesGlobales
import org.json.JSONObject

class PerfilconductoreditarFragment : Fragment() {
    private lateinit var dataSession: SharedPreferences
    private var imagenbaseperfilEditar: String? = ""
    private var imagenbaselicenciaEditar: String? = ""
    private var idConductor:String? =null
    private var tipo:String? =null
    private var cbotipodoc:String? =null
    private var URrlGlobal: String=""
    //private var listener: SpinnerSelectionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_perfilconductoreditar, container, false)
        var varGlobal =requireContext().applicationContext as VariablesGlobales
        URrlGlobal = varGlobal.urlApi

        listarDatosConductor(view)

        //TOMADO FOTO
        val btnCamaraPerfil = view.findViewById<Button>(R.id.btnFotoPerfilConductor)
        btnCamaraPerfil.setOnClickListener {
            startForResultEditar.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        val btnCamaraLicencia =view.findViewById<Button>(R.id.btnFotoLicenciaConductor)
        btnCamaraLicencia.setOnClickListener {
            startForResultLicenciaEditar.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        val btnEditar =view.findViewById<Button>(R.id.btnEditarRegistroConductor)
        btnEditar.setOnClickListener {
            editaConductor(view)
        }
        return view
    }

    private fun editaConductor(view:  View) {
        val url =URrlGlobal+"editar/driver"
        val txtNombres = view.findViewById<EditText>(R.id.txtNombresConductor).text.toString()
        val txtApellidos = view.findViewById<EditText>(R.id.txtApellidosConductor).text.toString()
        //val cbo_tipoDoc =  spinner.selectedItem.toString()
        val txtDocumento = view.findViewById<EditText>(R.id.txtDocumentoConductor).text.toString()
        val txtDireccion = view.findViewById<EditText>(R.id.txtDireccionConductor).text.toString()
        val txtCorreo = view.findViewById<EditText>(R.id.txtCorreoConductor).text.toString()
        val txtCelular = view.findViewById<EditText>(R.id.txtCelularConductor).text.toString()
        //val txtPassword = view.findViewById<EditText>(R.id.txtPassword).text.toString()
        //val txtimgPerfil = view.imagenbaseperfilEditar.toString() //findViewById<EditText>(R.id.txtImgbase64Perfil).text.toString()
        //val txtimgLicencia = view.imagenbaselicencia.toString()  //findViewById<EditText>(R.id.txtImgbase64Licencia).text.toString()
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Editando datos...")
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
                        Log.e("RESPONSE", "Error al analizar la respuesta JSON: " + e.message);
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

                parametros.put("id", idConductor.toString())
                parametros.put("nombres", txtNombres)
                parametros.put("apellidos", txtApellidos)
                parametros.put("numdoc", txtDocumento)
                parametros.put("tipodoc", cbotipodoc.toString())
                parametros.put("direccion", txtDireccion)
                parametros.put("correo", txtCorreo)
                parametros.put("celular", txtCelular)
                //parametros.put("password", txtPassword)
                parametros.put("imgperfil", imagenbaseperfilEditar.toString())
                parametros.put("imglicencia", imagenbaselicenciaEditar.toString())
                //Log.d("DATOS ENVIADOS", parametros.toString())
                return parametros
            }
        }
        queue.add(resultadoPost)
    }

    private val startForResultEditar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val resultMap = HashMap<String, Any>()

        if (result.resultCode == Activity.RESULT_OK) {

            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            // Convierte el Bitmap a un array de bytes
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Log.d("decodificacionImagen",byteArray.toString())
            Log.d("imageBitmap",imageBitmap.toString())
            // Codifica el array de bytes a Base64
            imagenbaseperfilEditar = Base64.encodeToString(byteArray, Base64.DEFAULT)
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d("decodificacionImagen",base64Image)
            //val perfil = view?.findViewById<EditText>(R.id.txtImgbase64Perfil)
            //perfil?.setText(base64Image)

            val imageView = view?.findViewById<ImageView>(R.id.imgPerfilConductor)
            imageView?.setImageBitmap(imageBitmap)
        }

    }

    private val startForResultLicenciaEditar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val resultMap = HashMap<String, Any>()

        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap

            // Convierte el Bitmap a un array de bytes
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Codifica el array de bytes a Base64
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
            imagenbaselicenciaEditar=base64Image
            //val licencia = view?.findViewById<EditText>(R.id.txtImgbase64Licencia)
            //licencia?.setText(base64Image)

            val imageView = view?.findViewById<ImageView>(R.id.imgLicenciaConductor)
            imageView?.setImageBitmap(imageBitmap)
        }

    }

    /*fun cboSpinner(view:  View): Unit {
        val spinner = view.findViewById<Spinner>(R.id.cbo_tipoDocConductor)
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
    }*/



    fun listarDatosConductor(view:  View): Unit {
        val nombreConductor = view.findViewById<TextView>(R.id.txtNombresConductor)
        val apellidoConductor = view.findViewById<TextView>(R.id.txtApellidosConductor)
        //val tipoDocConductor = cbotipodoc//view.findViewById<TextView>(R.id.cbo_tipoDocConductor)
        val documentoConductor = view.findViewById<TextView>(R.id.txtDocumentoConductor)
        val direccionConductor = view.findViewById<TextView>(R.id.txtDireccionConductor)
        val correoConductor = view.findViewById<TextView>(R.id.txtCorreoConductor)
        val celularConductor = view.findViewById<TextView>(R.id.txtCelularConductor)
        val txtimgPerfilConductor = view.findViewById<ImageView>(R.id.imgPerfilConductor)
        val txtimgLicenciaConductor = view.findViewById<ImageView>(R.id.imgLicenciaConductor)


        dataSession = requireActivity().getSharedPreferences("sessionDataConductor", Context.MODE_PRIVATE)
        idConductor = dataSession.getString("idusuario","");
        val nombres=dataSession.getString("nombres","");
        val apellidos=dataSession.getString("apellidos","");
        val tipoDoc=dataSession.getString("tipoDoc","");
        val numeroDoc=dataSession.getString("numDoc","");
        val direccion=dataSession.getString("direccion","");
        val celular = dataSession.getString("celular","");
        val email = dataSession.getString("email","");
        tipo = dataSession.getString("tipo","");
        val imgPerfil =dataSession.getString("imgPerfil","");
        val imgLicencia =dataSession.getString("imgLicencia","");

        nombreConductor.setText(nombres.toString());
        apellidoConductor.setText(apellidos.toString());
        //documentoConductor.setText(tipoDoc.toString()+": "+numeroDoc.toString());
        documentoConductor.setText(numeroDoc.toString());
        correoConductor.setText(email.toString());
        celularConductor.setText(celular.toString());
        direccionConductor.setText(direccion.toString());
        Glide.with(this).load(imgPerfil.toString()).into(txtimgPerfilConductor)
        Glide.with(this).load(imgLicencia.toString()).into(txtimgLicenciaConductor)
        //Glide.with(this).load("https://upload.wikimedia.org/wikipedia/commons/8/87/Avatar_poe84it.png").into(txtimgPerfilConductor)
        //Glide.with(this).load("https://upload.wikimedia.org/wikipedia/commons/8/87/Avatar_poe84it.png").into(txtimgLicenciaConductor)
        cboSpinner(view,tipoDoc.toString())
    }

    fun cboSpinner(view: View, opcionSeleccionada: String) {
        val spinner = view.findViewById<Spinner>(R.id.cbo_tipoDocConductor)
        val lista = resources.getStringArray(R.array.opciones)
        val adaptador = ArrayAdapter(requireContext(), R.layout.spinner_lista_style, lista)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptador

        // Buscar la posición de la opción seleccionada
        val posicionSeleccionada = lista.indexOf(opcionSeleccionada)
        Log.e("lista",lista.toString())
        Log.e("cbo",opcionSeleccionada)
        // Si la opción seleccionada se encuentra en la lista, seleccionarla en el Spinner
        if (posicionSeleccionada != -1) {
            //Log.e("cbo",posicionSeleccionada)
            spinner.setSelection(posicionSeleccionada)
        }



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("POSITION: ", lista[p2])
                cbotipodoc = lista[p2]
                //listener?.onItemSelected(selectedOption)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Aquí puedes manejar la situación cuando no se selecciona nada
            }
        }
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
            //val intent = Intent(this@Panel, MainActivity::class.java)
            //startActivity(intent)
            fragmentPerfilConductor()
        }

        alertDialog.setView(promptView)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun fragmentPerfilConductor(): Unit {
        val fragmento2 = PerfilconductorFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragmento2)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}