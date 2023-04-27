package com.smartherd.projemanag.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.smartherd.projemanag.R
import com.smartherd.projemanag.databinding.DialogProgressBinding

// TODO Setting Up The BaseActivity For Reuse Function (Step 3: Here we have created a BaseActivity Class in which we have added the progress dialog and SnackBar. Now all the activity will extend the BaseActivity instead of AppCompatActivity.)
open class BaseActivity : AppCompatActivity() {
    var basebinding : DialogProgressBinding? = null

    // If user presses back button more than once the app will close
    private var doubleBackToExitPressedOnce = false
    /**
     * This is a progress dialog instance which we will initialize later on.
     *
     * Display this dialog to the user each time something is loading/ happening in the background
     **/

    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    /**
     * This function is used to show the progress dialog with the title and message to user.
     */

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        basebinding = DialogProgressBinding.inflate(layoutInflater)
        /* Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen. */
        basebinding?.root?.let { mProgressDialog.setContentView(it) }

        basebinding?.tvProgressText?.text = text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    open fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit() {
        // If back button pressed twice within 2 seconds the application closes
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        // If clicked once doubleBackToExitPressedOnce is set to true since one click and two clicks are dependent events
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        // If the back button is not pressed again after 2 seconds everything is reset
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    // Show errors using snack bar
    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.snackbar_error_color
            )
        )
        snackBar.show()
    }

}