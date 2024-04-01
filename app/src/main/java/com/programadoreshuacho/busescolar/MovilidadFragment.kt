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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.programadoreshuacho.busescolar.Variables.VariablesGlobales
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*


class MovilidadFragment : Fragment() {
    private var imagenbaseFrontal: String? = ""
    private var imagenbaseLado: String? = ""
    private var idConductor:String? =""
    private var idMovilidad:String? =""
    private var URrlGlobal: String=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var varGlobal =requireContext().applicationContext as VariablesGlobales
        URrlGlobal = varGlobal.urlApi
        var variablesSession = requireActivity().getSharedPreferences("sessionData", Context.MODE_PRIVATE)
        idConductor = variablesSession.getString("idusuario","");

        val view = inflater.inflate(R.layout.fragment_movilidad, container, false)
        Log.d("FRAGMENT","XXXXX".toString())

        listarDataBus(view)

        //TOMADO FOTO
        val btnCamaraFrontal = view.findViewById<Button>(R.id.btnRegistrarFrontalBus)
        btnCamaraFrontal.setOnClickListener {
            startForResultFrontal.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        val btnCamaraLado =view.findViewById<Button>(R.id.btnRegistrarLadoBus)
        btnCamaraLado.setOnClickListener {
            Log.d("BTNIMAGEN","XXXXX".toString())
            startForResultLado.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarBus)
        btnGuardar.setOnClickListener {
            guardarDatos(view)
        }
        return view
    }

    fun listarDataBus(view:  View): Unit  {
        val txtPlaca = view.findViewById<EditText>(R.id.txtPlaca)
        val txtColorAuto = view.findViewById<EditText>(R.id.txtColorAuto)
        val txtCantAsientos = view.findViewById<EditText>(R.id.txtCantAsientos)
        val txtMarca = view.findViewById<EditText>(R.id.txtMarca)
        val txtModelo = view.findViewById<EditText>(R.id.txtModelo)
        val txtDirInicioRuta = view.findViewById<EditText>(R.id.txtDirInicioRuta)
        val txtimageLadoBus = view.findViewById<ImageView>(R.id.imageLadoBus)
        val txtimageFrontalBus = view.findViewById<ImageView>(R.id.imageFrontalBus)

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Esperando datos...")
        progressDialog.show();

        var variablesSessionBus = requireActivity().getSharedPreferences("sessionDataBus", Context.MODE_PRIVATE)
        idMovilidad = variablesSessionBus.getString("idmovilidad","");
        txtPlaca.setText(variablesSessionBus.getString("placa","").toString());
        txtColorAuto.setText(variablesSessionBus.getString("color","").toString());
        txtCantAsientos.setText(variablesSessionBus.getString("asiento","").toString());
        txtMarca.setText(variablesSessionBus.getString("marca","").toString());
        txtModelo.setText(variablesSessionBus.getString("modelo","").toString());
        txtDirInicioRuta.setText(variablesSessionBus.getString("direccion","").toString());
        Glide.with(this).load(variablesSessionBus.getString("foto_lado","").toString()).into(txtimageLadoBus)
        Glide.with(this).load(variablesSessionBus.getString("foto_frontal","").toString()).into(txtimageFrontalBus)
        progressDialog.dismiss();
    }

    private val startForResultFrontal = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
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
            imagenbaseFrontal = Base64.encodeToString(byteArray, Base64.DEFAULT)
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            val imageView = view?.findViewById<ImageView>(R.id.imageFrontalBus)
            imageView?.setImageBitmap(imageBitmap)
        }

    }

    private val startForResultLado = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
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
            imagenbaseLado=base64Image
            val imageView = view?.findViewById<ImageView>(R.id.imageLadoBus)
            imageView?.setImageBitmap(imageBitmap)
        }

    }

    private fun guardarDatos(view:View): Unit  {
        val url =URrlGlobal+"bus/register"
        val txtPlaca = view.findViewById<EditText>(R.id.txtPlaca).text.toString()
        val txtColorAuto = view.findViewById<EditText>(R.id.txtColorAuto).text.toString()
        val txtCantAsientos = view.findViewById<EditText>(R.id.txtCantAsientos).text.toString()
        val txtMarca = view.findViewById<EditText>(R.id.txtMarca).text.toString()
        val txtModelo = view.findViewById<EditText>(R.id.txtModelo).text.toString()
        val txtDirInicioRuta = view.findViewById<EditText>(R.id.txtDirInicioRuta).text.toString()

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Guardando datos...")
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
                        Log.e("RESPONSE", "Error al analizar la respuesta JSON-MOVILIDAD: " + e.message);
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

                parametros.put("idUsuario", idConductor.toString())
                parametros.put("idMovilidad", idMovilidad.toString())
                parametros.put("placa", txtPlaca)
                parametros.put("color", txtColorAuto)
                parametros.put("asiento", txtCantAsientos)
                parametros.put("marca", txtMarca)
                parametros.put("modelo", txtModelo)
                parametros.put("direccion", txtDirInicioRuta)
                parametros.put("imgfrontal", imagenbaseFrontal.toString())
                parametros.put("imglado", imagenbaseLado.toString())
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
            fragmentBuses()
        }

        alertDialog.setView(promptView)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun fragmentBuses(): Unit {
        val fragmento2 = BusFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragmento2)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}