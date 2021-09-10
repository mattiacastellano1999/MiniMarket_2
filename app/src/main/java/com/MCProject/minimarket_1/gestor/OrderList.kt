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
import com.MCProject.minimarket_1.R


class OrderList: ListActivity(){

    var orderList = ArrayList<Order>()
    lateinit var homeBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.gestor_activity_order)

        homeBtn = findViewById(R.id.home_btn)
    }

    override fun onStart() {
        super.onStart()

        orderList.clear()
        homeListener()

        frO.getAllOrder(mail, orderList, this)
                .addOnCompleteListener {
                    Log.i("HEY", "OrderList: ${orderList.size}")
                    val itemsAdapter: MyOrdersAdapter =
                            MyOrdersAdapter(
                                    this,
                                    orderList
                            )
                    listView.adapter = itemsAdapter
                    listView.setOnItemClickListener { parent, view, position, id ->
                        val order = orderList[position] 
                        Log.i("HEY", "Clicked "+order)

                        val cliente = order.cliente
                        val gestore = order.proprietario
                        val orderN = order.nome_ordine

                        val intent = Intent(this, OrderManagerActivity::class.java)
                        intent.putExtra("cliente", cliente)
                        intent.putExtra("gestore", gestore)
                        intent.putExtra("nome_ordine", orderN)
                        intent.putExtra("rStatus", order.riderStatus)
                        this@OrderList.startActivity(intent)
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
                tvName.text = order.nome_ordine
                tvHome.text = "Order From: "+ order.cliente + "\nTot: " + order.prezzo_tot
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
                    this@OrderList.startActivity(intent)
                }
                username.equals("gestori") -> {
                    val intent = Intent(this, GestorActivity::class.java)
                    this@OrderList.startActivity(intent)
                }
                username.equals("riders") -> {
                    val intent = Intent(this, RiderActivity::class.java)
                    this@OrderList.startActivity(intent)
                }
            }
        }
    }

}
