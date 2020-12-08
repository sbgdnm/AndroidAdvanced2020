package com.sbgdnm.yummyfood.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.Product
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_my_product.*
import java.io.IOException

class AddMyProductActivity : BaseActivity() , View.OnClickListener {

    // A global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for uploaded product image URL.
    private var mProductImageURL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_my_product)
        setupActionBar()

        iv_add_update_product.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_my_product_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_my_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                // The permission code is similar to the user profile image selection.
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) //добавлен в манивесте , для доступа и открытия галереи в телефоне
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddMyProductActivity)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                        must be requested in your manifest, they should not be granted to your app,
                        and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {
                    if (validateProductDetails()) {
                        uploadProductImage()
                    }
                }

            }
        }
    }

    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //Если разрешение уже есть
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //вызывием функцию выбора изображения (из constants), когда у пользователя уже есть разрешение на чтение хранилища.
                Constants.showImageChooser(this@AddMyProductActivity)
            } else {
                //если разрешения не дали
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Получаем результат после выбора изображения из памяти телефона, используя уникальный код, который мы передали в момент выбора через intent.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // Replace the add icon with edit icon once the image is selected.
            iv_add_update_product.setImageDrawable(
                ContextCompat.getDrawable(
                    this@AddMyProductActivity,
                    R.drawable.ic_vector_edit
                )
            )
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!

            try {
                // Load the product image in the ImageView.
                GlideLoader(this@AddMyProductActivity).loadUserPicture(
                    mSelectedImageFileUri!!,
                    iv_product_image
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * A function to validate the product details.
     */
    private fun validateProductDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_product_ingredients.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_ingredients),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }
    //A function to upload the selected product image to firebase cloud storage.
    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(
            this@AddMyProductActivity,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )
    }

    private fun uploadProductDetails() {

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
            this.getSharedPreferences(Constants.MY_YF_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!

        // Here we get the text from editText and trim the space
        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            et_product_title.text.toString().trim { it <= ' ' },
            et_product_price.text.toString().trim { it <= ' ' },
            et_product_description.text.toString().trim { it <= ' ' },
            et_product_ingredients.text.toString().trim { it <= ' ' },
            mProductImageURL
        )
        //когда все уже подготовлено отпрявляем в firestoreclass где там загрухят в сайт firebase
        FirestoreClass().uploadProductDetails(this@AddMyProductActivity, product)
    }
    //A function to get the successful result of product image upload.
    //Функция уведомления об успешном результате загрузки изображения в облачное хранилище.
    //@param imageURL После успешной загрузки Firebase Cloud возвращает URL-адрес.
    fun imageUploadSuccess(imageURL: String) {

        // Initialize the global image url variable.
        mProductImageURL = imageURL

        uploadProductDetails()
    }

    /**
     * A function to return the successful result of Product upload.
     */
    fun productUploadSuccess() {
        // Hide the progress dialog
        hideProgressDialog()
        Toast.makeText(
            this@AddMyProductActivity,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}