package com.example.safepp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ActionCodeEmailInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_informactivity.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.export_dialog.view.*
import kotlinx.android.synthetic.main.membresia_dialog.*
import kotlinx.android.synthetic.main.membresia_dialog.view.*
import kotlinx.android.synthetic.main.register_dialog.view.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //setup
        setup()
        session()

        //Remote config

        val configSettings= remoteConfigSettings {
            minimumFetchIntervalInSeconds=30
        }
        val firebaseConfig= Firebase.remoteConfig
        firebaseConfig.setConfigSettingsAsync(configSettings)
        firebaseConfig.setDefaultsAsync(mapOf("show_dialog_pause" to false,
            "show_button_login" to true, "show_button_scann" to true ))

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task->
            if (task.isSuccessful){

                val showButtonLogin = Firebase.remoteConfig.getBoolean("show_button_login")
                val showDialogPause = Firebase.remoteConfig.getBoolean("show_dialog_pause")

                if (showButtonLogin){
                    login.visibility=View.VISIBLE
                }
                else{
                    login.visibility=View.INVISIBLE
                }

                if (showDialogPause){
                    val mDialogView = LayoutInflater.from(this).inflate(R.layout.membresia_dialog,null)
                    val mBuilder= AlertDialog.Builder(this )
                        .setView(mDialogView)
                        .setTitle("Licencia")
                    val mAlertDialog= mBuilder.show()


                    mDialogView.button_Aceptar_Pause.setOnClickListener(){


                        mAlertDialog.dismiss()

                    }


                }

            }


        }


    }

    override fun onStart(){

        super.onStart()

        authLayout.visibility=View.VISIBLE

    }


private fun session(){

    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
    val email= prefs.getString("email",null)
    val provider = prefs.getString("provider",null)

    if (email != null && provider != null){
        authLayout.visibility= View.INVISIBLE
        this@MainActivity.finish()
        showHome(email, providerType.valueOf(provider))
    }


}
    private fun setup() {
        title = "Autenticación"

        
        login.setOnClickListener {

            if (email.text.isNotEmpty() && Password.text!!.isNotEmpty())
            {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(),Password.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        this@MainActivity.finish()
                        showHome(it.result?.user?.email ?:"",providerType.BASIC)
                    }else {
                        showAlert()
                    }
                }

            }

        }

       button_register.setOnClickListener {

           val mDialogView = LayoutInflater.from(this).inflate(R.layout.register_dialog,null)
           val mBuilder= AlertDialog.Builder(this )
               .setView(mDialogView)
               .setTitle("lock")
           val mAlertDialog= mBuilder.show()


           mDialogView.button_lockRegister.setOnClickListener(){

               val key = mDialogView.editText_Key_Register.text.toString()
               val passwordKey= "Safepp6768"
               if (key==passwordKey){
                   this@MainActivity.finish()
                   startActivity(Intent(this, RegisterActivity::class.java))
               }
               else {

                   Toast.makeText(this, "LA CONTRASEÑA NO ES CORRECTA", Toast.LENGTH_LONG).show()
                   //Alert
                   val builder = AlertDialog.Builder(this)
                   builder.setTitle("Error Key")
                   builder.setMessage("Contraseña Incorrecta")
                   builder.setPositiveButton("Aceptar",null)
                   val dialog: AlertDialog = builder.create()
                   dialog.show()

               }
               mAlertDialog.dismiss()

           }

           mDialogView.button_cancelar_lockRegister.setOnClickListener(){

               mAlertDialog.dismiss()
           }


       }



    }

    private fun showAlert(){


        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: providerType) {

        val intent = Intent(this, informactivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }


        val homeIntent = Intent(this, homeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)

        }

////////////////////////////////////////cierra todas las activity de la aplicacion ////////////////////////
    override fun  onBackPressed() {

        this@MainActivity.finish()
        //finishAffinity()

        super.onBackPressed()
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////

        }

