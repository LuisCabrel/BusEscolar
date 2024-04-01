package com.programadoreshuacho.busescolar

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.programadoreshuacho.busescolar.Variables.VariablesGlobales
import org.json.JSONArray
import org.json.JSONObject
import java.sql.DriverManager


class BusFragment : Fragment() {
    private lateinit var drawer: DrawerLayout
    private lateinit var dataSession: SharedPreferences
    private var URrlGlobal: String=""
    private var valJson: Boolean= false


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        var varGlobal =requireContext().applicationContext as VariablesGlobales
        URrlGlobal = varGlobal.urlApi
        val view = inflater.inflate(R.layout.fragment_bus, container, false)
        //btnEditarDetalle
        val btnDetalle = view.findViewById<Button>(R.id.btnEditarDetalle)
        //return inflater.inflate(R.layout.fragment_bus, container, false)
        Log.e("FRAGMENT", "BUSFRAGMENT".toString())
        listarDatosBus(view)

        btnDetalle.setOnClickListener {
            fragmentEditar(view)
        }

        return view
    }

    fun fragmentEditar(view:  View): Unit {
        val fragmento2 = MovilidadFragment()
        // Iniciar una transacción de fragmento
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        // Reemplazar el contenido actual del contenedor con Fragmento2
        transaction.replace(R.id.frameContainer, fragmento2)
        // Agregar la transacción a la pila de retroceso (opcional)
        transaction.addToBackStack(null)
        // Confirmar la transacción
        transaction.commit()
    }

    fun listarDatosBus(view:  View): Unit {
        val txtDetallePlaca = view.findViewById<TextView>(R.id.txtDetallePlaca)
        val txtDetalleColor = view.findViewById<TextView>(R.id.txtDetalleColor)
        val txtDetalleAsientos = view.findViewById<TextView>(R.id.txtDetalleAsientos)
        val txtDetalleMarca = view.findViewById<TextView>(R.id.txtDetalleMarca)
        val txtDetalleModelo = view.findViewById<TextView>(R.id.txtDetalleModelo)
        val txtDetalleDireccion = view.findViewById<TextView>(R.id.txtDetalleDireccion)
        val imgDetalleLado = view.findViewById<ImageView>(R.id.imgDetalleLado)
        val imgDetalleFrente = view.findViewById<ImageView>(R.id.imgDetalleFrente)


        dataSession = requireActivity().getSharedPreferences("sessionData", Context.MODE_PRIVATE)
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando datos...")
        progressDialog.show();

        val url =URrlGlobal+"bus/"+dataSession.getString("idusuario","")
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
                            txtDetallePlaca.setText(dataObj.getString("placa").toString());
                            txtDetalleColor.setText(dataObj.getString("color"));
                            txtDetalleAsientos.setText(dataObj.getString("asiento"));
                            txtDetalleMarca.setText(dataObj.getString("marca").toString());
                            txtDetalleModelo.setText(dataObj.getString("modelo").toString());
                            txtDetalleDireccion.setText(dataObj.getString("direccion").toString());
                            Glide.with(this).load(dataObj.getString("foto_lado").toString()).into(imgDetalleLado)
                            Glide.with(this).load(dataObj.getString("foto_frontal").toString()).into(imgDetalleFrente)

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
                        modalError("Error al analizar la respuesta JSON - DETALLE BUS : " + e.message)

                    }
                },
                Response.ErrorListener { error ->
                    // Manejar errores de la solicitud aquí
                    DriverManager.println("Error: ${error.message}")
                }) {
            // Aquí puedes anular métodos si necesitas enviar parámetros adicionales, encabezados, etc.
            /*override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }*/
        }
        queue.add(resultadoGet)

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
        var variablesSession = requireActivity().getSharedPreferences("sessionDataBus",Context.MODE_PRIVATE)

        val dataObj = jsonResponse.getJSONObject("datos")

        val placa = dataObj.getString("placa")
        val color = dataObj.getString("color")
        val asiento = dataObj.getString("asiento")
        val marca = dataObj.getString("marca")
        val modelo = dataObj.getString("modelo")
        val direccion = dataObj.getString("direccion")
        val foto_lado = dataObj.getString("foto_lado")
        val foto_frontal = dataObj.getString("foto_frontal")
        val idmovilidad = dataObj.getInt("idmovilidad")

        //GUARDANDO SESSION
        var editor = variablesSession.edit()
        editor.putString("idmovilidad", idmovilidad.toString())
        editor.putString("placa",placa.toString())
        editor.putString("color",color.toString())
        editor.putString("asiento",asiento.toString())
        editor.putString("marca",marca.toString())
        editor.putString("modelo",modelo.toString())
        editor.putString("direccion",direccion.toString())
        editor.putString("foto_lado",foto_lado.toString())
        editor.putString("foto_frontal",foto_frontal.toString())
        editor.commit()
    }




}