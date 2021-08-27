package com.MCProject.minimarket_1.user

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.MCProject.minimarket_1.MainActivity
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.gestor.MarketProductListActivity

class CartProductListActivity: MarketProductListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = "cart"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        titleTv.text = "Your Cart"
        addBtn.visibility = View.GONE
        checkoutBtn.visibility = View.VISIBLE
        checkoutBtn.text = ""
        checkoutBtn.background = resources.getDrawable(R.drawable.my_exit)
        checkoutBtn.setOnClickListener {
            //Perform the checkout
            doOrderCheckout()
        }
    }

    /**
     * Perform the order checkout
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @Synchronized
    private fun doOrderCheckout() {
        fr.uploadOrder(productList, this, MainActivity.user!!.email, null)
    }
}
