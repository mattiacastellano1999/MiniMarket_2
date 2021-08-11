package com.MCProject.minimarket_1.user

import android.os.Bundle
import android.view.View
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.access.Loading
import com.MCProject.minimarket_1.access.util.ProductListActivity
import com.MCProject.minimarket_1.gestor.MarketProductListActivity

class CartProductListActivity: MarketProductListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = "cart"
    }

    override fun onStart() {
        super.onStart()

        titleTv.text = "Your Cart"
        addBtn.visibility = View.GONE
        checkoutBtn.visibility = View.VISIBLE
        checkoutBtn.text = ""
        checkoutBtn.background = resources.getDrawable(R.drawable.my_exit)
        checkoutBtn.setOnClickListener {
            /*val intent = Intent(this, Checkout::class.java)
            this@ProductListActivity.startActivity(intent)*/
            //Perform the checkout
            doOrderCheckout()
        }
    }

    /**
     * Perform the order checkout
     */
    private fun doOrderCheckout() {
        val load = Loading(this)
        load.startLoading()
    }
}
