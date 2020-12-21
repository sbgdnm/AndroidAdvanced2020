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
import com.sbgdnm.yummyfood.models.DashboardProduct
import com.sbgdnm.yummyfood.models.Product
import com.sbgdnm.yummyfood.models.User
import com.sbgdnm.yummyfood.ui.activities.AddMyProductActivity
import com.sbgdnm.yummyfood.ui.activities.DashboardProductDetailsActivity
import com.sbgdnm.yummyfood.ui.activities.RecipeDetailsActivity
import com.sbgdnm.yummyfood.ui.activities.SettingsActivity
import com.sbgdnm.yummyfood.ui.activities.auth.UserProfileActivity
import com.sbgdnm.yummyfood.ui.activities.auth.LoginActivity
import com.sbgdnm.yummyfood.ui.activities.auth.RegisterActivity
import com.sbgdnm.yummyfood.ui.fragments.DashboardFragment
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
     * A function to get the product details based on the product id.
     */
    fun getProductDetails(activity: RecipeDetailsActivity, productId: String) {

        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(Product::class.java)!!

                // Notify the success result.

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

        // The collection name for PRODUCTS
        mFireStore.collection(Constants.DASHBOARD_PRODUCTS)
            .document(productId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(DashboardProduct::class.java)!!

                // Notify the success result.

                activity.productDetailsSuccess(product)

            }
            .addOnFailureListener { e ->

                // закрываем загрузку далее ошибка
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Ошибка при получении деталей о продукте", e)
            }
    }
}