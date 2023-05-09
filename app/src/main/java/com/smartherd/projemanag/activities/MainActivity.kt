package com.smartherd.projemanag.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging
import com.smartherd.projemanag.R
import com.smartherd.projemanag.adapter.BoardItemAdapter
import com.smartherd.projemanag.databinding.ActivityMainBinding
import com.smartherd.projemanag.databinding.AppBarMainBinding
import com.smartherd.projemanag.databinding.NavHeaderMainBinding
import com.smartherd.projemanag.firebase.FireStoreClass
import com.smartherd.projemanag.models.Board
import com.smartherd.projemanag.models.User
import com.smartherd.projemanag.utils.Constants


// TODO Adding Drawer Functionality (Step 5: Implement the NavigationView.OnNavigationItemSelectedListener and add the implement members of it.)
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    var binding : ActivityMainBinding? = null
    var navViewBinding: NavHeaderMainBinding? = null
    // TODO Creating a Board Image (Step 2: Create a global variable for user name)
    // START
    private lateinit var mUserName: String
    // END
    // TODO Adding the Token to the DB (Step 2: Add a global variable for SharedPreferences.)
    // START
    // A global variable for SharedPreferences
    private lateinit var mSharedPreferences: SharedPreferences
    // END

    // TODO Updating the Main Activity Profile Details via ActivityForResult (Step 1 : Declare a constant variable that will be a unique code for starting the activity for result)
    companion object {
        const val MY_PROFILE_REQUEST_CODE = 11
        // TODO Loading a Newly Created Board to the RecyclerView (Step 1: Add a unique code for starting the create board activity for result)
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Binding the header view to the main activity view
        navViewBinding = NavHeaderMainBinding.bind(binding?.navView!!.getHeaderView(0))
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setupActionBar()
        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = resources.getColor(R.color.colorPrimary);
        }
        // TODO Loading the Image and Username to Display it in the Drawer (Step 5: Call a function to get the current logged in user details.Note that at this point the concept of boards was not introduced)
        // START
        // Get the current logged in user details.
        FireStoreClass().signedInUserDetails(this@MainActivity)

        // TODO Adding Drawer Functionality (Step 3: Add click event for navigation in the action bar and call the toggleDrawer function.)
        // START
        binding?.appBarMain?.toolbarMainActivity?.setNavigationOnClickListener {
            toggleDrawer()
        }
        binding?.appBarMain?.fabCreateBoard?.setOnClickListener {
            // TODO Creating a Board Image (Step 4: Pass the user name through intent to CreateBoardScreen.)
            // START
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            // TODO Loading a Newly Created Board to the RecyclerView (Step 2: Here now pass the unique code for StartActivityForResult.)
            // START
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
            // END
        }
        // END
        // TODO Adding Drawer Functionality (Step 7: Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.)
        // START
        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        binding!!.navView.setNavigationItemSelectedListener(this)
        // END

        // TODO Adding the Token to the DB (Step 3: Initialize the mSharedPreferences variable.)
        // START
        mSharedPreferences = this.getSharedPreferences(Constants.PROGEMANAG_PREFERENCES, Context.MODE_PRIVATE /** This mode ensures that the data stored using preferences is available within this application and nowhere else */ )
        // END

        // TODO Adding the Token to the DB (Step 7: Get the FCM token and update it in the database.)
        // START
        // Variable is used get the value either token is updated in the database or not.
        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        // Here if the token is already updated than we don't need to update it every time.
        if (tokenUpdated) {
            // Get the current logged in user details.
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().signedInUserDetails(this@MainActivity)
        } else { // Get the updated token
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateFCMToken(task.result)
                }
            })
        }

    }
    // TODO Adding Drawer Functionality(Step 1: Create a function to setup action bar.)
    // START
    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {
        setSupportActionBar(binding?.appBarMain?.toolbarMainActivity)
        supportActionBar?.title = null
        binding?.appBarMain?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        //Set typeface
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding?.appBarMain?.toolbarTitle?.typeface = typeface
    }
    // END

    // TODO  Adding Drawer Functionality (Step 2: Create a function for opening and closing the Navigation Drawer.)
    // START
    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            binding?.drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }
    // END

    // TODO Adding Drawer Functionality (Step 4: Add a onBackPressed function and check if the navigation drawer is open or closed.)
    // START
    override fun onBackPressed() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // TODO Adding Drawer Functionality (Step 6: Add the click events of navigation menu items.)
        // START
        when (item.itemId) {
            R.id.nav_my_profile -> {
                // TODO Updating the Main Activity Profile Details via ActivityForResult (Step 2: Use start activity for result instead)
                // This line ensures that a result is returned from the ProfileActivity
                startActivityForResult(Intent(this@MainActivity, ProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // TODO Adding the Token to the DB (Step 8: Clear the shared preferences when the user signOut.)
                // START
                mSharedPreferences.edit().clear().apply()
                // END

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                // If activity to be opened is already running in the stack instead of creating a new instance of the activity we will clear all the activities until the activity to be opened is at the top of the stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        // END
        return true
    }
    // END

    // TODO Updating the Main Activity Profile Details via ActivityForResult (Step 4: Get the updated user data)
    // Get the result that was returned
    // This method combined with the requestCode enables one to get data from any class
    // But in this case it is used as a refresh functionality
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK /* RESULT_OK is the returned entity */ && requestCode == MY_PROFILE_REQUEST_CODE) {
            FireStoreClass().signedInUserDetails(this)
        }
        // TODO Loading a Newly Created Board to the RecyclerView (Step 4: Here if the result is OK get the updated boards list.)
        // START
        else if (resultCode == Activity.RESULT_OK
            && requestCode == CREATE_BOARD_REQUEST_CODE
        ) {
            // Get the latest boards list.
            FireStoreClass().getBoardsList(this@MainActivity)
        }
        // END
        else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    // TODO Loading the Image and Username to Display it in the Drawer (Step 4: Create a function to update the user details in the navigation view.)
    fun updateNavigationUserDetails(user: User) {
        hideProgressDialog()
        // TODO Creating a Board Image (Step 3: Initialize the UserName variable.)
        // START
        mUserName = user.name
        // END

        // Load the user image in the ImageView.
        navViewBinding?.ivUserImage?.let {
            Glide
                .with(this@MainActivity)
                .load(user.image) // URL of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_baseline_account_circle_24) // A default place holder
                .into(it)
        } // the view in which the image will be loaded.

        // Username
       navViewBinding?.tvUsername?.text = user.name

        // TODO Preparing The Boards Recyclerview Adapter and UI Elements (Step 4:Get the list of boards.)
        // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.fetching_boards))
            FireStoreClass().getBoardsList(this@MainActivity) // Called first therefore documentID in the Board class is properly set
    }

    // TODO Preparing The Boards Recyclerview Adapter and UI Elements(Step 1: Create a function that will receive data to populate the result of BOARDS list in the UI i.e in the recyclerView.)
    // START
    /**
     * A function to populate the result of BOARDS list in the UI i.e in the recyclerView.
     * onClickListener for the single items in the recycler view are added here
     */
    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {
       hideProgressDialog()
        // If the boardsList is not empty
        if (boardsList.size > 0) {
            binding?.appBarMain?.contentMain?.rvBoardsList?.visibility = View.VISIBLE
            binding?.appBarMain?.contentMain?.tvNoBoardsAvailable?.visibility = View.GONE
            binding?.appBarMain?.contentMain?.rvBoardsList?.layoutManager = LinearLayoutManager(this)
            binding?.appBarMain?.contentMain?.rvBoardsList?.setHasFixedSize(true)

            // Prepare the adapter
            val adapter = BoardItemAdapter(this@MainActivity,boardsList)

            // TODO Making Single Adapter Items Clickable(Step 2: Add click event for boards item and launch the TaskListActivity)
            adapter.setOnClickListener(object: BoardItemAdapter.OnClickListener { // At this point you are using a complete onClick listener, including the data that is required
                override fun onClick(position: Int, model: Board) {
                    // TODO The TaskListActivity (Step 4: Pass the documentId of a board through intent.)
                    // START
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
            binding?.appBarMain?.contentMain?.rvBoardsList?.adapter = adapter
        } else { // Boards list is empty
            binding?.appBarMain?.contentMain?.rvBoardsList?.visibility = View.GONE
            binding?.appBarMain?.contentMain?.tvNoBoardsAvailable?.visibility = View.VISIBLE
        }
    }

    // TODO Adding the Token to the DB (Step 4: Create a function to notify the token is updated successfully in the database.)
    // START
    /**
     * A function to notify the token is updated successfully in the database.
     */
    fun tokenUpdateSuccess() {
        hideProgressDialog()
        // Here we have added a another value in shared preference that enables the token to be automatically updated in the database so we don't need to update it every time.
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog("Please wait...")
        FireStoreClass().signedInUserDetails(this@MainActivity)
    }

    // TODO Adding the Token to the DB (Step 6: Create a function to update the user's FCM token into the database)
    // START
    /**
     * A function to update the user's FCM token into the database.
     */
    private fun updateFCMToken(token: String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        // Update the data in the database.
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().updateUserProfileData(this@MainActivity, userHashMap)
    }



}