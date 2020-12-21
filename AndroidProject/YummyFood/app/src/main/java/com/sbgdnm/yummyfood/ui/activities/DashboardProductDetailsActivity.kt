package com.sbgdnm.yummyfood.ui.activities


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.Cart
import com.sbgdnm.yummyfood.models.DashboardProduct
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_dashboard_product_details.*

class DashboardProductDetailsActivity : BaseActivity() , View.OnClickListener {

    // Глобальная переменная для идентификатора продукта.
    private var mProductId: String = ""
    private lateinit var mProductDetails: DashboardProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_product_details)

        // Получите идентификатор продукта через intent extra и распечатайте его в журнале.
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }
        var productOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == productOwnerId) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        setupActionBar()
        getProductDetails()
        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }

    //для того чтоб вернуться назад
    private fun setupActionBar() {

        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * Функция для вызова функции класса firestore, которая будет получать сведения о продукте из cloud firestore на основе идентификатора продукта.
     */
    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getDashboardProductDetails(this@DashboardProductDetailsActivity, mProductId)
    }

    /**
     * Функция уведомления об успешном результате получения сведений о продукте на основе идентификатора продукта.
     */
    fun productDetailsSuccess(product: DashboardProduct) {
        //Инициализируйте переменную
        mProductDetails = product
        // скрыть загрузку
        hideProgressDialog()

        //Заполните сведения о продукте в пользовательском интерфейсе.
        GlideLoader(this@DashboardProductDetailsActivity).loadProductPicture(
            product.image,
            iv_product_detail_image
        )

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "TNG ${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity

        FirestoreClass().checkIfItemExistInCart(this@DashboardProductDetailsActivity, mProductId)
    }

    //функция нажатия на баттон добавление в карты
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }
            }
        }
    }

    /**
     * Функция подготовки элемента cart для добавления его в заказы.
     */
    private fun addToCart() {
        val addToCart = Cart(
            FirestoreClass().getCurrentUserID(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this@DashboardProductDetailsActivity, addToCart)
    }

    //  Создайте функцию для уведомления об успешном результате добавления товара в корзину.
    fun addToCartSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@DashboardProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()
        // Скройте кнопку AddToCart, если товар уже находится в корзине.
        btn_add_to_cart.visibility = View.GONE
        // Покажите кнопку GoToCart, если товар уже находится в корзине. Пользователь может обновить количество на экране списка корзины, если он хочет.
        btn_go_to_cart.visibility = View.VISIBLE
    }
    /**
     *Функция уведомления об успешном результате товара существует в корзине.
     */
    fun productExistsInCart() {

        hideProgressDialog()
        // Скройте кнопку AddToCart, если товар уже находится в корзине.
        btn_add_to_cart.visibility = View.GONE
        // Покажите кнопку GoToCart, если товар уже находится в корзине. Пользователь может обновить количество на экране списка корзины, если он хочет.
        btn_go_to_cart.visibility = View.VISIBLE
    }

}