package com.sbgdnm.yummyfood.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
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
     * A function that will call the delete function of FirestoreClass that will delete the product added by the user.
     *
     * @param productID To specify which product need to be deleted.
     */
    fun deleteProduct(productID: String) {

        // Здесь мы вызовем функцию delete класса FirestoreClass. Но пока давайте выведем тост-сообщение и вызовем эту функцию из класса адаптера.
        Toast.makeText(
            requireActivity(),
            "Вы удалили $productID",
            Toast.LENGTH_SHORT
        ).show()
    }
}