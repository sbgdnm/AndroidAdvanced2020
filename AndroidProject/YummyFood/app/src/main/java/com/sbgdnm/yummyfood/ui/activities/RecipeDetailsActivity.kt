package com.sbgdnm.yummyfood.ui.activities

import android.os.Bundle
import android.util.Log
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.Product
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_recipe_details.*

class RecipeDetailsActivity : BaseActivity() {

    // Глобальная переменная для идентификатора продукта.
    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)
        setupActionBar()


        // Получите идентификатор продукта через intent extra
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }

        getProductDetails()
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

        // загрузка
        showProgressDialog(resources.getString(R.string.please_wait))
        // Вызовите функцию FirestoreClass, чтобы получить подробную информацию о продукте.
        FirestoreClass().getProductDetails(this@RecipeDetailsActivity, mProductId)
    }


    /**
     * Функция уведомления об успешном результате получения сведений о продукте на основе идентификатора продукта.
     */
    fun recipeDetailsSuccess(product: Product) {

        // закрыть
        hideProgressDialog()

        // Заполните сведения о продукте в пользовательском интерфейсе.
        GlideLoader(this@RecipeDetailsActivity).loadProductPicture(
            product.image,
            iv_product_detail_image
        )

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "TNG ${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_available_author.text = product.user_name
        tv_product_details_ingredients.text = product.ingredients

    }
}