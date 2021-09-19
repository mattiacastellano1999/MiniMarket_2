package com.MCProject.minimarket_1.gestor

import android.annotation.SuppressLint
import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.MainActivity.Companion.frO
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.rider.RiderActivity
import com.MCProject.minimarket_1.util.Order
import com.MCProject.minimarket_1.user.UserActivity
import android.widget.ArrayAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.MCProject.minimarket_1.MainActivity.Companion.homeListener
import com.MCProject.minimarket_1.R
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot


open class OrderList: ListActivity(){

    companion object{
        var orderList = ArrayList<Order>()
        lateinit var orderSelected: Order
    }

    lateinit var homeBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.gestor_order_list)

        homeBtn = findViewById(R.id.home_btn)
    }

    override fun onStart() {
        super.onStart()

        orderList.clear()
        homeListener(this, homeBtn)

        populateWithOrder()
    }

    open fun populateWithOrder() {
        frO.getAllOrder( orderList, this)
            .addOnCompleteListener {@Synchronized
                if ( it.isSuccessful) {
                    if( !it.result.isEmpty) {
                    var order: Order
                        for (doc in it.result) {
                            order = frO.parseOrder(doc)
                            if(order.deliveryStatus == null) {
                                orderList.add(order)
                            }
                        }
                    } else {
                        Log.e("HEY", "Error with Path")
                    }
                } else {
                    Log.e("HEY", "Error Firetore Marker Reading_0")
                }

                /*Log.i("HEY", "OrderList: ${orderList.size}")
                for (order in orderList) {
                    if(order.deliveryStatus != null) {
                        orderList.remove(order)
                    }
                }*/
                val itemsAdapter: MyOrdersAdapter =
                    MyOrdersAdapter(
                        this,
                        orderList
                    )
                listView.adapter = itemsAdapter
                listView.setOnItemClickListener { parent, view, position, id ->
                    orderSelected = orderList[position]
                    Log.i("HEY", "Clicked "+orderSelected.rider.toString())

                    val cliente = orderSelected.cliente
                    val gestore = orderSelected.proprietario
                    val orderN = orderSelected.ordine

                    if(orderSelected.riderStatus == getString(R.string.rider_status_accepted)){
                        val intent = Intent(this, ChatGestor::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, OrderManagerActivity::class.java)
                        intent.putExtra("cliente", cliente)
                        intent.putExtra("gestore", gestore)
                        intent.putExtra("nome_ordine", orderN)
                        intent.putExtra("rStatus", orderSelected.riderStatus)
                        this@OrderList.startActivity(intent)
                    }
                }
            }
    }

    class MyOrdersAdapter( context: Context,var orders: ArrayList<Order>) : ArrayAdapter<Order>(context, 0, orders) {
        @SuppressLint("SetTextI18n")
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
                tvName.text = order.ordine
                tvHome.text = "Order From: "+ order.cliente + "\nTot: " + order.prezzo_tot
            }
            // Return the completed view to render on screen
            return convertView
        }
    }
}
