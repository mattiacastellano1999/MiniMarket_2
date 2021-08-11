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
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.MCProject.minimarket_1.user.UserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.concurrent.thread


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    val REQUESTSTORAGEACCESS = 1001
    val REQUESTLOCATIONACCESS = 1010
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var auth: String
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Check if user is correctly logged
         */
        firebaseAuth = FirebaseAuth.getInstance()

        val intentLogout = Intent(this, Login::class.java)
        if(firebaseAuth.currentUser == null) {
            startActivity(intentLogout)
        } else {
            user = firebaseAuth.currentUser
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
                auth = user!!.displayName
                if (auth.equals("utenti")) {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                }
                if (auth.equals("rider")) {
                    val intent = Intent(this, RiderActivity::class.java)
                    startActivity(intent)
                }
                if (auth.equals("gestori")) {
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