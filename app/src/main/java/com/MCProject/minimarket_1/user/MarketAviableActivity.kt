@file:Suppress("DEPRECATION")

package com.MCProject.minimarket_1.user

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.rider.RiderActivity
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.firestore.FirestoreRequest
import com.MCProject.minimarket_1.util.MarketList
import com.MCProject.minimarket_1.util.MyLocation
import com.MCProject.minimarket_1.gestor.GestorActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlin.collections.ArrayList

/**
 * List All Market from the nearest to the most far away
 */
class MarketAviableActivity: ListActivity(), AdapterView.OnItemClickListener  {

    lateinit var marketList: ArrayList<String>
    val load = Loading(this)

    lateinit var tableLayout: TableLayout
    lateinit var textView: TextView
    lateinit var homeBTN: ImageButton
    lateinit var searchButton: Button
    lateinit var addButton: ImageButton
    lateinit var outButton: Button
    lateinit var searchViewED: EditText


    companion object {
        lateinit var sortedList: MutableSet<ArrayList<String>>
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_list)

        textView = findViewById(R.id.prod_text)
        homeBTN = findViewById(R.id.home_btn)
        searchButton = findViewById(R.id.search_btn)
        addButton = findViewById(R.id.add_product_btn)
        outButton = findViewById(R.id.checkout_btn)
        searchViewED = findViewById(R.id.myPosition_ed)


        addButton.visibility = View.GONE
        outButton.visibility = View.GONE
        searchViewED.visibility = View.VISIBLE
        searchButton.visibility = View.VISIBLE
        textView.text = "Market Aviable"


    }

    override fun onStart() {
        super.onStart()
        load.startLoading()

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val imgDb = FirebaseStorage.getInstance()
        val collection = auth.currentUser.displayName
        val mail = auth.currentUser.email
        MarketList
            .getAllMarket(this, FirestoreRequest(db, auth, imgDb, collection, mail))
            .addOnCompleteListener {
                marketList = ArrayList()
                sortedList.forEach {
                    marketList.add(it[0])
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, marketList)

                listAdapter = adapter
                listView.onItemClickListener = this

                val loc = MyLocation(this.applicationContext, this)
                searchViewED.append(loc.reverseGeocoding())

                searchButton.setOnClickListener {
                    val geo = loc.geocoding(searchViewED.text.toString())
                    if(geo.equals(1)){
                        MyLocation.address = searchViewED.text.toString()
                        val i = Intent(this, MarketAviableActivity::class.java)
                        startActivity(i)
                    }
                }
                load.stopLoadingDialog()
            }
            .addOnFailureListener {
                load.stopLoadingDialog()
                Toast.makeText(this, "Error during Markets ordering", Toast.LENGTH_SHORT).show()
            }

        homeBTN.setOnClickListener {
            val username = auth.currentUser.displayName
            when {
                username.equals("utenti") -> {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                }
                username.equals("gestori") -> {
                    val intent = Intent(this, GestorActivity::class.java)
                    startActivity(intent)
                }
                username.equals("riders") -> {
                    val intent = Intent(this, RiderActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        searchViewED.text.clear()
    }

    /*
    *   ["nome azienda"]
        ["email"]
        ["via"]
        ["citta"]
        ["lat"]
        ["lon"]
    */
    override fun onItemClick(clicked: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //visualizzazione dei prodotti venduti da quel market

        val mview = view as TextView

        sortedList.forEach {
            if (it.contains(mview.text)) {
                val intent = Intent(this, UserProductListActivity::class.java)
                intent.putExtra("gestor", it[1])
                startActivity(intent)
            }
        }
    }

}
