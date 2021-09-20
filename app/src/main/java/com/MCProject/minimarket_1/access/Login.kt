package com.MCProject.minimarket_1.access

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.MCProject.minimarket_1.*

class Login: AppCompatActivity() {


    private lateinit var auth : FirebaseAuth
    private lateinit var mailED: EditText
    private lateinit var passwordED: EditText
    private lateinit var login: Button
    private lateinit var joinUs: Button
    private lateinit var dialog: AlertDialog
    val load = Loading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //init autentication
        auth = FirebaseAuth.getInstance()

        //get variable
        mailED = findViewById(R.id.mail)
        passwordED = findViewById(R.id.pw)
        login = findViewById(R.id.login)
        joinUs = findViewById(R.id.joinUs)
    }

    override fun onStart() {
        super.onStart()

        /**
         *  verifica se l'utente è già loggato
         */
        val currentUser = auth.currentUser
        updateUI(currentUser)
        //new acount
        joinUs.setOnClickListener {
            val intent = Intent(this, JoinUs::class.java)
            startActivity(intent)
        }
    }


    private fun updateUI(currentUser: FirebaseUser?) {
        //Funzione che aggiorna la UI per un utente che si è già registrato


        if (currentUser == null) {
            //Utente non loggato
            //LOGIN:
            login.setOnClickListener {
                load.startLoading()
                login(mailED, passwordED)
            }

        } else {
            //Utente autenticato
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }

    private fun login(email: EditText, password: EditText) {
        if(email.text.isNotEmpty() && password.text.isNotEmpty()) {
            //Verifica se l'email è stata verificata
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        load.stopLoadingDialog()
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
                .addOnCanceledListener {
                    load.stopLoadingDialog()
                    Toast.makeText(
                        this, "Something Get Wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            load.stopLoadingDialog()
            Toast.makeText(
                this, "Authentication failed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}