package com.MCProject.minimarket_1.gestor

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.MainActivity.Companion.frO
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.RiderActivity
import com.MCProject.minimarket_1.access.util.Order
import com.MCProject.minimarket_1.user.MarketAviableActivity
import com.MCProject.minimarket_1.user.UserActivity
import com.MCProject.minimarket_1.user.UserProductListActivity
import android.widget.ArrayAdapter
import com.MCProject.minimarket_1.access.util.ProductListActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.MCProject.minimarket_1.R


class UnusedOrderManager: ListActivity(), AdapterView.OnItemClickListener{

    var orderList = ArrayList<Order>()
    lateinit var homeBtn: ImageButton
    var productList: ArrayList<ProductListActivity.Product> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.gestor_activity_order)

        homeBtn = findViewById(R.id.home_btn)
    }

    override fun onStart() {
        super.onStart()

        orderList.clear()
        productList.clear()
        homeListener()

        frO.getAllOrder(mail, orderList, productList, this)
                .addOnCompleteListener {
                    Log.i("HEY", "OrderList: ${orderList.size}")
                    val itemsAdapter: MyOrdersAdapter =
                            MyOrdersAdapter(
                                    this,
                                    orderList
                            )
                    listView.adapter = itemsAdapter
                }
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


    class MyOrdersAdapter( context: Context,var orders: ArrayList<Order>) : ArrayAdapter<Order>(context, 0, orders) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Get the data item for this position
            var convertView = convertView
            val order = getItem(position)
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.order, parent, false)
            }
            // Lookup view for data population
            val tvName = convertView!!.findViewById<View>(R.id.tvName) as TextView
            val tvHome = convertView.findViewById<View>(R.id.tvHome) as TextView
            // Populate the data into the template view using the data object
            if (order != null) {
                tvName.text = "Order N° $position:   "
                tvHome.text = "Cliente -${order.cliente}-"
            }
            // Return the completed view to render on screen
            return convertView
        }
    }

    private fun homeListener() {
        homeBtn.setOnClickListener {
            val username = auth.currentUser.displayName
            when {
                username.equals("utenti") -> {
                    val intent = Intent(this, UserActivity::class.java)
                    this@UnusedOrderManager.startActivity(intent)
                }
                username.equals("gestori") -> {
                    val intent = Intent(this, GestorActivity::class.java)
                    this@UnusedOrderManager.startActivity(intent)
                }
                username.equals("riders") -> {
                    val intent = Intent(this, RiderActivity::class.java)
                    this@UnusedOrderManager.startActivity(intent)
                }
            }
        }
    }

}