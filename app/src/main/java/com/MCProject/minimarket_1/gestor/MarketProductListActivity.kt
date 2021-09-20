package com.MCProject.minimarket_1.gestor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.MCProject.minimarket_1.MainActivity.Companion.auth
import com.MCProject.minimarket_1.MainActivity.Companion.collection
import com.MCProject.minimarket_1.MainActivity.Companion.mail
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.R.drawable.my_back_arrow
import com.MCProject.minimarket_1.util.ProductListActivity
import com.MCProject.minimarket_1.gestor.GestorPopup.Companion.dialog

/**
 * Activity che permette di gestire i Prodotti al Gestore
 * E di visualizzare il carrello agli Utenti
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
        "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
open class MarketProductListActivity : ProductListActivity() {

    lateinit var adapter: MarketProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = "market"
        user = auth.currentUser.email
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onStart() {
        super.onStart()
        //Adding Data
        load.startLoading()
        adapter = MarketProductAdapter()

        var path = ""
        if(type.equals("cart")) {
            path =  "/profili/$collection/$type/$mail/prodotti"
        } else {
            path =  "/profili/$collection/$type/$mail/miei_prodotti"
        }

        frM.addData(path, this, productList)
            .addOnCompleteListener{
                var i = 0
                for (doc in it.result) {
                    frM.addDataImg(this,
                            doc.data["nome"].toString(),
                            i,
                            productList
                        )
                        .addOnCompleteListener {
                            listAdapter = adapter
                            load.stopLoadingDialog()
                        }
                    i++
                }
                if(it.result.isEmpty){
                    load.stopLoadingDialog()
                }
            }

        titleTv.text = "Your Product"
        checkoutBtn.visibility = View.GONE
        checkoutBtn.text = "Back"
        checkoutBtn.background = resources.getDrawable(my_back_arrow)
        addBtn.visibility = View.VISIBLE
        addBtn.setOnClickListener {
            popup.clearData()
            popup.makePopup(this)
            dialog.setOnDismissListener {
                listAdapter = adapter
            }
        }
    }

    inner class MarketProductAdapter :
        ArrayAdapter<Product>(
            this@MarketProductListActivity,
            R.layout.product,
            R.id.prod_text,
            productList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var convertView = convertView
            var wrapper: TaskWrapper? = null

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.product, null)
                wrapper = TaskWrapper(convertView)
                convertView!!.tag = wrapper
            } else {
                wrapper = convertView.tag as TaskWrapper
            }
            wrapper.populateFrom(getItem(position)!!)

            return convertView
        }
    }

    internal inner class TaskWrapper(private val element_gestor_product: View) {

        private var nameTV: TextView? = null
        private var productIV: ImageView? = null
        private var removeBTN: ImageButton? = null
        private var editBTN: ImageButton? = null
        private var productPriceTV: TextView? = null
        private var productQtyTV: TextView? = null

        private fun getName(): TextView {
            if (nameTV == null) {
                nameTV = element_gestor_product.findViewById(R.id.element_name_tv)
            }
            return nameTV!!
        }

        private fun getImg(): ImageView {
            if (productIV == null) {
                productIV = element_gestor_product.findViewById(R.id.element_iv)
            }
            return productIV!!
        }

        private fun getPrice(): TextView {
            if (productPriceTV == null) {
                productPriceTV = element_gestor_product.findViewById(R.id.element_price_tv)
            }
            return productPriceTV!!
        }

        private fun getQuantity(): TextView {
            if (productQtyTV == null) {
                productQtyTV = element_gestor_product.findViewById(R.id.element_quantity_tv)
            }
            return productQtyTV!!
        }

        private fun getDelete(): ImageButton {
            if (removeBTN == null) {
                removeBTN = element_gestor_product.findViewById(R.id.removeElement_imgBtn)
            }
            return removeBTN!!
        }

        private fun getEdit(): ImageButton {
            if(editBTN == null){
                val addThisProdBTN = element_gestor_product.findViewById<Button>(R.id.addElement_btn)
                addThisProdBTN.visibility = View.GONE
                editBTN = element_gestor_product.findViewById(R.id.editElement_imgBtn)
                editBTN!!.visibility = View.VISIBLE
            }
            return editBTN!!
        }

        fun populateFrom(prod: Product) {
            getName().text = prod.name
            getImg().setImageURI(prod.img)
            getPrice().text = prod.price.toString()
            getQuantity().text = prod.quantity.toString()
            getDelete().setOnClickListener {
                fr.deleteFromDB(this@MarketProductListActivity, prod.name)
                productList.remove(prod)
                popup.oldProd = prod.name
                popup.deleteImageFromFS()
                    .addOnCompleteListener {
                        if (it.isSuccessful)
                            adapter.notifyDataSetChanged()
                    }
            }
            if(type == "market") {getEdit().setOnClickListener {
                    oldProd = prod.name
                    if(!isFinishing) {
                        popup.editProduct(prod)
                        dialog.setOnDismissListener {
                            adapter.notifyDataSetChanged()

                        }
                    } else {

                    }
                }
            } else {
                getDelete().visibility = View.GONE
                getEdit().visibility = View.GONE
            }

        }
    }

}