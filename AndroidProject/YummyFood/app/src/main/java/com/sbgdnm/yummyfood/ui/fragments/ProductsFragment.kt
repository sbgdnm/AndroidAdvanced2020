package com.sbgdnm.yummyfood.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.Product
import com.sbgdnm.yummyfood.ui.activities.AddMyProductActivity
import com.sbgdnm.yummyfood.ui.adapters.MyProductsListAdapter
import kotlinx.android.synthetic.main.fragment_products.*


class ProductsFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Если мы хотим использовать меню опций во фрагменте, нам нужно добавить его.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_products, container, false)
        return root
    }

    //Переопределите функцию onCreateOptionMenu и раздуйте файл меню панели мониторинга init.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_myproduct_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    //Переопределите функцию onOptionItemSelected и обработайте элементы действия init.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_add_myproduct -> {
                //Launch the AddMyProductActivity on click of action item.
                startActivity(Intent(activity, AddMyProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

//     Функция для получения успешного списка продуктов из cloud firestore.
//      @param productsList получит список продуктов из cloud firestore.
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        // Скрыть диалог выполнения.
        hideProgressDialog()
        if (productsList.size > 0) {
            rv_my_product_items.visibility = View.VISIBLE   //мы показываем наши рецепты ну продукты
            tv_no_products_found.visibility = View.GONE     //скрываем текст который пишет что у нас пока что нет рецептов

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)   //ставим их  как ланиер лайаут
            rv_my_product_items.setHasFixedSize(true)

            // Передайте значение
            val adapterProducts =
                MyProductsListAdapter(requireActivity(), productsList, this@ProductsFragment)
                rv_my_product_items.adapter = adapterProducts
            } else {//если нету , то обраното текст показываем
                 rv_my_product_items.visibility = View.GONE
                 tv_no_products_found.visibility = View.VISIBLE
            }

    }

    private fun getProductListFromFireStore() {
        // загрузка
        showProgressDialog(resources.getString(R.string.please_wait))

        // Вызовите функцию класса Firestore.
        FirestoreClass().getProductsList(this@ProductsFragment)
    }
    /**
     * Функция, которая вызовет функцию удаления FirestoreClass, которая удалит продукт, добавленный пользователем.
     *
     * @param productID To specify which product need to be deleted.
     */
    fun deleteProduct(productID: String) {
        // Здесь мы вызовем функцию delete класса FirestoreClass.
        showAlertDialogToDeleteProduct( productID)
    }
    /**
     * Функция уведомления об успешном результате удаления продукта из cloud firestore.
     */
    fun productDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        //Получите самый последний список продуктов из cloud firestore.
        getProductListFromFireStore()
    }

    /**
     * Функция отображения диалогового окна оповещения для подтверждения удаления продукта из cloud firestore.
     */
    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //установить заголовок для диалогового окна оповещения
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //установить сообщение для диалогового окна оповещения
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //выполнение позитивного действия
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            //  Вызовите функцию, чтобы удалить продукт из cloud firestore.
            //  Показать диалоговое окно прогресса.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Вызовите функцию класса Firestore.
            FirestoreClass().deleteProduct(this@ProductsFragment, productID)

            dialogInterface.dismiss()
        }

        //выполнение отрицательного действия
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Создать AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Установить другие свойства диалогового окна
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


}