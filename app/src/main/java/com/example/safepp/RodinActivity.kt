package com.example.safepp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_informactivity.*
import kotlinx.android.synthetic.main.activity_rondin.*
import kotlinx.android.synthetic.main.export_dialog.view.*
import kotlinx.android.synthetic.main.novelty_dialog.*
import kotlinx.android.synthetic.main.novelty_dialog.view.*
import kotlinx.android.synthetic.main.novelty_dialog.view.editTextTextMultiLine2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RodinActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var rondin: Rondin
    private lateinit var rondinLiveData: LiveData<Rondin>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rondin)

        setup()

        database= AppDatabase.getDatabase(this )

        val idRondin= intent.getIntExtra("id",0)

        rondinLiveData= database.rondines().get(idRondin)

      rondinLiveData.observe(this, Observer {


            rondin=it
            textViewZona1.text=rondin.zona
            textViewTime1.text=rondin.time
            textViewLat1.text=rondin.latitud
            textViewLon1.text=rondin.longitud
            imageView3.setImageResource(rondin.imagen)
            textView_Novedad.text=rondin.novedad



        })

        //val rondin = intent.getSerializableExtra("rondin")as Rondin










    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.rondin_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.delete1->{

               rondinLiveData.removeObservers(this)
                CoroutineScope(Dispatchers.IO).launch {
                    database.rondines().delete(rondin)
                    this@RodinActivity.finish()
                }

            }

            R.id.map->{

                var dato1 = textViewLat1.text.toString()
                var dato2 = textViewLon1.text.toString()
                val intent= Intent(this, MapActivity::class.java)
                intent.putExtra("latitud",dato1)
                intent.putExtra("longitud",dato2)
                startActivity(intent)


            }

        }


        return super.onOptionsItemSelected(item)
    }


    private fun setup() {
        //title = "Informe de ronda"


    }
}

