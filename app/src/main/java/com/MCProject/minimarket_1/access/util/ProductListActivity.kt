package com.MCProject.minimarket_1.access.util

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.RiderActivity
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.MCProject.minimarket_1.gestor.GestorPopup
import com.MCProject.minimarket_1.gestor.MarketProductListActivity
import com.MCProject.minimarket_1.user.UserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

open class ProductListActivity: ListActivity() {
    var user: String? = null
    var type: String = "market"

    data class Product(
        var id: Int,
        var img: Uri?,
        var name: String,
        var description: String,
        var price: Double,
        var quantity: Int)
    var productList = ArrayList<Product>()

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var imgDb: FirebaseStorage
    lateinit var collection: String
    lateinit var mail: String
    val load = Loading(this)
    lateinit var addBtn: ImageButton
    lateinit var titleTv: TextView
    lateinit var homeBtn: ImageButton
    lateinit var checkoutBtn: Button


    lateinit var popup : GestorPopup
    lateinit var oldProd: String
    lateinit var fr: FirestoreRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)
        Log.i("HEY", "Product List Activity")

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        imgDb = FirebaseStorage.getInstance()
        addBtn = findViewById(R.id.add_product_btn)
        checkoutBtn = findViewById(R.id.checkout_btn)
        homeBtn = findViewById(R.id.home_btn)
        titleTv = findViewById(R.id.prod_text)

        collection = auth.currentUser.displayName
        mail = auth.currentUser.email

        popup = GestorPopup(this, productList, collection, mail, db)
        fr = FirestoreRequest(db, auth, imgDb, collection, mail)
    }

    override fun onStart() {
        super.onStart()
        //back user/gestor/rider Home page
        homeBtn.setOnClickListener {
            val username = auth.currentUser.displayName
            when {
                username.equals("utenti") -> {
                    val intent = Intent(this, UserActivity::class.java)
                    this@ProductListActivity.startActivity(intent)
                }
                username.equals("gestori") -> {
                    val intent = Intent(this, GestorActivity::class.java)
                    this@ProductListActivity.startActivity(intent)
                }
                username.equals("riders") -> {
                    val intent = Intent(this, RiderActivity::class.java)
                    this@ProductListActivity.startActivity(intent)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == popup.IMAGE_CAPTURE_CODE || requestCode == popup.IMAGE_LOCAL_CODE ) {
            Log.i("HEY", "REQUEST_FOR_IMAGE")
            if ( resultCode == Activity.RESULT_OK && data!=null) {
                //set image captured to image view
                Log.i("HEY", "Immagine: " + data)
                popup.start(data, requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            popup.PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    popup.openCamera(this)
                }
                else{
                    Toast.makeText(this, "Permission denied, U can allow it in the settings", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}