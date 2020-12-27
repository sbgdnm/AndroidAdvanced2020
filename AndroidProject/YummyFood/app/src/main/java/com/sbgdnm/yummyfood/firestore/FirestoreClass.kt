package com.sbgdnm.yummyfood.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sbgdnm.yummyfood.models.*
import com.sbgdnm.yummyfood.ui.activities.*
import com.sbgdnm.yummyfood.ui.activities.auth.UserProfileActivity
import com.sbgdnm.yummyfood.ui.activities.auth.LoginActivity
import com.sbgdnm.yummyfood.ui.activities.auth.RegisterActivity
import com.sbgdnm.yummyfood.ui.fragments.DashboardFragment
import com.sbgdnm.yummyfood.ui.fragments.OrdersFragment
import com.sbgdnm.yummyfood.ui.fragments.ProductsFragment
import com.sbgdnm.yummyfood.ui.fragments.RecipeFragment
import com.sbgdnm.yummyfood.utils.Constants

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
        //Принимает регистр активити и данные юзера который зарегистрировался
    fun registerUser(activity: RegisterActivity, userInfo: User) {
        // Создаем колекцию users , так же если она уже создана то больше не будет создовать
        mFireStore.collection(Constants.USERS)
            // Идентификатор документа для полей пользователей. Здесь документ - это идентификатор пользователя. т.е. беред его айдишку
            .document(userInfo.id)
            //Здесь userInfo-это поле, а SetOption - это слияние. Это для того, если мы хотим слиться позже, а не заменять поля.
            .set(userInfo, SetOptions.merge()) //set даем значение а get получаем значения . get используется в низу в функции получения детальных данных пользователя
            .addOnSuccessListener {
                //Здесь вызывается функция из регистр активити для передачи ей результата.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Произошла ошибка при регистрации.",
                    e
                )
            }
    }

        //Функция для получения идентификатора пользователя текущего зарегистрированного пользователя.
    fun getCurrentUserID(): String {
        // инициализируем Экземпляр currentUser, использующий FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Переменная для присвоения currentUserId, если она не является нулевой, то даем айди , присваиваем
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

        //Функция для получения зарегистрированных сведений о пользователе из базы данных FireStore.
    fun getUserDetails(activity: Activity) {

        // Здесь мы передаем имя коллекции, из которой нам нужны данные.
        mFireStore.collection(Constants.USERS)
            // Идентификатор документа для получения полей пользователя.
            .document(getCurrentUserID())
            .get()//get получаем данные из дб
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Здесь мы получили снимок документа, который преобразуется в объект модели пользовательских данных.
                val user = document.toObject(User::class.java)!!
                    //Создайте экземпляр Android SharedPreferences.
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MY_YF_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                // создаем editor который поможет нам отредактировать SharedPreference.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,       //key
                    "${user.firstName} ${user.lastName}" //value
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        // Вызовите функцию базового действия для передачи ей результата.
                        activity.userLoggedInSuccess(user)
                    }
                    // Внесите изменения, чтобы отправить результат успеха в соответствующее действие.
                    is SettingsActivity ->{
                        // Вызовите функцию базового класса.
                        // Вызовите функцию базовой активности для передачи ей результата.
                        activity.userDetailsSuccess(user)

                    }

                }

            }
            .addOnFailureListener { e ->
                // Закрываем progress dialog и если есть ошибка то выводим ошибку
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    // Hide the progress dialog if there is any error for the respective error.
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Ошибка при получении сведений о пользователе.",
                    e
                )
            }
    }

    //Функция обновления данных профиля пользователя в базе данных.
     // @param activity используется для идентификации activity которому передается результат.
     // @param userHashMap HashMap полей, которые должны быть обновлены.
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // имя коллекции
        mFireStore.collection(Constants.USERS)
            // Идентификатор документа, по которому будут обновляться данные. Здесь идентификатор документа -
            // это текущий идентификатор вошедшего в систему пользователя.
            .document(getCurrentUserID())
            // A HashMap полей, которые должны быть обновлены.
            .update(userHashMap)
            .addOnSuccessListener {

                //Сообщите об успешном результате.
                when (activity) {
                    is UserProfileActivity -> {
                        // Вызоваем функцию base activity для передачи ей результата.
                        activity.userProfileUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        // Скрываем диалоговое окно загрузки, если есть какая-либо ошибка. И показываем ошибку в журнале.
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Ошибка при обновлении сведений о пользователе.",
                    e
                )
            }
    }

    // Функция загрузки изображения в облачное хранилище.
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri? , imageType: String) {

        //получение ссылки на хранилище
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //добавление файла в ссылку
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                //Загрузка изображения прошла успешно
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                // Получите загружаемый url-адрес из моментального снимка задачи
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("URL загружаемого изображения", uri.toString())

                        // Здесь вызывается функция базовой активности для передачи ей результата.
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddMyProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                // Закрываем диалоговое окно прогресса т.е. загрузки, если есть какая-либо ошибка. То показываем ошибку в журнале.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddMyProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }


     //Функция для внесения записи продукта пользователя в облачную базу данных firestore.
    fun uploadProductDetails(activity: AddMyProductActivity, productInfo: Product) {

        mFireStore.collection(Constants.PRODUCTS)
            .document()
            // Здесь userInfo-это поле, а SetOption - слияние. Это для того, если мы хотим слиться
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Здесь вызывается функция базовой активности для передачи ей результата.
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Ошибка при загрузке сведений о продукте(рецепте).",
                    e
                )
            }
    }

    //Функция для получения списка продуктов из cloud firestore.
     // @param fragment The fragment is passed as parameter as the function is called from fragment and need to the success result.
    fun getProductsList(fragment: Fragment) {
        // Название коллекции для продуктов
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() //Получите снимки документов.
            .addOnSuccessListener { document ->

                //Здесь мы получаем список досок в виде документов.
                Log.e("Список рецептов(продуктов)", document.documents.toString())

                // Здесь мы создали новый экземпляр для Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // Цикл for В соответствии со списком документов для преобразования их в Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e("Получит список продуктов(рецептов)", "Ошибка при получении списка рецептов(продуктов)", e)
            }
    }

    /**
     * Функция для получения списка элементов "рецепт". Список будет представлять собой общий список элементов, а не основанный на идентификаторе пользователя.
     */
    fun getRecipeItemsList(fragment: RecipeFragment) {
        // Название коллекции для продуктов
        mFireStore.collection(Constants.PRODUCTS)
            .get() // Получите снимки документов.
            .addOnSuccessListener { document ->

                // Здесь мы получаем список досок в виде документов.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Здесь мы создали новый экземпляр для Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                //Цикл for В соответствии со списком документов для преобразования их в Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                // Передайте результат успеха базовому фрагменту.
                fragment.successRecipeItemsList(productsList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting recipe items list.", e)
            }
    }
    /**
     * Функция удаления продукта из облачного магазина firestore.
     */
    fun deleteProduct(fragment: ProductsFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                // Сообщите об успешном результате базовому классу.
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Ошибка при удалении продукта!",
                    e
                )
            }
    }
    //для получения списка в dashboard
    fun getDashboardItemsList(fragment: DashboardFragment) {
        // Название коллекции для продуктов
        mFireStore.collection(Constants.DASHBOARD_PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Здесь мы получаем список досок в виде документов.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Здесь мы создали новый экземпляр для Products ArrayList.
                val dashboard_productsList: ArrayList<DashboardProduct> = ArrayList()

                // Цикл for В соответствии со списком документов для преобразования их в Products ArrayList.
                for (i in document.documents) {

                    val dashboard_product = i.toObject(DashboardProduct::class.java)!!
                    dashboard_product.product_id = i.id
                    dashboard_productsList.add(dashboard_product)
                }

                // Передайте результат успеха базовому фрагменту.
                fragment.successDashboardItemsList(dashboard_productsList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Ошибка при получении списка элиментов в dashboard", e)
            }
    }

    /**
     * Функция для получения сведений о продукте на основе id product рецепт
     */
    fun getProductDetails(activity: RecipeDetailsActivity, productId: String) {

        // Название коллекции для продуктов
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get() // получаем документ
            .addOnSuccessListener { document ->

                //Здесь мы получаем информацию о продукте в виде документа.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Преобразуйте snapshot в объект класса модели данных продукта.
                val product = document.toObject(Product::class.java)!!

                //Сообщите об успешном результате.
                activity.recipeDetailsSuccess(product)

            }
            .addOnFailureListener { e ->

                // закрываем загрузку далее ошибка
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Ошибка при получении деталей о продукте", e)
            }
    }
    /**
     * Продукты в dashboard
     */
    fun getDashboardProductDetails(activity: DashboardProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.DASHBOARD_PRODUCTS)
            .document(productId)
            .get() // получаем документ
            .addOnSuccessListener { document ->

                //Здесь мы получаем информацию о продукте в виде документа.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Преобразуйте snapshot в объект класса модели данных продукта.
                val product = document.toObject(DashboardProduct::class.java)!!

                //Сообщите об успешном результате.
                activity.productDetailsSuccess(product)

            }
            .addOnFailureListener { e ->

                // закрываем загрузку далее ошибка
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Ошибка при получении деталей о продукте", e)
            }
    }

    /**
     * Функция добавления товара в корзину(для заказа) в облачном firestore.
     */
    fun addCartItems(activity: DashboardProductDetailsActivity, addToCart: Cart) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            // Здесь userInfo-это поле, а SetOption - это слияние. Это для того, если мы хотим слиться
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {

                // передайем  результат.
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }


    /**
     * Функция проверки того, существует ли товар уже в корзине или нет.
     */
    fun checkIfItemExistInCart(activity: DashboardProductDetailsActivity, productId: String) {

        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Если размер документа больше 1, это означает, что товар уже добавлен в корзину.
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }

            }
            .addOnFailureListener { e ->
                // Скрыть диалоговое окно выполнения, если есть ошибка.
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }

    /**
     * Функция получения списка товаров корзины из  firestore.
     */
    fun getCartList(activity: Activity) {
        // Название коллекции для продуктов
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // получаем документ
            .addOnSuccessListener { document ->

                //Здесь мы получаем информацию о продукте в виде документа.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Здесь мы создали новый экземпляр для ArrayList элементов cart.
                val list: ArrayList<Cart> = ArrayList()

                for (i in document.documents) {

                    val cartItem = i.toObject(Cart::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }
                // Сообщаем об успешном результате.
                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                // закрываем загрузку далее ошибка
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }

    /**
     * Функция для получения всего списка продуктов из  firestore.
     *
     * @param activity activity передается в качестве параметра функции, потому что оно вызывается из activity .
     */
    fun getAllProductsList(activity: Activity) {
        // Название коллекции для продуктов
        mFireStore.collection(Constants.DASHBOARD_PRODUCTS)
            .get() //  получаем документ
            .addOnSuccessListener { document ->

                //Здесь мы получаем информацию о продукте в виде документа.
                Log.e("Products List", document.documents.toString())

                // Здесь мы создали новый экземпляр для Products ArrayList.
                val productsList: ArrayList<DashboardProduct> = ArrayList()

                for (i in document.documents) {

                    val product = i.toObject(DashboardProduct::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (activity) {
                    is CartListActivity -> {
                        //  Передайте результат успеха списка продуктов в activity cart list.
                        activity.successProductsListFromFireStore(productsList)
                    }

                    //  Notify the success result of latest cart items list to checkout screen.
                    is CheckoutActivity -> {
                        //  Передайте результат успеха списка продуктов в activity cart list.
                        activity.successProductsListFromFireStore(productsList)
                    }
                }


            }
            .addOnFailureListener { e ->
                // закрываем загрузку далее ошибка
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }

                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }

    /**
     * Функция удаления элемента корзины из  firestore.
     */
    fun removeItemFromCart(context: Context, cart_id: String) {
        // Название коллекции
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {
                // Уведомить об успешном результате удаленного элемента корзины из списка в базовый класс.
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }


    /**
     * Функция обновления элемента корзины в  firestore.
     *
     * @param activity activity class.
     * @param id cart id в списке.
     * @param itemHashMap значений обновлении.
     */
    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        //Название коллекции
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .update(itemHashMap) // Хэш-карта полей, которые должны быть обновлены.
            .addOnSuccessListener {

                // Уведомить базовый класс об успешном результате обновления списка элементов корзины.
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }
    /**
     *Функция добавления адреса в firestore.
     *
     * @param activity
     * @param addressInfo
     */
    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {

        // Название коллекции
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            // Здесь addressInfo-это поле, а SetOption - это слияние. Это для того, если мы хотим слиться
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Здесь вызывается функция базовой активности для передачи ей результата.
                activity.addUpdateAddressSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    /**
     *Функция для получения списка адресов из firestore.
     *
     * @param activity
     */
    fun getAddressesList(activity: AddressListActivity) {
        // Название коллекции
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // получаем документ
            .addOnSuccessListener { document ->
                // Здесь мы получаем список  в виде документов.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Здесь мы создали новый экземпляр для address ArrayList.
                val addressList: ArrayList<Address> = ArrayList()

                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                activity.successAddressListFromFirestore(addressList)

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }

    }

    /**
     * Функция обновить существующие решения в firestore.
     */
    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            //Здесь addressInfo-это поле, а SetOption - это слияние. Это для того, если мы хотим слиться
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Здесь вызывается функция базовой активности для передачи ей результата.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }

    /**
     * Функция удаления существующего адреса из firestore.
     */
    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                // Здесь вызывается функция базовой активности для передачи ей результата.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }

    /**
     * A function to place an order of the user in the cloud firestore.
     */
    fun placeOrder(activity: CheckoutActivity, order: Order) {

        mFireStore.collection(Constants.ORDERS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(order, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.orderPlacedSuccess()

            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error.
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }


    /**
     * A function to update all the required details in the cloud firestore after placing the order successfully.
     */
    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<Cart>) {

        val writeBatch = mFireStore.batch()

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.DASHBOARD_PRODUCTS)
                .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            //  Finally after performing all the operation notify the user with the success result.
            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(activity.javaClass.simpleName, "Error while updating all the details after order placed.", e)
        }
    }

    /**
     * A function to get the list of orders from cloud firestore.
     */
    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                //  Notify the success result to base class.
                fragment.populateOrdersListInUI(list)

            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }
}