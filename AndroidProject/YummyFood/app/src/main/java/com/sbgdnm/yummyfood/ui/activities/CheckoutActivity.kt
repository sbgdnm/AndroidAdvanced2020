package com.sbgdnm.yummyfood.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.Address
import com.sbgdnm.yummyfood.models.Cart
import com.sbgdnm.yummyfood.models.DashboardProduct
import com.sbgdnm.yummyfood.models.Order
import com.sbgdnm.yummyfood.ui.adapters.CartItemsListAdapter
import com.sbgdnm.yummyfood.utils.Constants
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {

    // A global variable for the selected address details.
    private var mAddressDetails: Address? = null
    // Global variable for all product list.
    private lateinit var mProductsList: ArrayList<DashboardProduct>
    // Global variable for cart items list.
    private lateinit var mCartItemsList: ArrayList<Cart>
    // A global variable for the SubTotal Amount.
    private var mSubTotal: Double = 0.0
    // A global variable for the Total Amount.
    private var mTotalAmount: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setupActionBar()

        // Получите информацию о выбранном адресе через intent.
        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }

        //Установите выбранные сведения об адресе в пользовательский интерфейс, полученный через intent.
        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_mobile_number.text = mAddressDetails?.mobileNumber
        }

        // Назначить событие click
        btn_place_order.setOnClickListener {
            placeAnOrder()
        }

        getProductList()
    }

    //назад
    private fun setupActionBar() {

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * Функция получения списка товаров для сравнения текущего запаса с товарами корзины.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }

    /**
     * Функция для получения списка элементов корзины в действии.
     */
    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@CheckoutActivity)
    }

    /**
     * Функция для получения результата успеха списка продуктов.
     */
    fun successProductsListFromFireStore(productsList: ArrayList<DashboardProduct>) {
        mProductsList = productsList

        // Call the function to get the latest cart items.
        getCartItemsList()
    }


    /**
     * Функция уведомления об успешном результате списка товаров корзины из cloud firestore.
     */
    fun successCartItemsList(cartList: ArrayList<Cart>) {

        //загрузка
        hideProgressDialog()
        // Обновите количество запасов в списке корзины из списка продуктов.
        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItemsList = cartList

        //  Заполните элементы корзины в пользовательском интерфейсе.
        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        // Передайте необходимый парам.
        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter



        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }

        tv_checkout_sub_total.text = "TNG $mSubTotal"

        tv_checkout_shipping_charge.text = "% 10.0"

        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE
            val percent = (mSubTotal * 10)/100
            mTotalAmount = mSubTotal + percent
            tv_checkout_total_amount.text = "TNG $mTotalAmount"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }
    }



    /**
     * Функция подготовки деталей заказа для размещения заказа.
     */
    private fun placeAnOrder() {

        // загрузка
        showProgressDialog(resources.getString(R.string.please_wait))

        //  Теперь подготовьте детали заказа на основе всех необходимых деталей.
        val order = Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "Мой заказ ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mSubTotal.toString(),
            "% 10.0",
            mTotalAmount.toString(),
            System.currentTimeMillis()
        )


        // Вызовите функцию для размещения заказа в облачном магазине firestore.
        FirestoreClass().placeOrder(this@CheckoutActivity, order)
    }

    /**
     * Функция уведомления об успешном результате размещения заказа.
     */
    fun orderPlacedSuccess() {
        FirestoreClass().updateAllDetails(this@CheckoutActivity, mCartItemsList)
    }

    /**
     * Функция уведомления об успешном результате после обновления всех необходимых деталей.
     */
    fun allDetailsUpdatedSuccessfully() {

        hideProgressDialog()

        Toast.makeText(this@CheckoutActivity, "Ваш заказ успешно принят.", Toast.LENGTH_SHORT)
            .show()
        //после того как успешно заказали , переходит в dashboard , так же , корзина очищена , а заказ в обработке
        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}