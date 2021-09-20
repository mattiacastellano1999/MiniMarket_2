package com.MCProject.minimarket_1.util

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
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.MainActivity.Companion.collection
import com.MCProject.minimarket_1.MainActivity.Companion.db
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.rider.RiderActivity
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.firestore.FirestoreRequest
import com.MCProject.minimarket_1.firestore.FirestoreRequest_Marketplace
import com.MCProject.minimarket_1.firestore.FirestoreRequest_Order
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.MCProject.minimarket_1.gestor.GestorPopup
import com.MCProject.minimarket_1.gestor.MarketProductListActivity
import com.MCProject.minimarket_1.user.UserActivity
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
        var quantity: Int,
        var owner: String
        )
    companion object {
        var productList = ArrayList<Product>()
    }
    lateinit var imgDb: FirebaseStorage
    val load = Loading(this)
    lateinit var addBtn: ImageButton
    lateinit var titleTv: TextView
    lateinit var homeBtn: ImageButton
    lateinit var checkoutBtn: Button


    lateinit var popup : GestorPopup
    lateinit var oldProd: String
    lateinit var frO: FirestoreRequest_Order
    lateinit var frM: FirestoreRequest_Marketplace
    lateinit var fr: FirestoreRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        imgDb = FirebaseStorage.getInstance()
        addBtn = findViewById(R.id.add_product_btn)
        checkoutBtn = findViewById(R.id.checkout_btn)
        homeBtn = findViewById(R.id.home_btn)
        titleTv = findViewById(R.id.prod_text)


        popup = GestorPopup(this, productList)
        frO = FirestoreRequest_Order(db, auth, imgDb, collection, mail)
        frM = FirestoreRequest_Marketplace(db, auth, imgDb, collection, mail)
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
            if ( resultCode == Activity.RESULT_OK && data!=null) {
                //set image captured to image view
                popup.start(this, data, requestCode)
            }
        }
        if (requestCode == 121) {
            val intent = Intent(this, MarketProductListActivity::class.java)
            this@ProductListActivity.startActivity(intent)
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