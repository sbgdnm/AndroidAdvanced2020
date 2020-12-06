package com.sbgdnm.yummyfood.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sbgdnm.yummyfood.models.User
import com.sbgdnm.yummyfood.ui.activities.auth.UserProfileActivity
import com.sbgdnm.yummyfood.ui.activities.auth.LoginActivity
import com.sbgdnm.yummyfood.ui.activities.auth.RegisterActivity
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
                }

            }
            .addOnFailureListener { e ->
                // Закрываем progress dialog и если есть ошибка то выводим ошибку
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
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
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {

        //получение ссылки на хранилище
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
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
                        }
                    }
            }
            .addOnFailureListener { exception ->
                // Закрываем диалоговое окно прогресса т.е. загрузки, если есть какая-либо ошибка. То показываем ошибку в журнале.
                when (activity) {
                    is UserProfileActivity -> {
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
}