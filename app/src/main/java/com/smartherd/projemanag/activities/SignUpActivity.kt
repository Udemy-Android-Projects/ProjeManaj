package com.smartherd.projemanag.activities

/** UUID -> User Unique ID **/

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.smartherd.projemanag.R
import com.smartherd.projemanag.databinding.ActivitySignUpBinding
import com.smartherd.projemanag.firebase.FireStoreClass
import com.smartherd.projemanag.models.User

class SignUpActivity : BaseActivity() {
    lateinit var binding : ActivitySignUpBinding
    // TODO Signing in the User (Step 1: Create an auth variable of type FirebaseAuth)
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO Signing in the User (Step 2: Initialize the auth variable)
        auth = FirebaseAuth.getInstance()
        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // TODO Designing The SignUp Activity (Step 4: Call the setup actionBar function.)
        setupActionBar()

        // TODO Preparing The Signup Feature in Firebase and in the App (Step 3: Add a click event to the Sign-Up button and call the registerUser function.)
        // START
        // Click event for sign-up button.
        binding.btnSignUp.setOnClickListener{
            registerUser()
        }
    }

    // TODO Designing The SignUp Activity(Step 3: A function for setting up the actionBar.)
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() } //  Enable back button functionality
    }

    // TODO Preparing The Signup Feature in Firebase and in the App (Step 1: A function to validate the entries of a new user.)
    // START
    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String, password: String): Boolean {
        // Check if the user has entered data or not
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }
    // END

    // TODO Preparing The Signup Feature in Firebase and in the App (Step 2: A function to register a new user to the app.)
    // START
    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser(){
        // .trim{ it <= ' '} specifies that the empty spaces should be removed
        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            // TODO Signing in the User (Step 3 : Create a user using their email and password)
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                // Hide the progress dialog
                hideProgressDialog()
                    // If the registration is successfully done
                    if (task.isSuccessful) {
                        // Firebase registered user
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        // Registered Email
                        val registeredEmail = firebaseUser.email!!
                        Toast.makeText(
                            this@SignUpActivity,
                            "$name has account with email $registeredEmail",
                            Toast.LENGTH_SHORT
                        ).show()
                        // TODO Using The Firestore Database to Store User Details (Step 6: Now here we will make an entry in the Database of a new user registered.)
                        // START
                        val user = User(
                            firebaseUser.uid, name, registeredEmail
                        )
                        Log.e("FireStore", user.id)
                        // call the registerUser function of FirestoreClass to make an entry in the database.
                        FireStoreClass().registerUser(this@SignUpActivity, user)
                    // END

                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    // TODO Using The Firestore Database to Store User Details (Step 5: Create a function to be called when the user is registered successfully and entry is made in the firestore database.)
    // START
    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess() {
        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        // Hide the progress dialog
        hideProgressDialog()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Sign-Up Screen
        finish()
    }
}
    // END
