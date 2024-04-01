package com.programadoreshuacho.busescolar

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.programadoreshuacho.busescolar.Variables.VariablesGlobales
import org.json.JSONArray
import org.json.JSONObject
import java.sql.DriverManager.println
import java.util.*

class MainActivity : AppCompatActivity() {
    private var valJson: Boolean= false
    private var URrlGlobal: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        var varGlobal =applicationContext as VariablesGlobales
        URrlGlobal = varGlobal.urlApi
        var variablesSession = getSharedPreferences("sessionData",Context.MODE_PRIVATE)
        var tokenSession = variablesSession.getString("token","")
        //Thread.sleep(5000)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        //Log.e("SESSION", tokenSession.toString())
        //if(tokenSession.toString() == null || tokenSession.toString().length ==0){
            setContentView(R.layout.activity_main)
        //}else{

            //startActivity(Intent(this,Panel::class.java))
          //  finish()
        //}

        //setContentView(R.layout.activity_registro)
        //ocultamos el nabvar para esta vista
        supportActionBar?.hide()
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener {
            startActivity(Intent(this,Registro::class.java))
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            //val url ="https://kotlin.locurapaper.com/api/login"
            val url =URrlGlobal+"login"
            //val url ="http://localhost/apiBusEscolar/api/login"

            val txtEmail = findViewById<EditText>(R.id.txtEmail).text.toString()
            val txtpassword = findViewById<EditText>(R.id.txtPassword).text.toString()
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Validando sus datos...")
            progressDialog.show();

            val queue= Volley.newRequestQueue(this)
            var resultadoPost = object : StringRequest(Request.Method.POST, url,
                Response.Listener<String> { response ->
                    Log.d("RESPONSE", response.toString())
                    try {
                        var resp: Boolean;
                        val msg:String
                        val jsonResponse = JSONObject(response)

                        resp = jsonResponse.getBoolean("response")

                        Log.e("RESPONSE RESP:::::::", resp.toString());
                        if (resp) {
                            //https://www.youtube.com/watch?v=DEyhwkvT3Gc
                            val dataObj = jsonResponse.getJSONObject("usuario")
                            val token = jsonResponse.getString("access_token")
                            val nombres = dataObj.getString("nombres")
                            val apellidos = dataObj.getString("apellidos")
                            val tipoDoc = dataObj.getString("tipo_documento")
                            val numDoc = dataObj.getString("num_documento")
                            val direccion = dataObj.getString("direccion_domicilio")
                            val email = dataObj.getString("email")
                            val celular = dataObj.getString("celular")
                            val tipo = dataObj.getString("tipo_usuario")
                            val id = dataObj.getInt("idusuario")
                            val codigo = dataObj.getString("codigo")
                            //GUARDANDO SESSION

                            var editor = variablesSession.edit()
                            editor.putString("idusuario",id.toString())
                            editor.putString("token",token.toString())
                            editor.putString("nombres",nombres.toString())
                            editor.putString("apellidos",apellidos.toString())
                            editor.putString("tipoDoc",tipoDoc.toString())
                            editor.putString("numDoc",numDoc.toString())
                            editor.putString("direccion",direccion.toString())
                            editor.putString("email",email.toString())
                            editor.putString("celular",celular.toString())
                            editor.putString("tipo",tipo.toString())
                            editor.putString("codigo",codigo.toString())
                            editor.commit()
                            startActivity(Intent(this,Panel::class.java))
                            progressDialog.dismiss();
                        } else {
                            val message = jsonResponse.getString("message")

                            validarJson(message.toString())
                            //Log.e("RESPONSE valJson:::::::", valJson.toString());
                            if(valJson){
                                val messageObject = jsonResponse.getJSONObject("message")
                                //Log.e("RESPONSE-->", JSONObject(messageObject));
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
                                //Log.e("RESPONSE MSG:::::::", message.toString());
                                progressDialog.dismiss();
                                modalAlerta(message.toString())
                            }
                        }

                    } catch (e: Exception) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        Log.e("RESPONSE", "Error al analizar la respuesta JSON: " + e.message);
                        modalError("Error al analizar la respuesta JSON: " + e.message)
                    }

                }, Response.ErrorListener { error ->
                    Log.d("ERROR EXCEPTION", error.toString())
                    progressDialog.dismiss();
                    modalError(error.toString())
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()

                    parametros.put("email", txtEmail)
                    parametros.put("password", txtpassword)
                    return parametros
                }
            }
            queue.add(resultadoPost)
        }
    }



    fun modalAlerta(message: String): Unit{
        val layoutInflater = LayoutInflater.from(this)

        //Load Layout custom_alertdialog.xml

        //Load Layout custom_alertdialog.xml
        val promptView: View = layoutInflater.inflate(R.layout.modal_alerta, null)
        val alertDialog = AlertDialog.Builder(this).create()

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
        val layoutInflater = LayoutInflater.from(this)

        //Load Layout custom_alertdialog.xml

        //Load Layout custom_alertdialog.xml
        val promptView: View = layoutInflater.inflate(R.layout.modal_error, null)
        val alertDialog = AlertDialog.Builder(this).create()

        val btnCancelar = promptView.findViewById<View>(R.id.btn_error) as Button
        val txtError = promptView.findViewById<TextView>(R.id.txt_error)
        txtError.setText(message);
        //texAlignment(txtError,Layout.Alignment.ALIGN_NORMAL,message);
        //appendTextAlignment(tv,Layout.Alignment.ALIGN_CENTER,"Centrado\n");
        //texAlignment(txtError,Layout.Alignment.ALIGN_OPPOSITE,"Derecha\n");
        //val btnPositive = promptView.findViewById<View>(R.id.AlertDialog_Positivo) as Button

        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }

        //btnPositive.setOnClickListener { Log.d("AlertDialog", "btnPositive") }

        alertDialog.setView(promptView)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun modalSuccess(message: String): Unit{
        val layoutInflater = LayoutInflater.from(this)

        //Load Layout custom_alertdialog.xml

        //Load Layout custom_alertdialog.xml
        val promptView: View = layoutInflater.inflate(R.layout.modal_exito, null)
        val alertDialog = AlertDialog.Builder(this).create()

        val btnCerrar = promptView.findViewById<View>(R.id.btn_success) as Button
        val txtError = promptView.findViewById<TextView>(R.id.txt_success)
        //txtError.setText(Html.fromHtml(message));
        txtError.setText(message);

        btnCerrar.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

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
}


