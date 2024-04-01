package com.programadoreshuacho.busescolar

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.util.Log
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.programadoreshuacho.busescolar.Adapter.AdapterTab
import com.programadoreshuacho.busescolar.Variables.VariablesGlobales
import org.json.JSONArray
import org.json.JSONObject
import java.sql.DriverManager.println


class PerfilconductorFragment : Fragment() {
    private lateinit var dataSession: SharedPreferences
    private var URrlGlobal: String=""
    private var valJson: Boolean= false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_perfilconductor, container, false)
        val view = inflater.inflate(R.layout.fragment_perfilconductor, container, false)
        var varGlobal =requireContext().applicationContext as VariablesGlobales
        URrlGlobal = varGlobal.urlApi
        val imageView = view.findViewById<ImageView>(R.id.imgPerfilConductor)
        // Traer la imagen al frente
        imageView.bringToFront()
        val btnEditar = view.findViewById<Button>(R.id.btnEditarConductor)
        listarDatosConductor(view)

        btnEditar.setOnClickListener {
            fragmentEditar(view)
        }

        return view
    }

    fun fragmentEditar(view:  View): Unit {
        val fragmento2 = PerfilconductoreditarFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragmento2)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun listarDatosConductor(view:  View): Unit {
        val nombreConductor = view.findViewById<TextView>(R.id.txtPerfilNombres)
        val documentoConductor = view.findViewById<TextView>(R.id.txtPerfilDocumento)
        val correoConductor = view.findViewById<TextView>(R.id.txtPerfilCorreo)
        val celularConductor = view.findViewById<TextView>(R.id.txtPerfilCelular)
        val direccionConductor = view.findViewById<TextView>(R.id.txtPerfilDireccion)
        val tipoPerfil = view.findViewById<TextView>(R.id.txtPerfilTipo)
        val imagenPerfil = view.findViewById<ImageView>(R.id.imgPerfilConductor)


        dataSession = requireActivity().getSharedPreferences("sessionData", Context.MODE_PRIVATE)
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando datos...")
        progressDialog.show();
        /*val nombres=dataSession.getString("nombres","");
        val apellidos=dataSession.getString("apellidos","");
        val tipoDoc=dataSession.getString("tipoDoc","");
        val numeroDoc=dataSession.getString("numDoc","");
        val direccion=dataSession.getString("direccion","");
        val celular = dataSession.getString("celular","");
        val email = dataSession.getString("email","");
        val tipo = dataSession.getString("tipo","");
        val tipoPerfilSession:String
        if(tipo.toString() == "1"){
            tipoPerfilSession = "Conductor"
        }else{
            tipoPerfilSession=""
        }*/
        val url =URrlGlobal+"user/"+dataSession.getString("idusuario","")
        val queue= Volley.newRequestQueue(requireContext())
        val resultadoGet = object : StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Manejar la respuesta aquí
                    Log.e("RESPONSE", response.toString())
                    try {
                        var resp: Boolean;
                        val jsonResponse = JSONObject(response)
                        resp = jsonResponse.getBoolean("response")

                        if (resp) {

                            dataSharedPreferences(response.toString())
                            val dataObj = jsonResponse.getJSONObject("datos")
                            Log.e("tiooo",dataObj.getString("imgPerfil").toString())
                            tipoPerfil.setText(dataObj.getString("tipo_usuario_nombre").toString());
                            nombreConductor.setText(dataObj.getString("nombres")+" "+dataObj.getString("apellidos").toString());
                            documentoConductor.setText(dataObj.getString("tipo_documento")+": "+dataObj.getString("num_documento").toString());
                            correoConductor.setText(dataObj.getString("email").toString());
                            celularConductor.setText(dataObj.getString("celular").toString());
                            direccionConductor.setText(dataObj.getString("direccion_domicilio").toString());//
                            Glide.with(this).load(dataObj.getString("imgLicencia").toString()).into(imagenPerfil)

                            progressDialog.dismiss();
                        } else {
                            val message = jsonResponse.getString("message")
                            validarJson(message.toString())
                            if(valJson){
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
                            }else{
                                progressDialog.dismiss();
                                modalAlerta(message.toString())
                            }
                        }

                    }catch (e: Exception) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        modalError("Error al analizar la respuesta JSON-listarDatosConductor: " + e.message)

                    }
                },
                Response.ErrorListener { error ->
                    // Manejar errores de la solicitud aquí
                    println("Error: ${error.message}")
                }) {
            // Aquí puedes anular métodos si necesitas enviar parámetros adicionales, encabezados, etc.
            /*override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }*/
        }
        queue.add(resultadoGet)

       /* tipoPerfil.setText(tipoPerfilSession);
        nombreConductor.setText(nombres.toString()+" "+apellidos.toString());
        documentoConductor.setText(tipoDoc.toString()+": "+numeroDoc.toString());
        correoConductor.setText(email.toString());
        celularConductor.setText(celular.toString());
        direccionConductor.setText(direccion.toString());*/
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

    fun validarJson(cadena: String) {
        try {
            val jsonArray = JSONArray(cadena)
            // Si no hay una excepción al crear el JSONArray, entonces es un JSONArray válido
            Log.d("msg","La cadena es un JSONArray válido.")
            valJson = false;
        } catch (e: Exception) {
            try {
                val jsonObject = JSONObject(cadena)
                // Si no hay una excepción al crear el JSONObject, entonces es un JSONObject válido
                Log.d("msg","La cadena es un JSONObject válido.")
                valJson = true;
            } catch (e: Exception) {
                // Si se lanza una excepción en ambos casos, la cadena no es un JSONArray ni un JSONObject válido
                Log.d("msg","La cadena no es ni un JSONArray ni un JSONObject válido." + e.message)
                valJson = false;
            }
        }
    }

    fun dataSharedPreferences(data: String):Unit{
        val jsonResponse = JSONObject(data)
        //dataSession = requireActivity().getSharedPreferences("sessionData", Context.MODE_PRIVATE)
        var variablesSession = requireActivity().getSharedPreferences("sessionDataConductor",Context.MODE_PRIVATE)

        val dataObj = jsonResponse.getJSONObject("datos")

        val nombres = dataObj.getString("nombres")
        val apellidos = dataObj.getString("apellidos")
        val tipoDoc = dataObj.getString("tipo_documento")
        val numDoc = dataObj.getString("num_documento")
        val direccion = dataObj.getString("direccion_domicilio")
        val email = dataObj.getString("email")
        val celular = dataObj.getString("celular")
        val tipo = dataObj.getString("tipo_usuario_nombre")
        val codigo = dataObj.getString("codigo")
        val imgPerfil = dataObj.getString("imgPerfil")
        val imgLicencia = dataObj.getString("imgLicencia")
        val id = dataObj.getInt("idusuario")

        //GUARDANDO SESSION
        var editor = variablesSession.edit()
        editor.putString("idusuario",id.toString())
        editor.putString("nombres",nombres.toString())
        editor.putString("apellidos",apellidos.toString())
        editor.putString("tipoDoc",tipoDoc.toString())
        editor.putString("numDoc",numDoc.toString())
        editor.putString("direccion",direccion.toString())
        editor.putString("email",email.toString())
        editor.putString("celular",celular.toString())
        editor.putString("tipo",tipo.toString())
        editor.putString("codigo",codigo.toString())
        editor.putString("imgPerfil",imgPerfil.toString())
        editor.putString("imgLicencia",imgLicencia.toString())
        editor.commit()
    }



}