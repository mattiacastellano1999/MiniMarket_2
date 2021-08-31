package com.MCProject.minimarket_1.user

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.MCProject.minimarket_1.R
import com.MCProject.minimarket_1.firestore.FirestoreRequest
import com.MCProject.minimarket_1.access.util.ProductListActivity

/**
 * Crea una lista di prodotti che possono essere comprati dagli utenti
 */

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UserProductListActivity: ProductListActivity() {

    lateinit var gestorEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = auth.currentUser.email

        gestorEmail = intent.extras!!["gestor"].toString()
        fr = FirestoreRequest(db, auth, imgDb, "gestori", gestorEmail)
    }

    override fun onStart() {
        super.onStart()

        titleTv.text = "Product List"
        checkoutBtn.text = "Market List"
        checkoutBtn.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
        addBtn.visibility = View.GONE
        checkoutBtn.visibility = View.VISIBLE
        load.startLoading()
        frM.addData("/profili/gestori/market/$gestorEmail/miei_prodotti", this, productList)
            .addOnCompleteListener{
                var i = 0
                for (doc in it.result) {
                    Log.i("HEY", "PrductsCycle: " + it.result.size())
                    if (productList[i].quantity <= 0) {
                        //inserisco nella lista di prodotti acquistabili solo quelli con quantità > 0
                        productList.removeAt(i)
                    } else {
                        fr.addDataImg(this,
                            doc.data["nome"].toString(),
                            i,
                            productList
                        ).addOnCompleteListener {
                            listAdapter = UserProductAdapter()
                            load.stopLoadingDialog()
                        }
                        i++
                    }
                }
                //listAdapter = ProductAdapter()
            }
        //Check no product with quantity = 0
        productList.forEachIndexed { i, el ->
            if (el.quantity <= 0) {
                //inserisco nella lista di prodotti acquistabili solo quelli con quantità > 0
                productList.removeAt(i)
            }
        }
        checkoutBtn.setOnClickListener {
            finish()
        }

        Log.i("HEY", "Before UserProductAdapter")
    }

    internal inner class UserProductAdapter :
        ArrayAdapter<Product>(
            this@UserProductListActivity,
            R.layout.product,
            R.id.prod_text,
            productList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            Log.i("HEY", "Getting View")
            var convertView = convertView
            var wrapper: TaskWrapper? = null

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.product, null)
                wrapper = TaskWrapper(convertView)
                convertView!!.tag = wrapper
            } else {
                wrapper = convertView.tag as TaskWrapper
            }

            wrapper.removeTooMany()
            Log.i("HEY", "populating form_0")
            wrapper.populateFrom(getItem(position)!!)

            return convertView
        }
    }

    internal inner class TaskWrapper(private val element_gestor_product: View) {

        private var nameTV: TextView? = null
        private var productIV: ImageView? = null
        private var productPriceTV: TextView? = null
        private var productQtyTV: TextView? = null
        private var addThisProdBTN: Button? = null
        private var tableRow: TableRow? = null

        private fun getRow(): TableRow {
            if (tableRow == null) {
                tableRow = element_gestor_product.findViewById(R.id.trow)
            }
            return tableRow!!
        }

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

        private fun getAdd(): Button {
            if(addThisProdBTN == null){
                addThisProdBTN = element_gestor_product.findViewById(R.id.addElement_btn)
            }
            return addThisProdBTN!!
        }

        fun populateFrom(prod: Product) {
            getName().text = prod.name
            Log.i("HEY", "Image URI: ${prod.img}")
            getImg().setImageURI(prod.img)
            getPrice().text = prod.price.toString()
            getQuantity().text = prod.quantity.toString()
            getAdd().visibility = View.VISIBLE
            getAdd().setOnClickListener {
                Log.i("HEY", "Dentro il Cick")
                frM.addDataIntoCart(
                    this@UserProductListActivity,
                    user!!,
                    gestorEmail,
                    prod
                ).addOnCompleteListener {
                    prod.quantity -= 1
                    listAdapter = UserProductAdapter()
                }
            }
            getRow().setOnClickListener {
                //val pop = GestorPopup()
            }
            Log.i("HEY", "form populated")
        }

        /**
         * Elimina gli elementi superflui del Product List Activity
         */
        fun removeTooMany() {
            val edit = element_gestor_product.findViewById<ImageButton>(R.id.editElement_imgBtn)
            edit.visibility = View.GONE
            val delete = element_gestor_product.findViewById<ImageButton>(R.id.removeElement_imgBtn)
            delete.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        productList.clear()
        super.onDestroy()
    }

}
