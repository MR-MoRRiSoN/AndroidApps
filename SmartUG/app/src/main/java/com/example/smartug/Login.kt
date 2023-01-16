package com.example.smartug

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.smartug.session.LoginPref
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var session: LoginPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)
        logIn()
    }

    private fun logIn() {
        edtEmail = findViewById(R.id.username)
        edtPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.loginButton)
        session = LoginPref(this)
        edtEmail = findViewById(R.id.username)
        edtPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.loginButton)
        mAuth = FirebaseAuth.getInstance()
        if (session.islogedIn()) {
            val i = Intent(applicationContext, ListDevice::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()
        }

        btnLogin.setOnClickListener {
            if (haveNetwork()){
            mAuth.signInWithEmailAndPassword(edtEmail.text.toString(), edtPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //    var sayMyID = FirebaseAuth.getInstance().currentUser?.uid
                        session.createLoginSession(
                            edtEmail.text.toString(), edtPassword.text.toString()
                        )
                        val i = Intent(applicationContext, ListDevice::class.java)
                        startActivity(i)
                        finish()

                    } else {

                    }

                }
            }else{
                edtEmail.text.clear()
                edtPassword.text.clear()
                edtEmail.hint = "No Network Connection"
                edtPassword.hint ="No Network Connection"
                btnLogin.text="Try Again"
                btnLogin.setOnClickListener{
                    if (haveNetwork()){
                        val mainLayout = findViewById<View>(R.id.loginLayout) as ConstraintLayout
                        val snackbar = Snackbar.make(mainLayout, "Network Connected", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                        val i = Intent(applicationContext, Login::class.java)
                        startActivity(i)
                    }
                }
            }
        }
    }

    private fun haveNetwork(): Boolean {
        var haveWifi = false
        var haveMobile = false
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.allNetworkInfo
        for (info in networkInfo) {
            if (info.typeName.equals("WIFI", ignoreCase = true)) if (info.isConnected) haveWifi =
                true
            if (info.typeName.equals(
                    "MOBILE",
                    ignoreCase = true
                )
            ) if (info.isConnected) haveMobile = true
        }
        return haveMobile || haveWifi
    }
}