package com.smartherd.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.smartherd.projemanag.R
import com.smartherd.projemanag.databinding.ActivitySignInBinding
import com.smartherd.projemanag.models.User

class SignInActivity : BaseActivity() {
    // TODO Signing in the User (Step 4: Create an auth variable of type FirebaseAuth)
    private lateinit var auth : FirebaseAuth

    lateinit var binding : ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO Signing in the User (Step 5: Initialize the auth variable)
        auth = FirebaseAuth.getInstance()
        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }

        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    // TODO Signing in the User (Step 6: A function for Sign-In using the registered user using the email and password.)
    // START
    /**
     * A function for Sign-In using the registered user using the email and password.
     */
    private fun signInRegisteredUser() {
        // Here we get the text from editText and trim the space
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        // Validate form checks if the user has entered something
        if (validateForm(email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            // Sign-In using FirebaseAuth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // Placed here since communication with the server is complete from the client(phone) end and a response is returned
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@SignInActivity,
                            "You have successfully signed in.",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    }
                    else
                    {
                        Toast.makeText(
                            this@SignInActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }

    // TODO Signing In And Getting User Data (Step 3: Create a function to get the user details from the firestore database after authentication.)
    // START
    /**
     * A function to get the user details from the firestore database after authentication.
     */
    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }
    // END
}