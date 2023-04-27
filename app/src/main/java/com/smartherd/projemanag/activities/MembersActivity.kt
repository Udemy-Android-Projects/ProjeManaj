package com.smartherd.projemanag.activities

import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartherd.projemanag.R
import com.smartherd.projemanag.adapter.MembersAdapter
import com.smartherd.projemanag.databinding.ActivityMembersBinding
import com.smartherd.projemanag.databinding.DialogSearchMemberBinding
import com.smartherd.projemanag.firebase.FireStoreClass
import com.smartherd.projemanag.models.Board
import com.smartherd.projemanag.models.User
import com.smartherd.projemanag.utils.Constants

class MembersActivity : BaseActivity() {
    lateinit var binding: ActivityMembersBinding
    // TODO Creating the Member Item and Toolbar (Step 3: Create a global variable for Board Details.)
    // START
    // A global variable for Board Details.
    private lateinit var mBoardDetails: Board
    // END
    // TODO Adding a New Member to a Board (Step 1: A global variable for Users List.)
    // START
    // A global variable for Assigned Members List.
    private lateinit var mAssignedMembersList:ArrayList<User>
    // END

    // TODO Reloading Board Details On Change (Step 3: Declare a global variable for notifying any changes done or not.)
    // START
    // A global variable for notifying any changes done or not in the assigned members list.
    private var anyChangesDone: Boolean = false
    // END

    // Dialog binding
    lateinit var dialogBinding: DialogSearchMemberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
        }

        // TODO Creating the Member Item and Toolbar (Step 4: Get the Board Details through intent and assign it to the global variable.)
        // START
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!! // We are retrieving the entire board object
        }
        // END
        // TODO Fetching and Displaying the Members of a Board (Step 5: Get the members list details from the database.)
        // START
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.fetching_members))
        FireStoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )
        // END
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarMembersActivity)
        val actionBar = supportActionBar
        val typeFace : Typeface = Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding.toolbarTitle.text = resources.getText(R.string.members)
        binding.toolbarTitle.typeface = typeFace
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = null
        }
        binding.toolbarMembersActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // TODO Fetching and Displaying the Members of a Board (Step 2: Create a function to setup assigned members list into recyclerview.)
    // START
    /**
     * A function to setup assigned members list into recyclerview.
     */
    fun setupMembersList(list: ArrayList<User>) {
        // TODO Adding a New Member to a Board (Step 2: Initialize the Assigned Members List.)
        // START
        mAssignedMembersList = list
        // END
        hideProgressDialog()
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MembersAdapter(this,list)
        binding.rvMembersList.adapter = adapter
    }

    // TODO Adding a New Member to a Board  (Step 3: Here we will get the result of the member if it found in the database.)
    // START
    fun memberDetails(user: User) {
        // TODO Adding a New Member to a Board  (Step 6: Here add the user id to the existing assigned members list of the board.)
        // START
        mBoardDetails.assignedTo.add(user.id)
        // TODO Adding a New Member to a Board (Step 9: Finally assign the member to the board in the database.)
        // START
        FireStoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
        // END
    }

    // TODO Adding New Members - UI (Step 4: Initialize the dialog for searching member from Database.)
    // START
    /**
     * Method is used to show the Custom Dialog.
     */
    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        // Inflate dialog
        dialogBinding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvAdd.setOnClickListener {
            val email = dialogBinding.etEmailSearchMember.text.toString()
            if(email.isNotEmpty()) {
                // TODO Adding a New Member to a Board (Step 5: Get the member details from the database.)
                // START
                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this@MembersActivity, email)
                // END
                dialog.dismiss()
            } else {
                Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        dialogBinding.tvCancel.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    // TODO Adding New Members - UI (Step 3: Inflate the menu file for adding the member and also add the onOptionItemSelected function.)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member -> {
                // Display dialog
                // TODO Adding New Members - UI (Step 5: Call the dialogSearchMember function here.)
                // START
                dialogSearchMember()
                // END
                return true /** Don't forget this */
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // TODO Adding a New Member to a Board (Step 7: Initialize the dialog for searching member from Database.)
    // START
    /**
     * A function to get the result of assigning the members.
     */
    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        // TODO Reloading Board Details On Change (Step 4: Here the list is updated so change the global variable which we have declared for notifying changes.)
        // START
        anyChangesDone = true
        // END
        setupMembersList(mAssignedMembersList)
    }

    // TODO Reloading Board Details On Change (Step 5: Send the result to the base activity onBackPressed.)
    override fun onBackPressed() {
        if (anyChangesDone) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

}