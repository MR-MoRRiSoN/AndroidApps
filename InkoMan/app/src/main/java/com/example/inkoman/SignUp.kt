package com.example.inkoman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class SignUp : AppCompatActivity() {
    private lateinit var edtNik: EditText
    private lateinit var edtEmail: EditText
    private lateinit var accessCode: EditText
    private lateinit var edtPassword: EditText
    private lateinit var repeatPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()



        edtNik = findViewById(R.id.edt_SignUp_Name)
        edtEmail = findViewById(R.id.edt_SignUp_email)
        accessCode = findViewById(R.id.edt_SignUp_AccessCode)
        edtPassword = findViewById(R.id.edt_SignUp_enterPass)
        repeatPassword = findViewById(R.id.edt_SignUp_repPass)
        btnSignUp = findViewById(R.id.edt_SignUp_btnSignUp)

        btnSignUp.setOnClickListener {
            val name = edtNik.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            signUp(email, password, name)
        }

    }

    private fun signUp(email: String, password: String, name: String) {
        //registration
        try {
            if (validation()) {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            addUserToDataBase(name, email, mAuth.currentUser?.uid!!)

                            val intent = Intent(this@SignUp, MainActivity::class.java)
                            finish()
                            startActivity(intent)

                        } else {
                            Toast.makeText(
                                this@SignUp,
                                "Check your Email Address",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }

            }
        } catch (e: Exception) {
            Toast.makeText(this, "Fill All Information", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validation(): Boolean {

        var tester: Boolean
        var pas1: String = edtPassword.text.toString()
        var pas2: String = repeatPassword.text.toString()
        var access = accessCode.text.toString()
        var accessMustBe = "1011"
        if (pas1!=pas2||pas1==""){
            Toast.makeText(this,"Chek Your Password",Toast.LENGTH_SHORT).show()
        }else if (access!=accessMustBe){
            Toast.makeText(this,"Your Access Code Is NOT Acceptable,\n4Pleas Connect The Administration",Toast.LENGTH_SHORT).show()
        }


        tester = pas1 == pas2 && access == accessMustBe
        return tester
    }

    private fun addUserToDataBase(name: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child("user").child(uid).setValue(User(name, email, uid))
    }

}