package com.MCProject.minimarket_1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.firestore.FirestoreRequest
import com.MCProject.minimarket_1.firestore.FirestoreRequest_Marketplace
import com.MCProject.minimarket_1.firestore.FirestoreRequest_Order
import com.MCProject.minimarket_1.firestore.FirestoreRequest_User
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.MCProject.minimarket_1.user.UserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    val REQUESTSTORAGEACCESS = 1001
    val REQUESTLOCATIONACCESS = 1010

    companion object{
        lateinit var authString: String
        lateinit var auth: FirebaseAuth
        var user: FirebaseUser? = null
        lateinit var db: FirebaseFirestore
        lateinit var imgDb: FirebaseStorage
        lateinit var frO: FirestoreRequest_Order
        lateinit var frU: FirestoreRequest_User
        lateinit var frM: FirestoreRequest_Marketplace
        lateinit var collection: String
        lateinit var mail: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authString = ""
        db = FirebaseFirestore.getInstance()
        imgDb = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        collection = auth.currentUser.displayName
        mail = auth.currentUser.email
        frO = FirestoreRequest_Order(db, auth, imgDb, collection, mail)
        frU = FirestoreRequest_User(db, auth, imgDb, collection, mail)
        frM = FirestoreRequest_Marketplace(db, auth, imgDb, collection, mail)

        /**
         * Check if user is correctly logged
         */
        val intentLogout = Intent(this, Login::class.java)
        if(auth.currentUser == null) {
            startActivity(intentLogout)
        } else {
            user = auth.currentUser
        }
    }

    override fun onStart() {
        super.onStart()
        /**
         * Check external storage
         */
        if (
            ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUESTSTORAGEACCESS)
        } else if (
            ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUESTLOCATIONACCESS)
        } else {

            if (user != null) {
                authString = user!!.displayName
                if (authString.equals("utenti")) {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                }
                if (authString.equals("riders")) {
                    val intent = Intent(this, RiderActivity::class.java)
                    startActivity(intent)
                }
                if (authString.equals("gestori")) {
                    val intent = Intent(this, GestorActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUESTSTORAGEACCESS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //ok
                recreate()
            }
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                    this,
                    "Unable to show Product Images - permission required",
                    Toast.LENGTH_LONG
                ).show()
                    .run {
                        recreate()
                    }
            }
        }

        if (requestCode == REQUESTLOCATIONACCESS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //ok
                recreate()
            }
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                    this,
                    "Unable to show location - permission required",
                    Toast.LENGTH_LONG
                ).show()
                    .run {
                        recreate()
                    }
            }
        }
    }
}