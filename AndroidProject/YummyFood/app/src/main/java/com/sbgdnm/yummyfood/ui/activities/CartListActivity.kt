package com.sbgdnm.yummyfood.ui.activities


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.Cart
import com.sbgdnm.yummyfood.models.DashboardProduct
import com.sbgdnm.yummyfood.ui.adapters.CartItemsListAdapter
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {
    //  global variable for the product list.
    private lateinit var mProductsList: ArrayList<DashboardProduct>
    // A global variable for the cart list items.
    private lateinit var mCartListItems: ArrayList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionBar()

    }
    //для того чтоб вернуться назад
    private fun setupActionBar() {

        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }


    //  Override the onResume function and call the function to getCartItemsList.
    override fun onResume() {
        super.onResume()
        getProductList()
    }

    // function to get the list of cart items in the activity.
    private fun getCartItemsList() {
        // Show the progress dialog.
     //  showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartListActivity)
    }

    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CartListActivity)
    }
    /**
     * A function to notify the success result of the cart items list from cloud firestore.
     */
    fun successCartItemsList(cartList: ArrayList<Cart>) {
        // Hide progress dialog.
        hideProgressDialog()

        //Compare the product id of product list with product id of cart items list and update the stock quantity in the cart items list from the latest product list.
        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {

                    cart.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0){
                        cart.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        //проверка
        if ( mCartListItems.size > 0) {

            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, cartList)
            rv_cart_items_list.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in  mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }
            }

            tv_sub_total.text = "TNG $subTotal"
            // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
            tv_shipping_charge.text = "% 10"

            if (subTotal > 0) {
                ll_checkout.visibility = View.VISIBLE
                val percent = (subTotal * 10)/100
                val total = subTotal + percent
                tv_total_amount.text = "TNG $total"
            } else {
                ll_checkout.visibility = View.GONE
            }

        } else {//когда пусто
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }
    /**
     * A function to get the success result of product list.
     *
     * @param productsList
     */
    fun successProductsListFromFireStore(productsList: ArrayList<DashboardProduct>) {
        //Initialize the product list global variable once we have the product list.
        mProductsList = productsList
        // Once we have the latest product list from cloud firestore get the cart items list from cloud firestore.
        getCartItemsList()

    }

    /**
     * A function to notify the user about the item removed from the cart list.
     */
    fun itemRemovedSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }

    /**
     * A function to notify the user about the item quantity updated in the cart list.
     */
    fun itemUpdateSuccess() {

        hideProgressDialog()

        getCartItemsList()
    }
}