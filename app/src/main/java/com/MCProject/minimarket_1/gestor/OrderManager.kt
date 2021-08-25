package com.MCProject.minimarket_1.gestor

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.util.FirestoreRequest
import com.MCProject.minimarket_1.access.util.Order
import com.MCProject.minimarket_1.access.util.ProductListActivity
import com.MCProject.minimarket_1.user.MarketAviableActivity
import com.MCProject.minimarket_1.user.UserProductListActivity
import com.google.firebase.auth.FirebaseAuth

class OrderManager: ListActivity(), AdapterView.OnItemClickListener{

    var orderList = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.gestor_activity_order)
        MainActivity.fr.getAllOrder(auth.currentUser!!, orderList, this)
    }

    override fun onItemClick(clicked: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //visualizzazione dei prodotti venduti da quel market

        val mview = view as TextView

        MarketAviableActivity.sortedList.forEach {
            if (it.contains(mview.text)) {
                val intent = Intent(this, UserProductListActivity::class.java)
                intent.putExtra("gestor", it[1])
                startActivity(intent)
            }
        }
    }

}