package com.programadoreshuacho.busescolar

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.programadoreshuacho.busescolar.Adapter.AdapterTab
import com.programadoreshuacho.busescolar.Variables.VariablesGlobales
import org.json.JSONObject
import java.util.*


class RegistrarpadreFragment : Fragment() {
    private var idConductor:String? =""
    private var idMovilidad:String? =""
    private var URrlGlobal: String=""
    private var codigo: String?=""
    private var cbotipodoc:String? =null
    private var idPadre: String?=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var varGlobal =requireContext().applicationContext as VariablesGlobales
        URrlGlobal = varGlobal.urlApi
        var variablesSession = requireActivity().getSharedPreferences("sessionData", Context.MODE_PRIVATE)
        idConductor = variablesSession.getString("idusuario","");
        codigo = variablesSession.getString("codigo","");
        //Log.i("codigo",codigo.toString())
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_registrarpadre, container, false)
        val view = inflater.inflate(R.layout.fragment_registrarpadre, container, false)

        val spinner = view.findViewById<Spinner>(R.id.cbo_tipoDocPadre)
        val lista = resources.getStringArray(R.array.opciones)
        val adaptador = ArrayAdapter(requireContext(), R.layout.spinner_lista_style, lista)
        val txtCodigoPadre = view.findViewById<EditText>(R.id.txtCodigoPadre)
        txtCodigoPadre.setText(codigo)
        txtCodigoPadre.isEnabled = false
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptador
        spinner.onItemSelectedListener= object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("POSITION: ", lista[p2])
                cbotipodoc = lista[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        val btnGuardarPadre = view.findViewById<Button>(R.id.btnGuardarPadre)
        btnGuardarPadre.setOnClickListener {
            guardarDatosPadre(view)
        }
        //modalSuccess("demos",view)

        /*val pasajerosFragment = PasajerosFragment()
        pasajerosFragment?.tabulador()*/
        return view
    }

    private fun guardarDatosPadre(view:View): Unit  {
        var sessionDataPadre = requireActivity().getSharedPreferences("sessionDataPadre",Context.MODE_PRIVATE)
        var editor = sessionDataPadre.edit()
        editor.clear()
        idPadre=""
        
        val url =URrlGlobal+"parents/register"
        val txtNombrePadre = view.findViewById<EditText>(R.id.txtNombrePadre).text.toString()
        val txtApellidoPadre = view.findViewById<EditText>(R.id.txtApellidoPadre).text.toString()
        val txtCboTipoPadre = cbotipodoc.toString()
        val txtDocumentoPadre = view.findViewById<EditText>(R.id.txtDocumentoPadre).text.toString()
        val txtDireccionPadre = view.findViewById<EditText>(R.id.txtDireccionPadre).text.toString()
        val txtCelularPadre = view.findViewById<EditText>(R.id.txtCelularPadre).text.toString()
        val txtEmailPadre = view.findViewById<EditText>(R.id.txtEmailPadre).text.toString()
        val txtCodigoPadre = view.findViewById<EditText>(R.id.txtCodigoPadre).text.toString()

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
                        idPadre = jsonResponse.getString("id")
                        sessionPadre(view)
                        modalSuccess("${message}",view)
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
                parametros.put("nombrePadre", txtNombrePadre)
                parametros.put("apellidoPadre", txtApellidoPadre)
                parametros.put("tipoDocPadre", txtCboTipoPadre)
                parametros.put("documentoPadre", txtDocumentoPadre)
                parametros.put("direccionPadre", txtDireccionPadre)
                parametros.put("celularPadre", txtCelularPadre)
                parametros.put("emailPadre", txtEmailPadre)
                parametros.put("codigoPadre", txtCodigoPadre)
                Log.i("DATOS ENVIADOS PADRE::", parametros.toString())
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

    fun modalSuccess(message: String,view:View): Unit{
        val layoutInflater = LayoutInflater.from(requireContext())
        val promptView: View = layoutInflater.inflate(R.layout.modal_exito, null)
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val btnCerrar = promptView.findViewById<View>(R.id.btn_success) as Button
        val txtError = promptView.findViewById<TextView>(R.id.txt_success)
        txtError.setText(message);
        btnCerrar.setOnClickListener {
            alertDialog.dismiss()
            //fragmentRegistrarAlumno()
        }

        alertDialog.setView(promptView)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun sessionPadre(view:  View): Unit  {
        val txtNombrePadre = view.findViewById<EditText>(R.id.txtNombrePadre).text.toString()
        val txtApellidoPadre = view.findViewById<EditText>(R.id.txtApellidoPadre).text.toString()
        val txtCboTipoPadre = cbotipodoc.toString()
        val txtDocumentoPadre = view.findViewById<EditText>(R.id.txtDocumentoPadre).text.toString()
        val txtDireccionPadre = view.findViewById<EditText>(R.id.txtDireccionPadre).text.toString()
        val txtCelularPadre = view.findViewById<EditText>(R.id.txtCelularPadre).text.toString()
        val txtEmailPadre = view.findViewById<EditText>(R.id.txtEmailPadre).text.toString()
        val txtCodigoPadre = view.findViewById<EditText>(R.id.txtCodigoPadre).text.toString()

        var sessionDataPadre = requireActivity().getSharedPreferences("sessionDataPadre",Context.MODE_PRIVATE)
        var editor = sessionDataPadre.edit()
        editor.clear()
        editor.putString("idPadre",idPadre)
        editor.putString("nombrePadre",txtNombrePadre)
        editor.putString("apellidoPadre",txtApellidoPadre)
        editor.putString("tipoDocPadre",txtCboTipoPadre)
        editor.putString("documentoPadre",txtDocumentoPadre)
        editor.putString("direccionPadre",txtDireccionPadre)
        editor.putString("celularPadre",txtCelularPadre)
        editor.putString("emailPadre",txtEmailPadre)
        editor.putString("codigoPadre",txtCodigoPadre)
        editor.commit()
    }

    /*private fun fragmentRegistrarAlumno(): PasajerosFragment? {
        val fragmentManager = requireActivity().supportFragmentManager
        return fragmentManager.findFragmentByTag("pasajeros_fragment") as? PasajerosFragment
    }*/

    fun fragmentRegistrarAlumno_(){
        val fragmentManager = supportFragmentManager

        // Crear una instancia del fragmento PasajerosFragment
        val pasajerosFragment = PasajerosFragment()

        // Iniciar una transacción de fragmento
        /*val transaction = fragmentManager.beginTransaction()

        // Agregar o reemplazar el fragmento en el contenedor con un tag específico
        transaction.replace(R.id.viewMaster, pasajerosFragment, "pasajeros_fragment")

        // Confirmar la transacción
        transaction.commit()*/

        // Obtener una referencia al ViewPager después de que el fragmento se haya agregado
        val viewPager = pasajerosFragment.getViewPager()

        // Realizar cualquier operación que necesites con el ViewPager
        viewPager.setCurrentItem(1, true)
    }

    private fun fragmentRegistrarAlumnox() {
        // Obtener una referencia al FragmentManager
        val fragmentManager = requireActivity().supportFragmentManager

        // Encontrar el fragmento PasajerosFragment en el FragmentManager
        val pasajerosFragment = fragmentManager.findFragmentByTag("pasajeros_fragment") as? PasajerosFragment
        Log.e("dddddddddddd ",pasajerosFragment.toString())
        // Verificar si el fragmento pasajerosFragment no es nulo y es una instancia de PasajerosFragment
        pasajerosFragment?.let {
            // Llamar a la función tabulador() en PasajerosFragment
            it.tabulador()
        }
    }

    fun fragmentRegistrarAlumno(){
        val fragmentAView = requireActivity().findViewById<View>(R.id.viewMaster)
        // Asegúrate de que fragmentAView no sea nulo
        if (fragmentAView is ViewPager) {
            // Acceder al ViewPager dentro de FragmentA
            val viewPager = fragmentAView

            // Encontrar el TabLayout dentro de FragmentA
            val tabLayout = fragmentAView.findViewById<TabLayout>(R.id.tab_registro)

            // Configurar el adaptador del ViewPager
            val fragmentAdapter = AdapterTab(childFragmentManager)
            viewPager.adapter = fragmentAdapter

            // Configurar el TabLayout con el ViewPager
            tabLayout.setupWithViewPager(viewPager)

            // Establecer la página actual del ViewPager
            viewPager.setCurrentItem(1, false)
        } else {
            Log.e("FragmentB", "No se encontró ViewPager con ID viewMaster en FragmentA")
        }


    }

}