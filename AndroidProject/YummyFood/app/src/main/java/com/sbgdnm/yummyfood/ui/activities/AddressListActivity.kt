package com.sbgdnm.yummyfood.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.Address
import com.sbgdnm.yummyfood.ui.adapters.AddressListAdapter
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.SwipeToDeleteCallback
import com.sbgdnm.yummyfood.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*

class AddressListActivity : BaseActivity() {

    // Declare a global variable to select the address.
    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        setupActionBar()

        // Receive the value and initialize the variable to select the address.
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress =
                intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        //  If it is about to select the address then update the title.
        if (mSelectAddress) {
            tv_title.text = resources.getString(R.string.title_select_address)
        }

        //вызов добавление
        tv_add_address.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            //Now to notify the address list about the latest address added we need to make neccessary changes as below.
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }
        getAddressList()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * Функция для получения списка адресов из cloud firestore.
     */
    private fun getAddressList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAddressesList(this@AddressListActivity)
    }

    /**
     * Функция для получения успешного результата списка адресов из cloud firestore.
     */
    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {

        // закрыть загрузку
        hideProgressDialog()

        // Распечатайте весь список адресов в журнале с именем.
        for (i in addressList) {

            Log.i("Name and Address", "${i.name} ::  ${i.address}")
        }


        //  Заполните список адресов в пользовательском интерфейсе.
        if (addressList.size > 0) {

            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList ,mSelectAddress )
            rv_address_list.adapter = addressAdapter

            //Не позволяйте пользователю редактировать или удалять адрес, когда он собирается выбрать его.
            if (!mSelectAddress) {
                //  Добавить swipe, чтобы редактировать элемент.
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        //Вызовите функцию notifyEditItem класса адаптера.
                        val adapter = rv_address_list.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)

                //  Добавьте функцию swipe right для удаления address.
                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // Показать загрузку
                        showProgressDialog(resources.getString(R.string.please_wait))

                        // Вызовите функцию, чтобы удалить адрес из cloud firetore.
                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }
        } else {
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }

    /**
     * Функция уведомляет пользователя о том, что адрес успешно удален.
     */
    fun deleteAddressSuccess() {

        // закрыть загрузку
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()
        getAddressList()
    }


    // Override the onActivityResult function and get the latest address list based on the result code.
    /**
     * Receive the result from a previous call to
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE) {

                getAddressList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }
}