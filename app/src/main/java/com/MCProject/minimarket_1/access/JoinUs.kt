package com.MCProject.minimarket_1.access

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.*
import com.MCProject.minimarket_1.firestore.FirestoreRequest
import com.MCProject.minimarket_1.firestore.FirestoreRequest_User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


class JoinUs : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var mailED: EditText
    lateinit var passwordED: EditText
    lateinit var joinInBTN: Button
    lateinit var backBTN: Button
    lateinit var clientRB: RadioButton
    lateinit var riderRB: RadioButton
    lateinit var gestorRB: RadioButton
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_us)

        auth = FirebaseAuth.getInstance()

        //get variable
        mailED = findViewById(R.id.mailJ)
        passwordED = findViewById(R.id.pwJ)
        joinInBTN = findViewById(R.id.joinIn)
        backBTN = findViewById(R.id.back)
        clientRB = findViewById(R.id.client_rb)
        gestorRB = findViewById(R.id.gestor_rb)
        riderRB = findViewById(R.id.rider_rb)

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed if (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)

        //back to login
        backBTN.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        onCheckboxClicked(this)
    }

    private fun onCheckboxClicked(context: Context) {
        if(clientRB.isChecked){
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.joinUs_frame_layout, ClientJoinUsFragment())
                addToBackStack(null)
                commit()
            }
        }
        clientRB.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.joinUs_frame_layout, ClientJoinUsFragment())
                addToBackStack(null)
                commit()
            }
        }
        riderRB.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.joinUs_frame_layout, RiderJoinUsFragment())
                addToBackStack(null)
                commit()
            }
        }
        gestorRB.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.joinUs_frame_layout, GestorJoinUsFragment())
                addToBackStack(null)
                commit()
            }
        }
    }


    fun updateUI(currentUser: FirebaseUser?) {
        //Funzione che aggiorna la UI per un utente che si è già registrato

        if (currentUser == null) {
            //Utente non loggato
            //LOGIN:
            joinInBTN.setOnClickListener {
                joinUs(auth, mailED, passwordED)
            }

        } else {
            //Utente autenticato
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun joinUs(auth: FirebaseAuth, email: EditText, password: EditText) {
        if(email.text.isNotEmpty() && password.text.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this)
                { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser

                        /**
                         * salvo i dati dell'utente/rider/gestore su firebase
                         */
                        saveDataOnFirestore(user)

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                                this, "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                    this, "Err - Authentication failed.",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveDataOnFirestore(user: FirebaseUser?) {

        var entry: HashMap<String, Any?> = hashMapOf<String, Any?>(
                "nome" to "",
                "cognome" to "",
                "email" to ""
        )
        var collection = "utenti"
        if (clientRB.isChecked){
            val nome = findViewById<EditText>(R.id.frag_name)
            val cognome = findViewById<EditText>(R.id.frag_surname)
            entry.clear()
            entry = hashMapOf<String, Any?>(
                    "nome" to nome.text.toString(),
                    "cognome" to cognome.text.toString(),
                    "email" to mailED.text.toString()
            )
        }

        if (riderRB.isChecked){
            entry.clear()
            collection = "riders"
            val nomeR = findViewById<EditText>(R.id.frag_rider_name)
            val cognomeR = findViewById<EditText>(R.id.frag_rider_surname)
            entry = hashMapOf<String, Any?>(
                    "nome rider" to nomeR.text.toString(),
                    "cognome rider" to cognomeR.text.toString(),
                    "email" to mailED.text.toString(),
                    "status" to "0"
            )
        }

        if (gestorRB.isChecked){
            entry.clear()
            collection = "gestori"
            val nomeG = findViewById<EditText>(R.id.frag_socName)
            val via = findViewById<EditText>(R.id.frag_position_via)
            val city = findViewById<EditText>(R.id.frag_position_city)
            entry = hashMapOf<String, Any?>(
                    "nome azienda" to nomeG.text.toString(),
                    "via" to via.text.toString(),
                    "email" to mailED.text.toString(),
                    "citta" to city.text.toString()
            )
        }
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(collection)
                .build()
        user!!.updateProfile(profileUpdates)

        val fr = FirestoreRequest_User(db, auth, null, collection, null)
            fr.newUser(this, entry, collection)
            .addOnSuccessListener {
                updateUI(user)
            }
            .addOnFailureListener {
                updateUI(null)
            }
    }
}