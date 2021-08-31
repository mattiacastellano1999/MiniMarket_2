package com.MCProject.minimarket_1.gestor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.GestorOrderManagerFragment
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.collection
import com.MCProject.minimarket_1.MainActivity.Companion.db
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Login
import com.MCProject.minimarket_1.access.util.FirebaseMessaging
import com.MCProject.minimarket_1.access.util.ProductListActivity
import com.MCProject.minimarket_1.firestore.FirestoreRequest_User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class GestorActivity: AppCompatActivity() {

    lateinit var prodListImgBtn: ImageButton
    lateinit var logoutImgBtn: ImageButton
    lateinit var riderVisualBtn: Button
    lateinit var orderManageBtn: Button
    val auth = FirebaseAuth.getInstance()
    lateinit var popup : GestorPopup

    val gestorRidersFragment = GestorVisualize_ridersFragment()
    val orderManagerFragment = GestorOrderManagerFragment()


    private val PERMISSION_CODE = 100;
    private val IMAGE_CAPTURE_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.gestor_activity_main)

        prodListImgBtn = findViewById(R.id.listProduct_imgBtn)
        logoutImgBtn = findViewById(R.id.exit_imgBtn)
        riderVisualBtn = findViewById(R.id.locationRider_btn)
        orderManageBtn = findViewById(R.id.orderManage_btn)
        popup = GestorPopup(this, ArrayList<ProductListActivity.Product>(), collection, mail, db)

        if(intent.flags == R.string.SHOWRIDERPOPUP){
            Log.i("HEY", "Intent Started!!")
            /*popup.clearData()
            popup.makePopup(this)
            GestorPopup.dialog.setOnDismissListener {
                Log.e("HEY", "VAL: exited")
            }*/
        }

    }

    override fun onRestart() {
        super.onRestart()
        if(intent.flags == R.string.SHOWRIDERPOPUP){
            Log.i("HEY", "Intent Started!!")
            /*popup.clearData()
            popup.makePopup(this)
            GestorPopup.dialog.setOnDismissListener {
                Log.e("HEY", "VAL: exited")
            }*/
        }

    }

    override fun onStart() {
        super.onStart()

        orderManageBtn.visibility = View.VISIBLE
        riderVisualBtn.visibility = View.VISIBLE

        logoutImgBtn.setOnClickListener {
            auth.signOut()
            if(auth.currentUser == null) {
                val intentLogout = Intent(this, Login::class.java)
                startActivity(intentLogout)
            }
        }

        orderManageBtn.setOnClickListener {
            riderVisualBtn.visibility = View.GONE
            orderManageBtn.visibility = View.GONE
            /*supportFragmentManager.beginTransaction().apply {
                //inserisce in main_fragment il contenuto del userMapsFragment
                replace(R.id.main_fragment, orderManagerFragment)
                commit() //con il commit esegue le azioni descritte prima
            }*/
            val intentLogout = Intent(this, UnusedOrderManager::class.java)
            startActivity(intentLogout)
        }

        riderVisualBtn.setOnClickListener {
            riderVisualBtn.visibility = View.GONE
            orderManageBtn.visibility = View.GONE
            supportFragmentManager.beginTransaction().apply {
                //inserisce in main_fragment il contenuto del userMapsFragment
                replace(R.id.main_fragment, gestorRidersFragment)
                commit() //con il commit esegue le azioni descritte prima
            }
        }

        prodListImgBtn.setOnClickListener {
            Log.i("HEY", "enter ListProd-- $auth")
            val intent = Intent(this, MarketProductListActivity::class.java)
            startActivity(intent)
        }
        val fr = FirestoreRequest_User(FirebaseFirestore.getInstance(), auth, FirebaseStorage.getInstance(), auth.currentUser.displayName, auth.currentUser.email)
        fr.getAllCategoryProfile(this, "riders")

        //Check some notification
        Log.i("HEY", "Check Notify")
        val fm = FirebaseMessaging(MainActivity.mail)
        fm.addRealtimeUpdate(this)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}