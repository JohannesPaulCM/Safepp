package com.example.safepp


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Environment.*
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import kotlinx.android.synthetic.main.activity_informactivity.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.delete_dialog.view.*
import kotlinx.android.synthetic.main.export_dialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileWriter
import java.security.AccessController.getContext


class informactivity : AppCompatActivity() {


    /////Variables share//////
    private var Inform: File? = null
    private val authorities = "com.example.safepp.fileprovider"
    private val STORAGE_REQUEST_CODE_IMPORT=3

 /////////////////////////////////////////////variables export////////////////////////////////////////////////////
    lateinit var database: AppDatabase
    private lateinit var rondin: Rondin
    private lateinit var rondinLiveData: LiveData<Rondin>
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////permission ExportCSV/////////////////////////////////////////////////
    private val STORAGE_REQUEST_CODE_EXPORT=1
    private lateinit var storagePermission:Array<String>
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informactivity)


        ///////////////////////////////////////////Init array permission///////////////////////////////////////
        storagePermission= arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //////////////////////////////////////////////////////////////////////////////////////////////////////

        database= AppDatabase.getDatabase(this)
        val idRondin= intent.getIntExtra("id", 0)

        rondinLiveData= database.rondines().get(idRondin)


        var listaRondines= emptyList<Rondin>()
        val database = AppDatabase.getDatabase(this)


        database.rondines().getAll().observe(this, Observer {
            listaRondines = it


            val adapter = rondinAdapter(this, listaRondines)

            lista.adapter = adapter


        })

        lista.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, RodinActivity::class.java)
            intent.putExtra("id", listaRondines[position].idRondin)
            startActivity(intent)


        }

         floatingActionButton4.setOnClickListener{

             //val intent= Intent(this,homeActivity::class.java)
             //startActivity(intent)

             this@informactivity.finish()

             /////////////////////deleteALL/////////////////////////
             //rondinLiveData.removeObservers(this)
             //CoroutineScope(Dispatchers.IO).launch {
                //database.rondines().deleteAll()

             //}
             ///////////////////////////////////////////////////////


         }

        //////////////////////////////////////////////ExportCSV/////////////////////////////////////////////////////
        floatingActionButton10.setOnClickListener {

            val mDialogView = LayoutInflater.from(this).inflate(R.layout.export_dialog, null)
            val mBuilder= AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Export Form")
            val mAlertDialog= mBuilder.show()


            mDialogView.buttonExport.setOnClickListener(){

                val nameCSV = mDialogView.editTextTextName.text.toString()
                textViewNombre.setText("" + nameCSV)
                if (checkStoragePermission()){

                    ExportCSV()

                }
                else {

                    resquestStoragePermissionExport()
                }
                mAlertDialog.dismiss()

            }

            mDialogView.buttonCancelar.setOnClickListener(){

                mAlertDialog.dismiss()
            }



        }


        floatingActionButton_share.setOnClickListener {


            //shareOneAudio()
            if (checkStoragePermission()){

                checkPermissionsAndOpenFilePicker()


            }
            else {

                resquestStoragePermissionImport()
            }



        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        setup()

        /////////////////////////////////////////////////variable name/////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////////////////////////////


    }

    private fun setup() {
        title = "Informe de ronda"

    }

//codigo para el boton de ir hacia atras de la pantalla donde esta la lista de puntos;
    //al oprimir este boton se abre la pantalla del boton scannear "homeActivity"
    override fun onBackPressed() {

       //val intent = Intent(this, homeActivity::class.java)
       //startActivity(intent)
     this@informactivity.finish()
       super.onBackPressed()
   }
    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////ExportCSV////////////////////////////////////////////////////////////////////////////
    private fun ExportCSV(){

        val nombre =textViewNombre.text.toString()
        val exportDir = File(getExternalStorageDirectory(), "/INFORMES DE RONDA")// your path where you want save your file
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }

        val file = File(exportDir, "$nombre.csv")//$TABLE_NAME.csv is like user.csv or any name you want to save
        try {
            file.createNewFile()
            val csvWrite = CSVWriter(FileWriter(file))
            val curCSV = database.query("SELECT * FROM rondines", null)// query for get all data of your database table
            csvWrite.writeNext(curCSV.columnNames)
            while (curCSV.moveToNext()) {
                //Which column you want to export
                val arrStr = arrayOfNulls<String>(curCSV.columnCount)
                for (i in 0 until curCSV.columnCount - 1) {
                    when (i) {
                        20, 22 -> {
                        }
                        else -> arrStr[i] = curCSV.getString(i)
                    }
                }
                csvWrite.writeNext(arrStr)
            }
            csvWrite.close()
            curCSV.close()
            Toast.makeText(this, "Backup exported to $file ", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkStoragePermission ():Boolean{
      return ContextCompat.checkSelfPermission(
          this,
          android.Manifest.permission.WRITE_EXTERNAL_STORAGE
      )==(PackageManager.PERMISSION_GRANTED)
    }

    private fun resquestStoragePermissionExport(){

        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE_EXPORT)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){

            STORAGE_REQUEST_CODE_EXPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ExportCSV()
                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                }

            }
            STORAGE_REQUEST_CODE_IMPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                }

            }
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openFilePicker()
                    Toast.makeText(this, "Permision GRANTED...", Toast.LENGTH_SHORT).show()

                } else {
                    showError()
                }

            }

        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                                               //menu deleteALL//

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_all, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll -> {

                val mDialogView = LayoutInflater.from(this).inflate(R.layout.delete_dialog, null)
                val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
                    .setTitle("Delete All Form")
                val mAlertDialog = mBuilder.show()


                mDialogView.buttonDeleteAceptar.setOnClickListener() {


                    rondinLiveData.removeObservers(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        database.rondines().deleteAll()
                        //deleteDatabase("appDatabase")
                        //database.clearAllTables()


                    }



                    mAlertDialog.dismiss()

                }

                mDialogView.buttonDeleteCancelar.setOnClickListener() {

                    mAlertDialog.dismiss()
                }


            }

        }

        return super.onOptionsItemSelected(item)




    }

    ///////share funtion/////////
    private fun shareOneCSV() {
        val path = getUriForFile(this, authorities, Inform!!)

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, path)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.type = "text/csv"
        startActivity(Intent.createChooser(shareIntent, "Share..."))

    }

    private fun saveCSVToPhone() {
        val sharename= textViewshare.text.toString()
        val downloadPath = File(getExternalStorageDirectory(), "/INFORMES DE RONDA")
        Inform = File(downloadPath, "$sharename")

    }



    ////Filepicker////

    private fun checkPermissionsAndOpenFilePicker() {
        val permissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            openFilePicker()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showError()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }
    private fun showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show()
    }
    private fun openFilePicker() {
        val externalStorage = Environment.getExternalStorageDirectory()

        MaterialFilePicker()
            // Pass a source of context. Can be:
            //    .withActivity(Activity activity)
            //    .withFragment(Fragment fragment)
            //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
            .withActivity(this)
            // With cross icon on the right side of toolbar for closing picker straight away
            .withRequestCode(FILE_PICKER_REQUEST_CODE)
            .withHiddenFiles(true)
            .start()
    }
    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 0
        private const val FILE_PICKER_REQUEST_CODE = 1

        private const val ALARMS_EXTERNAL_STORAGE_FOLDER = "Alarms"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data ?: throw IllegalArgumentException("data must not be null")

            val path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)

            if (path != null) {
                Log.d("Path: ", path)

                val f1 = File("$path")
                val NameSimple =f1.name

                // Create a file object

                // Create a file object
                val f = File("$path")

                // Get the Name of the given file f

                // Get the Name of the given file f
                val Name = f.nameWithoutExtension

                Toast.makeText(this, "Informe de: $Name Seleccionado ", Toast.LENGTH_LONG).show()

               textViewshare.text=NameSimple

                saveCSVToPhone()
                shareOneCSV()
            }
        }
    }

    private fun resquestStoragePermissionImport(){

        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE_IMPORT)
    }




}