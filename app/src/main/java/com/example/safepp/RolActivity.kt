package com.example.safepp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.activity_rol.*

class RolActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rol)

        setup()

    }


    private fun setup() {
        title = "roles"

        rolCelador.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))
            //this@RolActivity.finish()
        }

        rolSupervisor.setOnClickListener {

            startActivity(Intent(this,LoginSupervisor::class.java))
        }

    }

    override fun  onBackPressed() {

        finishAffinity()

        super.onBackPressed()
    }

}