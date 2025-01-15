package com.example.safepp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*
import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.android.synthetic.main.activity_informactivity.*
import kotlinx.android.synthetic.main.activity_rondin.*
import kotlinx.android.synthetic.main.export_dialog.view.*
import kotlinx.android.synthetic.main.membresia_dialog.*
import kotlinx.android.synthetic.main.membresia_dialog.view.*
import kotlinx.android.synthetic.main.novelty_dialog.*
import kotlinx.android.synthetic.main.novelty_dialog.view.*
import kotlinx.android.synthetic.main.pregunta_novedad.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class  providerType{

    BASIC
}




 class homeActivity : AppCompatActivity() {

    //////////variables para GPS
     val PERMISSION_ID = 42
     lateinit var mFusedLocationClient: FusedLocationProviderClient
    /////////////////////////////


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")


        //GURDADO DE DATOS

        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        /////////////////////////////////GPS////////////////////////////////////
        botonscan.setOnClickListener {

            if (isLocationEnabled()&&checkPermissions()) {


                requestNewLocationData()

                initScanner()

                //// fecha y hora actual /////////
                val text: TextView = findViewById<TextView>(R.id.textViewFecha)
                var date = Date()
                val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
                val answer: String = formatter.format(date)
                text.setText("time:" + answer)
                ////////////////////////////////

                //////////////GPS//////////////
                //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                //getLastLocation()




            }

            else {

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                requestPermissions()
                requestDeviceLocationSettings()

            }
        }
        ////////////////////////////////////////////////////////////////////////

        //Remote config
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task->
            if (task.isSuccessful){

                val showButtonScann = Firebase.remoteConfig.getBoolean("show_button_scann")
                val showDialogPause = Firebase.remoteConfig.getBoolean("show_dialog_pause")

                if (showButtonScann){
                    botonscan.visibility=View.VISIBLE
                }
                else{
                    botonscan.visibility=View.INVISIBLE
                    //Logout
                    FirebaseAuth.getInstance().signOut()
                    //onBackPressed()
                    this@homeActivity.finish()
                    startActivity(Intent(this, MainActivity::class.java))

                    //BORRADO DE DATOS

                    val prefs =
                        getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefs.clear()
                    prefs.apply()

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

    ///////////////////////////////////////////para el menu//////////////////////////////////////////////////////////
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ronda, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.informe -> {

                startActivity(Intent(this, informactivity::class.java))
                //this@homeActivity.finish()
            }

            R.id.myLocation -> {

                val uri = Uri.parse("geo:${textViewLa},${textViewLon}")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }



        }

        return super.onOptionsItemSelected(item)

    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setup(email: String, provider: String) {
        title = "Control Ronda"
        logout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            //onBackPressed()
            this@homeActivity.finish()
            startActivity(Intent(this, MainActivity::class.java))

            //BORRADO DE DATOS

            val prefs =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

        }


        ////////////////////////button scand//////////////////////


    }

     ////////////////////////////////////funciones para QR y guardado de la base de datos////////////////////////////////////////
    private fun initScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Enfoque el QR de la zona para el informe de RONDA")
        integrator.setBeepEnabled(true)
        integrator.initiateScan()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "cancelado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "La zona escaneada es:${result.contents}", Toast.LENGTH_SHORT)
                    .show()

                //// RESULTADO QR /////////
                val text: TextView = findViewById<TextView>(R.id.textViewQr)
                text.setText( result.contents)
                //////////////////////////////////

                /////////////////////////////GPS LOCATION/////////////////////////////////////////////////////////////
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                getLastLocation()
                //////////////////////////////////////////////////////////////////////////////////////////////////////


                //////////////////////////////////////AlertDialog pregunta novedad///////////////////////////////////
                val mDialogView1 = LayoutInflater.from(this).inflate(R.layout.pregunta_novedad,null)
                val mBuilder1= AlertDialog.Builder(this )
                    .setView(mDialogView1)
                    .setTitle("Observación de novedades")
                val mAlertDialog1= mBuilder1.show()


                mDialogView1.button_Si_novedad.setOnClickListener(){

                    ///////////////////////////////////////alertDialog novedad////////////////////////////////////////
                    val mDialogView = LayoutInflater.from(this).inflate(R.layout.novelty_dialog,null)
                    val mBuilder= AlertDialog.Builder(this )
                        .setView(mDialogView)
                        .setTitle("Ventana de Novedades")
                    val mAlertDialog= mBuilder.show()


                    mDialogView.button_save_novelty.setOnClickListener(){

                        val novedadtext = mDialogView.editTextTextMultiLine2.text.toString()
                        textView_novedad_home.setText(""+novedadtext)

                        startActivity(Intent(this, informactivity::class.java))

                        val database = AppDatabase.getDatabase(this)

                        val zona = textViewQr.text.toString()
                        val time = textViewFecha.text.toString()
                        val longitud = textViewLon.text.toString()
                        val latitud = textViewLa.text.toString()
                        val novedad = textView_novedad_home.text.toString()


                        val rondin =
                            Rondin( zona, time, longitud, latitud,novedad,R.drawable.ic_baseline_center_focus_strong_24)
                        CoroutineScope(Dispatchers.IO).launch {

                            database.rondines().inserAll(rondin)

                            //this@homeActivity.finish()

                        }


                        mAlertDialog.dismiss()

                    }

                    mDialogView.button_cancel_novelty.setOnClickListener(){


                        startActivity(Intent(this, informactivity::class.java))

                        val database = AppDatabase.getDatabase(this)

                        val zona = textViewQr.text.toString()
                        val time = textViewFecha.text.toString()
                        val longitud = textViewLon.text.toString()
                        val latitud = textViewLa.text.toString()
                        val novedad = textView_novedad_home.text.toString()


                        val rondin =
                            Rondin( zona, time, longitud, latitud,novedad,R.drawable.ic_baseline_center_focus_strong_24)
                        CoroutineScope(Dispatchers.IO).launch {

                            database.rondines().inserAll(rondin)

                            //this@homeActivity.finish()

                        }







                        mAlertDialog.dismiss()
                    }

                    //////////////////////////////////////////////////////////////////////////////////////////////////
                    mAlertDialog1.dismiss()

                }

                mDialogView1.button_No_novedad.setOnClickListener(){

                    startActivity(Intent(this, informactivity::class.java))

                    val database = AppDatabase.getDatabase(this)

                    val zona = textViewQr.text.toString()
                    val time = textViewFecha.text.toString()
                    val longitud = textViewLon.text.toString()
                    val latitud = textViewLa.text.toString()
                    val novedad = textView_novedad_home.text.toString()


                    val rondin =
                        Rondin( zona, time, longitud, latitud,novedad,R.drawable.ic_baseline_center_focus_strong_24)
                    CoroutineScope(Dispatchers.IO).launch {

                        database.rondines().inserAll(rondin)

                        //this@homeActivity.finish()

                    }



                    mAlertDialog1.dismiss()
                }


                /////////////////////////////////////////////////////////////////////////////////////////////////////



            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }


    }

     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////funciones para GPS//////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        //requestNewLocationData()
                        findViewById<TextView>(R.id.textViewLa).text = location.latitude.toString()
                        findViewById<TextView>(R.id.textViewLon).text = location.longitude.toString()
                    }
                }
            } else {

                requestDeviceLocationSettings()

                //Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                //val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                //startActivity(intent)

            }
        } else {
            requestPermissions()
        }
    }

     @SuppressLint("MissingPermission")
     private fun requestNewLocationData() {
         var mLocationRequest = LocationRequest()
         mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
         mLocationRequest.interval = 50
         mLocationRequest.fastestInterval = 25
         mLocationRequest.numUpdates = 20

         mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
         mFusedLocationClient!!.requestLocationUpdates(
             mLocationRequest, mLocationCallback,
             Looper.myLooper()
         )
     }

     private val mLocationCallback = object : LocationCallback() {
         override fun onLocationResult(locationResult: LocationResult) {
             var mLastLocation: Location = locationResult.lastLocation
             findViewById<TextView>(R.id.textViewLa).text = mLastLocation.latitude.toString()
             findViewById<TextView>(R.id.textViewLon).text = mLastLocation.longitude.toString()
         }
     }

     private fun isLocationEnabled(): Boolean {
         var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
         return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
             LocationManager.NETWORK_PROVIDER
         )
     }

     private fun checkPermissions(): Boolean {
         if (ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.ACCESS_COARSE_LOCATION
             ) == PackageManager.PERMISSION_GRANTED &&
             ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.ACCESS_FINE_LOCATION
             ) == PackageManager.PERMISSION_GRANTED
         ) {
             return true
         }
         return false
     }

     private fun requestPermissions() {
         ActivityCompat.requestPermissions(
             this,
             arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
             PERMISSION_ID
         )
     }

     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
         if (requestCode == PERMISSION_ID) {
             if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                 getLastLocation()
             }
         }
     }

     fun requestDeviceLocationSettings() {


         val locationRequest = LocationRequest.create().apply {
             interval = 50
             fastestInterval = 25
             priority = LocationRequest.PRIORITY_HIGH_ACCURACY
         }

         val builder = LocationSettingsRequest.Builder()
             .addLocationRequest(locationRequest)

         val client: SettingsClient = LocationServices.getSettingsClient(this)
         val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())


         task.addOnSuccessListener { locationSettingsResponse ->
             // All location settings are satisfied. The client can initialize
             // location requests here.
             // ...
             val state = locationSettingsResponse.locationSettingsStates
         }

         task.addOnFailureListener { exception ->
             if (exception is ResolvableApiException){
                 // Location settings are not satisfied, but this can be fixed
                 // by showing the user a dialog.
                 try {
                     // Show the dialog by calling startResolutionForResult(),
                     // and check the result in onActivityResult().
                     exception.startResolutionForResult(this@homeActivity,
                         100)
                 } catch (sendEx: IntentSender.SendIntentException) {
                     // Ignore the error.
                 }
             }
         }


     }


     override fun onResume() {
         super.onResume()
         requestNewLocationData()
         textView_novedad_home.setText("")
     }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



     //funcion para el bloqueo del boton de ir hacia atras, esto evita que se cierre la sesion
     //en la APP

     override fun  onBackPressed() {

         finishAffinity()
         super.onBackPressed()
     }
     ///////////////////////////////////////////////////////////////////////////////////////////




}