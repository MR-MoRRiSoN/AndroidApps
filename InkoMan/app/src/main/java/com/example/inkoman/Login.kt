package com.example.inkoman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception


class Login : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        supportActionBar?.hide()


        setContentView(R.layout.activity_login)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_enterPass)
        btnLogin = findViewById(R.id.edt_logIn)
        btnSignUp = findViewById(R.id.edt_signUp)

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            login(email, password)


        }


    }

    private fun login(email: String, password: String) {
        //login
        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@Login, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Login, "Check your information", Toast.LENGTH_SHORT).show()
                }

            }

        }catch (e:Exception){
            Toast.makeText(this, "Enter USer OR Password", Toast.LENGTH_SHORT).show()
        }


    }
}