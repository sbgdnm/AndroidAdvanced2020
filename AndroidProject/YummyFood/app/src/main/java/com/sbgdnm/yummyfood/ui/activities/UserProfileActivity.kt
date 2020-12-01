package com.sbgdnm.yummyfood.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.models.User
import com.sbgdnm.yummyfood.utils.Constants
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        //Retrieve the User details from intent extra.
        // Create a instance of the User model class.
        var userDetails: User = User()
        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }


        //After receiving the user details from intent set it to the UI.
        // Here, the some of the edittext components are disabled because it is added at a time of Registration.
        et_first_name.isEnabled = false     //их нельзя менять так как они являются статичными , мы их вводим при регистрации
        et_first_name.setText(userDetails.firstName)

        et_last_name.isEnabled = false
        et_last_name.setText(userDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(userDetails.email)
    }

}