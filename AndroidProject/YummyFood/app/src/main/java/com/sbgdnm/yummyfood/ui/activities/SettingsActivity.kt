package com.sbgdnm.yummyfood.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sbgdnm.yummyfood.R
import com.sbgdnm.yummyfood.firestore.FirestoreClass
import com.sbgdnm.yummyfood.models.User
import com.sbgdnm.yummyfood.ui.activities.auth.LoginActivity
import com.sbgdnm.yummyfood.ui.activities.auth.UserProfileActivity
import com.sbgdnm.yummyfood.utils.Constants
import com.sbgdnm.yummyfood.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() , View.OnClickListener{

    // A variable for user details which will be initialized later on.
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Call the function to setup action bar.
        setupActionBar()

        //Assign the onclick event to the edit text
        tv_edit.setOnClickListener(this@SettingsActivity)
        // Assign the onclick event to the logout button.
        btn_logout.setOnClickListener(this@SettingsActivity)
    }
    //Override the onResume function and call the getUserDetails function init.
    override fun onResume() {
        super.onResume()

        getUserDetails()
    }


    // Create a function to setup action bar.
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {
        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }


    //Create a function to get the user details from firestore.
    /**
     * A function to get the user details from firestore.
     */
    private fun getUserDetails() {
        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        // Call the function of Firestore class to get the user details from firestore which is already created.
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    // Create a function to receive the success result.
    /**
     * A function to receive the user details and populate it in the UI.
     */
    fun userDetailsSuccess(user: User) {
        //Initialize the mUserDetails variable.
        mUserDetails = user
        //Set the user details to UI.
        // Hide the progress dialog
        hideProgressDialog()

        // Load the image using the Glide Loader class.
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)

        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = "${user.mobile}"

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                // Call the User Profile Activity to add the Edit Profile feature to the app. Pass the user details through intent.
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

                //Add Logout feature when user clicks on logout button.
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

            }
        }
    }


}