package com.sbgdnm.yummyfood.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.models.User
import com.sbgdnm.yummyfood.utils.Constants
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        //Ивзлекаем данные пользователя
        //сперва создаем экзампляр класса пользователя
        var userDetails: User = User()
        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Получием сведения о пользователе из intent в виде ParcelableExtra.
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }


        //После получения реквизитов пользователей показываем данные на интерфейсе
        // Здесь некоторые компоненты edittext отключены, поскольку они добавляются во время регистрации.
        et_first_name.isEnabled = false     //их нельзя менять так как они являются статичными , мы их вводим при регистрации
        et_first_name.setText(userDetails.firstName)

        et_last_name.isEnabled = false
        et_last_name.setText(userDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(userDetails.email)

        // Назначьте событие on click фотографии профиля пользователя.
        iv_user_photo.setOnClickListener(this@UserProfileActivity)
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

                        iv_user_photo.setImageURI(Uri.parse(selectedImageFileUri.toString()))
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

}