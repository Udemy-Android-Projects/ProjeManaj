package com.smartherd.projemanag.activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
//import com.smartherd.projemanag.Manifest
import com.smartherd.projemanag.R
import com.smartherd.projemanag.databinding.ActivityProfileBinding
import com.smartherd.projemanag.firebase.FireStoreClass
import com.smartherd.projemanag.models.User
import com.smartherd.projemanag.utils.Constants
import java.io.IOException

class ProfileActivity : BaseActivity() {
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    var binding : ActivityProfileBinding? = null
    // TODO Image Chooser for Profile Image (Step 6: Add a global variable for URI of a selected image from phone storage. This uri defines the mobile phone location)
    // START
    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // TODO  Uploading An image to storage (Step 4: Create a global variable for a user profile image URL)
    private var mProfileImageURL: String = ""
    // END

    // TODO Updating the User Data Via HashMap in the FireStore Database (Step 3: Create a global variable to store user details)
    // A global variable for user details.
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()
        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = resources.getColor(R.color.colorPrimary);
        }
        // TODO Populating The Profile Activity With User Data (Step 1: Call a function to get the current logged in user details.)
        // START
        FireStoreClass().signedInUserDetails(this@ProfileActivity)
        // END

        // TODO Image Chooser for Profile Image (Step 2: Add a click event for iv_profile_user_image.)
        // START
        binding?.ivUserImage?.setOnClickListener {
            // Check if permission are granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            {
                // TODO Image Chooser for Profile Image (Step 5: Call the image chooser function.)
                // START
                showImageChooser()
                // END
            } else {
                // Ask for permission
                /*Requests permissions to be granted to this application. These permissions
                must be requested in your manifest, they should not be granted to your app,
                and they should have protection level*/
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE // Each permission has its own code that will be used as the identifier
                )
            }
        }

        // TODO Uploading An image to storage  (Step 5: Add a click event for updating the user profile data to the database.)
        binding?.btnUpdate?.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else { // Update other information
                // TODO Updating the User Data Via HashMap in the FireStore Database (Step 7: Upload the update in the update button)
                updateUserProfileData()
            }
        }

    }

    // TODO Image Chooser for Profile Image (Step 3: Check the result of runtime permission after the user allows or deny based on the unique code.)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO Image Chooser for Profile Image (Step 5: Call the image chooser function.)
                // START
                showImageChooser()
                // END
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // TODO Image Chooser for Profile Image (Step 4: Create a function for image selection from phone storage.)
    private fun showImageChooser() {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI // MediaStore enables the application to access images
        )
        // Launches the image selection of phone storage using the constant code.
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE) // startActivityForResult used since we want to get data or we expect data back
    }


    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    // TODO Populating The Profile Activity With User Data (Step 2: Create a function to set the existing data in UI.)
    // START
    /**
     * A function to set the existing details in UI.
     */
    fun setUserDataInUI(user: User) {

        // TODO Updating the User Data Via HashMap in the FireStore Database (Step 4: Initialize user details variable here since this is where we fetch user details)
        // Initialize the user details variable
        mUserDetails = user

        binding?.ivUserImage?.let {
            Glide
                .with(this@ProfileActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(it)
        }
        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if(user.mobile != 0L)
            binding?.etMobile?.setText(user.mobile.toString())
    }

    // TODO Updating the User Data Via HashMap in the FireStore Database(Step 6: Create a function in the profile activity to update the user profile details into the database.)

    private fun updateUserProfileData() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val userHashMap = HashMap<String, Any>()
        var anyChanges = false
        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image /* Ensures that the image isn't the same as the existing one */) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChanges = true
        }
        if (binding?.etName?.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = binding?.etName?.text.toString()
            anyChanges = true
        }

        if (binding?.etMobile?.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = binding?.etMobile?.text.toString().toLong()
            anyChanges = true
        }

        // Update the data in the database.
        if(anyChanges)
            FireStoreClass().updateUserProfileData(this@ProfileActivity, userHashMap)
    }


    // TODO Image Chooser for Profile Image (Step 7: Get the result of the image selection based on the constant code.)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null)
        {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data
            try {
                // Load the user image in the ImageView.
                binding?.ivUserImage?.let {
                    Glide
                        .with(this@ProfileActivity)
                        .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                        .centerCrop() // Scale type of the image.
                        .placeholder(R.drawable.ic_baseline_account_circle_24) // A default place holder
                        .into(it)
                } // the view in which the image will be loaded.
            }catch(e : IOException) {
                e.printStackTrace()
            }

        }
    }

    // TODO Uploading An image to storage (Step 2: Create a function to get the extension of the file passed in order to know what to do.)
    // START
    /**
     * A function to get the extension of selected image.
     */
    private fun getFileExtension(uri: Uri?): String? {
        /*
       * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa. Multi-Purpose Internet Mail Extensions. It is a development to the Internet email protocol that enables its users to exchange several kinds of data files over the Internet, including images, audio, and video.
       *
       * getSingleton(): Get the singleton instance of MimeTypeMap.
       *
       * getExtensionFromMimeType: Return the registered extension for the given MIME type.
       *
       * contentResolver.getType: Return the MIME type of the given content URL.
       */
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    // TODO Uploading An image to storage (Step 3: Create a function to upload the selected user image to storage and get the url of it to store in the database.)
    // START
    // Before start with database we need to perform some steps in Firebase Console and after adding a dependency in Gradle file.
    // Follow the Steps:
    // Step 1: Go to the "Storage" tab in the Firebase Console in your project details in the navigation bar under "Develop".
    // Step 2: In the Storage Page click on the Get Started. Click on Next
    // Step 3: As we have already selected the storage location while creating the database so now click the Done button.
    // Step 4: Now the storage bucket is created.
    // Step 5: For more details visit the link: https://firebase.google.com/docs/storage/android/start
    // Step 6: Now add the code to upload image.
    /**
     * A function to upload the selected user image to firebase cloud storage.
     */
    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri != null) {
            //getting the storage reference where we put our image
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child( //The name of the image/item to be stored is defined within these brackets....make it unique
                "USER_PROFILE_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                    mSelectedImageFileUri
                )
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot -> // This snapshot contains the link that will be stored in the firestore database
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            // TODO Updating the User Data Via HashMap in the FireStore Database (Step 7: Upload the update)
                            updateUserProfileData()
                        }
                    hideProgressDialog()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@ProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()
                    hideProgressDialog()
                }
        }
    }

    // TODO Updating the User Data Via HashMap in the FireStore Database (Step 1: Create a function to notify the user profile is updated successfully.)
    fun profileUpdateSuccess() {
        hideProgressDialog()
        // TODO Updating the Main Activity Profile Details via ActivityForResult (Step 3: Set a flag that indicates that the process of updating the user details is successful)
        setResult(Activity.RESULT_OK)
        finish() // Close the profile activity so the user doesn't have to do it manually
    }
    }