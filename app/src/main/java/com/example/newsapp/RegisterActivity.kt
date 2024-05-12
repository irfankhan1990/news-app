package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.resources.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()

    // Create a new user with a first and last name
    val cities = db.collection("users")
    private lateinit var loginbutton: TextView
    private lateinit var btnsignup: TextView
    private lateinit var emailsignup: EditText
    private lateinit var passwordsignup: EditText
    private lateinit var confirmpassword: EditText
    private lateinit var namesignup: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        loginbutton = findViewById<TextView>(R.id.loginbutton)
        btnsignup = findViewById<TextView>(R.id.btnsignup)
        emailsignup = findViewById<EditText>(R.id.emailsignup)
        passwordsignup = findViewById<EditText>(R.id.passwordsignup)
        confirmpassword = findViewById<EditText>(R.id.confirmpassword)
        namesignup = findViewById<EditText>(R.id.namesignup)

        loginbutton.setOnClickListener {
            login()
        }

        btnsignup.setOnClickListener {
            signup()
        }
    }

    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun signup() {
        val email = emailsignup.text.toString()
        val password = passwordsignup.text.toString()
        val passwordc = confirmpassword.text.toString()
        val name = namesignup.text.toString();
        var language = ""
//            regBotton.isEnabled = false
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show()
            return@signup
        }

        if (password != passwordc) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
            return@signup
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(
                        this@RegisterActivity,
                        "You are Registered",
                        Toast.LENGTH_SHORT
                    ).show()


                    val data1 =
                        user(Firebase.auth.currentUser!!.uid, name, email, language, password)

                    data1.uid?.let { it1 -> cities.document(it1).set(data1) }
                    db.collection("users").document(Firebase.auth.currentUser!!.uid)
                        .update("language", "English")

                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}