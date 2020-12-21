package com.sbgdnm.yummyfood.ui.activities


import android.os.Bundle
import android.util.Log
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.DashboardProduct
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_dashboard_product_details.*

class DashboardProductDetailsActivity : BaseActivity() {
    // A global variable for product id.
    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_product_details)

        // Get the product id through the intent extra and print it in the log.
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }
        setupActionBar()
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
     * A function to call the firestore class function that will get the product details from cloud firestore based on the product id.
     */
    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getDashboardProductDetails(this@DashboardProductDetailsActivity, mProductId)
    }

    /**
     * A function to notify the success result of the product details based on the product id.
     */
    fun productDetailsSuccess(product: DashboardProduct) {

        // Hide Progress dialog.
        hideProgressDialog()

        // Populate the product details in the UI.
        GlideLoader(this@DashboardProductDetailsActivity).loadProductPicture(
            product.image,
            iv_product_detail_image
        )

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "TNG ${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity

    }
}