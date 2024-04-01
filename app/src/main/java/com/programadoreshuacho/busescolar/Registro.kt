package com.programadoreshuacho.busescolar

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.programadoreshuacho.busescolar.Variables.VariablesGlobales
import org.jetbrains.anko.internals.AnkoInternals.getContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*


class Registro : AppCompatActivity() {
    private var imagenbaseperfil: String? = null
    private var imagenbaselicencia: String? = null
    private var URrlGlobal: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        var varGlobal =applicationContext as VariablesGlobales
        URrlGlobal = varGlobal.urlApi

        //ocultamos el nabvar para esta vista
        supportActionBar?.hide()
        val btnAtras = findViewById<Button>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        //https://www.youtube.com/watch?v=nzQVzIHIzUg&ab_channel=Codingraph
        val spinner = findViewById<Spinner>(R.id.cbo_tipoDoc)
        //val lista = listOf("Dni","CE","Pasaporte")
        val lista = resources.getStringArray(R.array.opciones)

        Log.d("LISTA: ", Arrays.toString(lista))

        //val adaptador = ArrayAdapter(this,android.R.layout.simple_spinner_item,lista)
        //adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val adaptador = ArrayAdapter(this, R.layout.spinner_lista_style, lista)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptador

        spinner.onItemSelectedListener= object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Log.d("PARENT: ", Arrays.toString(p0))
                //Log.d("VIEW: ", lista[p1])
                Log.d("POSITION: ", lista[p2])
                //Log.d("ID: ", lista[p3])
                //Toast.makeText(applicationContext, lista[p2], 9000).show()
                //Toast.makeText(this@Registro, "mensaje TOAST", Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        //TOMADO FOTO

        val btnCamaraPerfil = findViewById<Button>(R.id.btnFotoPerfil)
        btnCamaraPerfil.setOnClickListener {
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        val btnCamaraLicencia =findViewById<Button>(R.id.btnFotoLicencia)
        btnCamaraLicencia.setOnClickListener {
            startForResultLicencia.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        


        //CAPTURANDO DATOS
        val btnGuardarRegistro = findViewById<Button>(R.id.btnGuardarRegistro)
        btnGuardarRegistro.setOnClickListener {
            //val url ="https://192.168.0.10/apiBusEscolar/api/register/driver"
            //val url ="https://kotlin.locurapaper.com/api/register/driver"
            val url =URrlGlobal+"register/driver"

            val txtNombres = findViewById<EditText>(R.id.txtNombres).text.toString()
            val txtApellidos = findViewById<EditText>(R.id.txtApellidos).text.toString()
            val cbo_tipoDoc =  spinner.selectedItem.toString()
            val txtDocumento = findViewById<EditText>(R.id.txtDocumento).text.toString()
            val txtDireccion = findViewById<EditText>(R.id.txtDireccion).text.toString()
            val txtCorreo = findViewById<EditText>(R.id.txtCorreo).text.toString()
            val txtCelular = findViewById<EditText>(R.id.txtCelular).text.toString()
            val txtPassword = findViewById<EditText>(R.id.txtPassword).text.toString()
            val txtimgPerfil = imagenbaseperfil.toString() //findViewById<EditText>(R.id.txtImgbase64Perfil).text.toString()
            val txtimgLicencia = imagenbaselicencia.toString()  //findViewById<EditText>(R.id.txtImgbase64Licencia).text.toString()

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("cargando")
            progressDialog.show();

            val queue=Volley.newRequestQueue(this)
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
                                val codigo = jsonResponse.getString("codigo")
                                //Toast.makeText(this, "${message}\n CODIGO:${codigo}", Toast.LENGTH_LONG).show()
                                modalSuccess("${message}\n CODIGO:${codigo}")
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
                                //Toast.makeText(this, errorMessages.toString(), Toast.LENGTH_LONG).show()
                                progressDialog.dismiss();
                                modalAlerta(errorMessages.toString())

                            }

                        } catch (e: Exception) {
                            e.printStackTrace();
                            Log.e("RESPONSE", "Error al analizar la respuesta JSON: " + e.message);
                            //Toast.makeText(getApplicationContext(), "Error al analizar la respuesta JSON: " + e.message, Toast.LENGTH_LONG).show();
                            modalError("Error al analizar la respuesta JSON: " + e.message)
                        }
                        progressDialog.dismiss();
                    }, Response.ErrorListener { error ->
                        Log.d("ERROR EXCEPTION", error.toString())
                        //Toast.makeText(this, "ERROR ${error.toString()}", Toast.LENGTH_LONG).show()
                        progressDialog.dismiss();
                        modalError(error.toString())
                    }) {
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()

                            parametros.put("nombres", txtNombres)
                            parametros.put("apellidos", txtApellidos)
                            parametros.put("numdoc", txtDocumento)
                            parametros.put("tipodoc", cbo_tipoDoc)
                            parametros.put("direccion", txtDireccion)
                            parametros.put("correo", txtCorreo)
                            parametros.put("celular", txtCelular)
                            parametros.put("password", txtPassword)
                            parametros.put("imgperfil", txtimgPerfil)
                            parametros.put("imglicencia", txtimgLicencia)
                            //Log.d("DATOS ENVIADOS", parametros.toString())
                            return parametros
                        }

                    }

            queue.add(resultadoPost)






            //val datos = HashMap<String,String>()
            /*val txtNombres = findViewById<EditText>(R.id.txtNombres).text.toString()
            val txtApellidos = findViewById<EditText>(R.id.txtApellidos).text.toString()
            val cbo_tipoDoc =  spinner.selectedItem.toString()
            val txtDocumento = findViewById<EditText>(R.id.txtDocumento).text.toString()
            val txtDireccion = findViewById<EditText>(R.id.txtDireccion).text.toString()
            val txtCorreo = findViewById<EditText>(R.id.txtCorreo).text.toString()
            val txtCelular = findViewById<EditText>(R.id.txtCelular).text.toString()
            val txtPassword = findViewById<EditText>(R.id.txtPassword).text.toString()
            val txtimgPerfil = findViewById<EditText>(R.id.txtImgbase64Perfil).text.toString()
            val txtimgLicencia = findViewById<EditText>(R.id.txtImgbase64Licencia).text.toString()
            Log.d("txtNombres", txtNombres)
            Log.d("txtimgPerfil", txtimgPerfil)
            */

            /*
            datos["txtNombres"] = findViewById<EditText>(R.id.txtNombres).text.toString()
            datos["txtApellidos"] = findViewById<EditText>(R.id.txtApellidos).text.toString()
            datos["cbo_tipoDoc"] =  spinner.selectedItem.toString()
            datos["txtDocumento"] = findViewById<EditText>(R.id.txtDocumento).text.toString()
            datos["txtDireccion"] = findViewById<EditText>(R.id.txtDireccion).text.toString()
            datos["txtCorreo"] = findViewById<EditText>(R.id.txtCorreo).text.toString()
            datos["txtCelular"] = findViewById<EditText>(R.id.txtCelular).text.toString()
            datos["txtPassword"] = findViewById<EditText>(R.id.txtPassword).text.toString()
            datos["txtimgPerfil"] = findViewById<EditText>(R.id.txtImgbase64Perfil).text.toString()
            datos["txtimgLicencia"] = findViewById<EditText>(R.id.txtImgbase64Licencia).text.toString()

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Cargando...") // Establece el mensaje que se mostrará en el ProgressDialog
            progressDialog.setCancelable(false) // Establece si el usuario puede cancelar el ProgressDialog
            progressDialog.show() // Muestra el ProgressDialog

            val datos_enviar = JSONObject(datos)
            val solicitud = JsonObjectRequest(Request.Method.POST,url,datos_enviar,
            {response ->
                try {
                    val error_serv = response.getInt("response")
                    if(error_serv == 0){
                        Toast.makeText(this@Registro,"ERROR .${response.getString("message")}",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this@Registro,"EXITO .${response.getString("message")}",Toast.LENGTH_LONG).show()
                    }
                }catch (e:Exception){
                    Toast.makeText(this@Registro,"ERROR .${e}",Toast.LENGTH_LONG).show()
                }
            },{
                Toast.makeText(this@Registro,"ERROR .${e}",Toast.LENGTH_LONG).show()
            })

            VolleySingleton.getIntance(this).addToRequestQueue(solicitud)

        */



        }

    }



    //Evento que procesa el resultado de la cámara y envía la vista previa de la foto al ImageView
    //https://www.youtube.com/watch?v=6h5BfTrcSqA
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
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
            val perfil = findViewById<EditText>(R.id.txtImgbase64Perfil)
            perfil.setText(base64Image)

            val imageView = findViewById<ImageView>(R.id.imgPerfil)
            imageView.setImageBitmap(imageBitmap)
        }

    }

    private val startForResultLicencia = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
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
            imagenbaselicencia=base64Image
            val licencia = findViewById<EditText>(R.id.txtImgbase64Licencia)
            licencia.setText(base64Image)

            val imageView = findViewById<ImageView>(R.id.imgLicencia)
            imageView.setImageBitmap(imageBitmap)



        }

    }

    fun modalSuccess_(message: String): Unit {
        val modal = AlertDialog.Builder(this)
        modal.apply{
            setIcon(R.drawable.ico_check)
            setTitle("Datos Correctos !!")
            setMessage(message)
            setCancelable(false)
            setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this@Registro, MainActivity::class.java)
                startActivity(intent)
            }.apply {
                getContext().theme.applyStyle(R.style.AlertDialogButtonSuccess, true)
            }
        }.create().show()
    }

    fun modalAlerta2(message: String): Unit {
        val modal = AlertDialog.Builder(this)
        modal.apply{
            setIcon(R.drawable.ico_warning)
            setTitle("Alerta !!")
            setMessage(message)
            setCancelable(false)
            setPositiveButton("Cerrar") { dialog, _ ->
                    dialog.dismiss()
                }
            /*val alertDialog = create()
            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this@Registro, R.color.color_warning))
            }
            alertDialog.show()*/
        }.apply {
            getContext().theme.applyStyle(R.style.AlertDialogButtonWarning, true)
        }.create().show()
    }

    fun modalError_(message: String): Unit {
        val modal = AlertDialog.Builder(this)
        modal.apply{
            setIcon(R.drawable.ico_error)
            setTitle("Error !!")
            setMessage(message)
            setCancelable(false)
            setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }.apply {
                getContext().theme.applyStyle(R.style.AlertDialogButtonError, true)
            }
        }.create().show()
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
            val intent = Intent(this@Registro, MainActivity::class.java)
            startActivity(intent)
        }

        alertDialog.setView(promptView)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }








}


