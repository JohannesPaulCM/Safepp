package com.example.safepp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LoginSupervisor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_supervisor)


        setup()

    }

    private fun setup(){

        title="Autenticación Supervisor"
    }

    override fun onBackPressed(){

        this@LoginSupervisor.finish()
        super.onBackPressed()

    }


}