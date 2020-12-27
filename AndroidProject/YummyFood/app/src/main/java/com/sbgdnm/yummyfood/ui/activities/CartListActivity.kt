package com.sbgdnm.yummyfood.ui.activities


import android.content.Intent
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
import com.sbgdnm.yummyfood.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {
    // глобальная переменная для списка продуктов.
    private lateinit var mProductsList: ArrayList<DashboardProduct>
    // Глобальная переменная для элементов списка корзины.
    private lateinit var mCartListItems: ArrayList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionBar()

        // Assign the click event to the checkout button and proceed to the next screen.
        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }

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


    override fun onResume() {
        super.onResume()
        getProductList()
    }

    // функция для получения списка элементов корзины в действии.
    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CartListActivity)
    }

    /**
     * Функция получения списка товаров для сравнения текущего запаса с товарами корзины.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CartListActivity)
    }
    /**
     * Функция уведомления об успешном результате списка товаров корзины из cloud firestore.
     */
    fun successCartItemsList(cartList: ArrayList<Cart>) {
        // закрыть загрузку
        hideProgressDialog()

        //Сравните идентификатор продукта списка продуктов с идентификатором продукта списка товаров корзины
        // и обновите количество запасов в списке товаров корзины из последнего списка продуктов.
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

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity,  mCartListItems ,true )
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
            // Здесь мы сохранили стоимость доставки фиксированной как %10, но в вашем случае это может быть Кэри.
            // Кроме того, это зависит от местоположения и общей суммы.
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
     * Функция для получения результата успеха списка продуктов.
     *
     * @param productsList
     */
    fun successProductsListFromFireStore(productsList: ArrayList<DashboardProduct>) {
        //Инициализируйте глобальную переменную списка продуктов,
        // как только у нас будет список продуктов.
        mProductsList = productsList
        // Как только у нас будет последний список продуктов из cloud firestore, получите список товаров корзины из cloud firestore.
        getCartItemsList()

    }

    /**
     * Функция уведомления Пользователя о товаре, удаленном из списка корзины.
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
     * Функция уведомления Пользователя об обновленном количестве товара в списке корзины.
     */
    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }
}