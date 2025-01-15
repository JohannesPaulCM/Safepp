package com.example.safepp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setup()

    }

    private fun setup (){
        title="Register"
        floatingActionButton_crearRegister.setOnClickListener {

            if (editTextEmailRegister.text.isNotEmpty() && editTextPasswordRegister.text!!.isNotEmpty())
            {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(editTextEmailRegister.text.toString(),editTextPasswordRegister.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Registro Exitoso")
                        builder.setMessage("¡Se ha creado correctamente el usuario!")
                        builder.setPositiveButton("Aceptar",null)
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                        editTextEmailRegister.setText("")
                        editTextPasswordRegister.setText("")
                    }else {

                            FirebaseAuth.getInstance().signInWithEmailAndPassword(editTextEmailRegister.text.toString(), editTextPasswordRegister.text.toString()).addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(this, "EL USUARIO YA EXISTE", Toast.LENGTH_LONG).show()
                                }else {
                                    Toast.makeText(this, "ERROR DE REGISTRO; EL CORREO YA EXISTE O NO ES UNA ESTRUCTURA VALIDA", Toast.LENGTH_LONG).show()
                                }
                            }


                    }
                }

            }
        }

        button_Login_Register.setOnClickListener {

            this@RegisterActivity.finish()
            startActivity(Intent(this, MainActivity::class.java))

        }

    }

    override fun  onBackPressed() {

        this@RegisterActivity.finish()
        startActivity(Intent(this, MainActivity::class.java))

        super.onBackPressed()
    }



}