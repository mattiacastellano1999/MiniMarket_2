package com.MCProject.minimarket_1.gestor

import android.os.Bundle
import android.util.Log
import android.view.View
import com.MCProject.minimarket_1.MainActivity.Companion.db
import com.MCProject.minimarket_1.gestor.OrderList.Companion.orderSelected
import com.MCProject.minimarket_1.util.Chat

class ChatGestor: Chat() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title1.text = orderSelected.rider

        title2.visibility = View.GONE
        button2.visibility = View.GONE
        messageBox2.visibility = View.GONE
        textBox2.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()


    }
}
