package com.smartherd.projemanag.activities

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
import com.smartherd.projemanag.R
import com.smartherd.projemanag.databinding.ActivityCreateBoardBinding
import com.smartherd.projemanag.firebase.FireStoreClass
import com.smartherd.projemanag.models.Board
import com.smartherd.projemanag.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 3
        private const val PICK_IMAGE_REQUEST_CODE = 4
    }

    var binding : ActivityCreateBoardBinding? = null
    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null
    // TODO Creating a Board Image (Step 5: Create a global variable for User name)
    // START
    private lateinit var mUserName: String
    // END
    // TODO Creating a Board (Step 1: Create a global variable for the Board image URL.)
    // START
    // A global variable for a board image URL
    private var mBoardImageURL: String = ""
    // END

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()
        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = resources.getColor(R.color.colorPrimary);
        }

        // TODO Creating a Board Image (Step 6: Get the username from the intent. The user name is sent here from the profile activity to reduce the number of database accesses/request from the application)
        // START
        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }
        // END

        binding?.ivBoardImage?.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                // Ask for permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE // Each permission has its own code that will be used as the identifier
                )
            }
        }

        // TODO Creating a Board (Step 4: Add a click event for btn_create.)
        binding?.btnCreate?.setOnClickListener {
            // Here if the image is not selected then update the other details of user.
            if (mSelectedImageFileUri != null) {
                uploadBoardImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                // Call a function to update or create a board without updating the image.
                createBoard()
            }
        }

    }
    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE) {
            // If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 showImageChooser()
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun showImageChooser() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI // MediaStore enables the application to access images
        )
        // Launches the image selection of phone storage using the constant code.
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null)
        {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data
            try {
                binding?.ivBoardImage?.let {
                    Glide
                        .with(this@CreateBoardActivity)
                        .load(Uri.parse(mSelectedImageFileUri.toString()))
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(it)
                }
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
    }

    // TODO Creating a Board Image (Step 8: Create a function which will notify the success of board creation.)
    // START
    /**
     * A function for notifying the board is created successfully.
     */
    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        // TODO Loading a Newly Created Board to the RecyclerView (Step 3: Set the result as OK.)
        // START
        setResult(Activity.RESULT_OK)
        // END
        finish()
    }
    // END

    // TODO Creating a Board (Step 2: Create a function to create the board.)
    // START
    /**
     * A function to make an entry of a board in the database.
     */
    private fun createBoard() {
        //  A list is created to add the assigned members.
        //  This can be modified later on as of now the user itself will be the member of the board.
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID()) // adding the current user id.

        // Creating the instance of the Board and adding the values as per parameters.
        val board = Board(
            binding?.etBoardName?.text.toString(),
            mBoardImageURL,
            mUserName, // Will later replace with UUID.name
            assignedUsersArrayList
        )
        FireStoreClass().createBoard(this@CreateBoardActivity, board)
    }

    // TODO Creating a Board (Step 3: Creating the function to upload the Board Image to storage and getting the downloadable URL of the image.)
    // START
    /**
     * A function to upload the Board Image to storage and getting the downloadable URL of the image.
     */
    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("BOARD_IMAGE" + System.currentTimeMillis() + "." + getFileExtension( mSelectedImageFileUri)
        )
        //adding the file to reference
        sRef.putFile(mSelectedImageFileUri!!)
            .addOnSuccessListener {taskSnapshot ->
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
                        mBoardImageURL = uri.toString()
                        // Call a function to create the board.
                        createBoard()
                    }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@CreateBoardActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }


}