package com.MCProject.minimarket_1.rider

import android.content.Intent
import android.util.Log
import android.webkit.WebView
import android.widget.TextView
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.MainActivity.Companion.frO
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.gestor.ChatGestor
import com.MCProject.minimarket_1.gestor.OrderList
import com.MCProject.minimarket_1.gestor.OrderManagerActivity

/**
 * Visualizza lo storico delle consegne con il relativo esito
 */
class DeliveryHistory: OrderList() {

    override fun populateWithOrder() {

        val order_text = findViewById<TextView>(R.id.order_text)
        order_text.text = "Old Delivery List"

        val username = auth.currentUser!!.displayName
        frO.getAllOrder(orderList, this).addOnCompleteListener { @Synchronized
            if ( it.isSuccessful) {
                if (!it.result.isEmpty) {
                    for (doc in it.result) {
                        val order = frO.parseOrder(doc)
                        if (order.orderStatus == getString(R.string.order_status_complete)) {
                            if (username == "riders" && order.rider == mail) {
                                orderList.add(order)
                            } else if (username == "gestori" && order.proprietario == mail) {
                                orderList.add(order)
                            } else if (username == "utenti" && order.cliente == mail) {
                                orderList.add(order)
                            }
                        }
                    }
                    val itemsAdapter = MyOrdersAdapter(
                        this,
                        orderList
                    )
                    listView.adapter = itemsAdapter
                    listView.setOnItemClickListener { parent, view, position, id ->
                        orderSelected = orderList[position]
                        Log.i("HEY", "Clicked " + orderSelected.rider.toString())

                        val cliente = orderSelected.cliente
                        val gestore = orderSelected.proprietario
                        val orderN = orderSelected.ordine

                        /*if (username == "gestori" && orderSelected.riderStatus == getString(R.string.rider_status_accepted)) {
                            val intent = Intent(this, ChatGestor::class.java)
                            startActivity(intent)
                        } else {*/
                        val intent = Intent(this, DeliveryHistoryManager::class.java)
                        intent.putExtra("testo", cliente)
                        intent.putExtra("gestore", gestore)
                        intent.putExtra("nome_ordine", orderN)
                        intent.putExtra("rStatus", orderSelected.riderStatus)
                        intent.putExtra("dStatus", orderSelected.deliveryStatus)
                        intent.putExtra("rider", orderSelected.rider)
                        this@DeliveryHistory.startActivity(intent)
                        //}
                    }
                } else {
                    Log.e("HEY", "Error with Path")
                }
            } else {
                Log.e("HEY", "Error Firetore Marker Reading_0")
            }
        }
    }
}
