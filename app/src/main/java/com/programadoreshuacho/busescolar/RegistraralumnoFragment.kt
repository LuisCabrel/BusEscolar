package com.programadoreshuacho.busescolar

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
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
import com.programadoreshuacho.busescolar.Variables.VariablesGlobales
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*


class RegistraralumnoFragment : Fragment() {
    private var idConductor:String? =""
    private var idMovilidad:String? =""
    private var URrlGlobal: String=""
    private var cbotipodoc:String? =null
    private var codigo: String?=""
    private var idPadre: String?=""
    private var imagenbaseperfil: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var varGlobal =requireContext().applicationContext as VariablesGlobales
        URrlGlobal = varGlobal.urlApi
        var variablesSession = requireActivity().getSharedPreferences("sessionData", Context.MODE_PRIVATE)
        idConductor = variablesSession.getString("idusuario","");
        codigo = variablesSession.getString("codigo","");
        var variablesSessionPadre = requireActivity().getSharedPreferences("sessionDataPadre", Context.MODE_PRIVATE)
        idPadre = variablesSessionPadre.getString("idPadre","");
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

        val txtDireccionRecojoAlumno = view.findViewById<EditText>(R.id.txtDireccionRecojoAlumno)
        txtDireccionRecojoAlumno.setOnClickListener {
            // Aquí puedes llevar a cabo la acción que desees, como levantar un fragment

            val fragmento2 = MapaIdaFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragmento2)
            transaction.addToBackStack(null)
            transaction.commit()

        }

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

    private fun guardarDatosAlumno(view:View): Unit  {
        val url =URrlGlobal+"alumno/register"
        val txtNombreAlumno = view.findViewById<EditText>(R.id.txtNombreAlumno).text.toString()
        val txtApellidoAlumno = view.findViewById<EditText>(R.id.txtApellidoAlumno).text.toString()
        val cbo_tipoDocAlumno = cbotipodoc.toString()
        val txtDocumentoAlumno = view.findViewById<EditText>(R.id.txtDocumentoAlumno).text.toString()
        val txtDireccionRecojoAlumno = view.findViewById<EditText>(R.id.txtDireccionRecojoAlumno).text.toString()
        val txtDireccionDestinoAlumno = view.findViewById<EditText>(R.id.txtDireccionDestinoAlumno).text.toString()


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
                parametros.put("direccionRecojoAlumno", txtDireccionRecojoAlumno)
                parametros.put("direccionDestinoAlumno", txtDireccionDestinoAlumno)
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
            Log.d("decodificacionImagen",byteArray.toString())
            Log.d("imageBitmap",imageBitmap.toString())
            // Codifica el array de bytes a Base64
            imagenbaseperfil = Base64.encodeToString(byteArray, Base64.DEFAULT)
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d("decodificacionImagen",base64Image)
            //val perfil = view?.findViewById<EditText>(R.id.txtImgbase64Perfil)
            //perfil?.setText(base64Image)

            val imageView = view?.findViewById<ImageView>(R.id.imgAlumno)
            imageView?.setImageBitmap(imageBitmap)
        }

    }


}