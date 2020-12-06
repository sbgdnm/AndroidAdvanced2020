package com.sbgdnm.yummyfood.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.models.User
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    //Экземпляр класса модели пользовательских данных. Мы инициализируем его позже.
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        //Ивзлекаем данные пользователя
        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Получием сведения о пользователе из intent в виде ParcelableExtra.
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }


        //После получения реквизитов пользователей показываем данные на интерфейсе
        // Здесь некоторые компоненты edittext отключены, поскольку они добавляются во время регистрации.
        et_first_name.isEnabled = false     //их нельзя менять так как они являются статичными , мы их вводим при регистрации
        et_first_name.setText(mUserDetails.firstName)

        et_last_name.isEnabled = false
        et_last_name.setText(mUserDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)

        // Назначьте событие on click фотографии профиля пользователя.
        iv_user_photo.setOnClickListener(this@UserProfileActivity)

        // Назначим  событие нажатия на кнопку "Сохранить".
        btn_submit.setOnClickListener(this@UserProfileActivity)
    }
    //Override the onClick функция для события добавленее фотографии
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    // Здесь мы проверим, разрешено ли разрешение уже или нам нужно его запросить.
                    // Прежде всего мы проверим разрешение READ_EXTERNAL_STORAGE, и если оно не будет разрешено, мы запросим его
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        //вызовием функцию выбора изображения (из constants), когда у пользователя уже есть разрешение на чтение хранилища.
                        Constants.showImageChooser(this@UserProfileActivity)
                    } else {

                        /*Запрашивает разрешения, которые должны быть предоставлены этому приложению.
                        Эти разрешения должны быть запрошены в вашем манифесте, они не должны быть предоставлены вашему приложению,
                         и они должны иметь уровень защиты*/


                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit ->{
                    //если validateUserProfileDetails() все окей , то ..
                    if(validateUserProfileDetails()){

                        //Создаем HashMap пользовательских данных для обновления в базе данных и добавляем значения init.
                        val userHashMap = HashMap<String, Any>()

                        // Здесь поле, которое не редактируется, не нуждается в обновлении. Итак, сейчас мы обновим номер мобильного телефона пользователя и его пол.

                        //Здесь мы получаем текст из editText и обрезаем пространство
                        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

                        val gender = if (rb_male.isChecked) {
                            Constants.MALE
                        } else {
                            Constants.FEMALE
                        }

                        if (mobileNumber.isNotEmpty()) {
                            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                        }
                        //хэш мап принимает ключь gender и value male or female
                        userHashMap[Constants.GENDER] = gender

                        // показываем Загрузка
                        showProgressDialog(resources.getString(R.string.please_wait))

                        // вызоваем функцию registerUser класса FireStore, чтобы сделать запись в базе данных.
                        FirestoreClass().updateUserProfileData(
                            this@UserProfileActivity,
                            userHashMap)
                        //showErrorSnackBar("Ваши данные действительны. Вы можете обновить их.",false)
                    }
                }
            }
        }
    }

    // Override функция для идентификации результата разрешения среды выполнения после того,
    // как пользователь разрешает или запрещает разрешение на основе уникального кода.

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
                Constants.showImageChooser(this@UserProfileActivity)
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
    /**
     * @param requestCode integer запрос ,предоставленный startActivityForResult (), позволяет определить, от кого пришел этот результат
     * @param resultCode integer код результата
     * @param data Intent,которое может возвращать вызывающему данные результата
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // Uri выбранного изображения из памяти телефона.
                        val selectedImageFileUri = data.data!!
                        //Передаем нужные параметры glide функцию для установления фото , glide функция работает более быстро и оптемизирует фотографию , тип ну , если большая фотография то норм ставится
                        GlideLoader(this@UserProfileActivity).loadUserPicture(
                            selectedImageFileUri,
                            iv_user_photo
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // когда пользователь закрывает или отменяет выбор изображения.
            Log.e("Запрос Отменен", "Выбор изображения отменяется")
        }
    }


    // Функция проверки входных данных для получения сведений о профиле.
    private fun validateUserProfileDetails(): Boolean {
        return when {

            // Мы сохранили изображение профиля пользователя необязательным.
            // Имя, Фамилия и идентификатор электронной почты не редактируются, когда они приходят с экрана входа в систему.
            // Переключатель для пола всегда имеет выбранное по умолчанию значение.

            // Проверяем, не пуст ли номер мобильного телефона, так как он обязательно должен быть введен.
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    //Функция для уведомления об успешном результате и дальнейшего выполнения соответствующих действий после обновления сведений о пользователе.
    fun userProfileUpdateSuccess() {
        //Закрываем загрузку
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        // Перенаправление на главный экран после завершения профиля.
        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
        finish()
    }

}