package com.smartherd.projemanag.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.smartherd.projemanag.R
import com.smartherd.projemanag.adapter.CardMembersAdapter
import com.smartherd.projemanag.databinding.ActivityCardDetailsBinding
import com.smartherd.projemanag.dialogs.LabelColorListDialog
import com.smartherd.projemanag.dialogs.MembersListDialog
import com.smartherd.projemanag.firebase.FireStoreClass
import com.smartherd.projemanag.models.*
import com.smartherd.projemanag.utils.Constants

class CardDetailsActivity : BaseActivity() {
    lateinit var binding : ActivityCardDetailsBinding
    // TODO Loading Card Details to Set Card Title (Step 3: Create all the global variables to get all the data that is sent through intent and use them further as per requirement.)
    // START
    // A global variable for board details
    private lateinit var mBoardDetails: Board
    // A global variable for task item position
    private var mTaskListPosition: Int = -1 // -1 means not present
    // A global variable for card item position
    private var mCardPosition: Int = -1
    // END
    // TODO Setting the Color and Updating the Card (Step 3: Add a global variable for selected label color)
    // START
    // A global variable for selected label color
    private var mSelectedColor: String = ""
    // END
    // TODO Passing The Memberlist to The Card (Step 7: Add a global variable for Assigned Members Detail List.)
    // START
    // A global variable for Assigned Members List.
    private lateinit var mMembersDetailList: ArrayList<User>
    // END
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
            // TODO Loading Card Details to Set Card Title (Step 6: Call the getIntentData function here.)
            // START
            getIntentData()
            // END
            // TODO Adding The Delete Card Menu and Populating the Edittext (Step 3: Set the card name in the EditText for editing.)
            binding.etNameCardDetails.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
            // Set the focus of the cursor to the end of the line
            binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)
        }
        // TODO Deleting And Updating Cards  (Step 4: Add a click event for update button and also call the function to update the card details.)
        binding.btnUpdateCardDetails.setOnClickListener {
            if(binding.etNameCardDetails.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(this@CardDetailsActivity, "Enter card name.", Toast.LENGTH_SHORT).show()
            }
        }

        // If clicked mSelected color will be set
        binding.tvSelectLabelColor.setOnClickListener {
            labelColorsListDialog()
        }

        // TODO Adding Colors to Our Cards (Step 1: Get the already selected label color and set it to the TextView background.)
        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }

        // TODO Finish the Add Members Feature (Step 1: Call the method to set up the recycler view in the card)
        setupSelectedMembersList()

        // TODO Preparing and Passing the Card members Dialog (Step 7: Add the click event to launch the members list dialog.)
        binding.tvSelectMembers.setOnClickListener {
            membersListDialog()
        }
    }

    private fun setUpActionBar(title: String) {
        setSupportActionBar(binding.toolbarCardDetailsActivity)
        //Set typeface
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding.toolbarTitle.text = title
        binding.toolbarTitle.typeface = typeface

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = null
        }
        binding.toolbarCardDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // TODO Loading Card Details to Set Card Title (Step 4: Create a function to get all the data that is sent through intent.)
    // START
    // A function to get all the data that is sent through intent.
    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!

        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1) // If nothing is present the default value is used
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        // TODO Passing The Memberlist to The Card (Step 8: Get the members detail list here through intent.)
        // START
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
        // END
        // TODO Loading Card Details to Set Card Title (Step 5: Set the title of action bar.)
        setUpActionBar(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
    }

    // TODO Adding The Delete Card Menu and Populating the Edittext (Step 2: Inflate the menu file here.)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }
    // TODO Deleting And Updating Cards (Step 9: Call the function for showing an alert dialog for deleting the card.)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // TODO Deleting And Updating Cards (Step 2: Create A function to get the result of add or updating the task list.)
    fun addTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK) // Enables the refresh functionality once card details have been updated
        finish() // Go back to previous activity
    }
    // TODO Deleting And Updating Cards (Step 3: Create a function to update card details.)
    // START
    /**
     * A function to update card details.
     */
    private fun updateCardDetails() {
        val card = Card(binding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            // TODO Setting the Color and Updating the Card (Step 6: Pass the selected label color of the card in the data model class.)
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
            )

        // Override in order to pass the updates
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        // As long as the addTaskList method is used one must delete at position [list.size - 1] to prevent the dummy task from showing up
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog("Updating card details...")
        FireStoreClass().addTaskList(this@CardDetailsActivity,mBoardDetails) // Updates everything
    }

    // TODO Deleting And Updating Cards (Step 6: Create a function to show an alert dialog for the confirmation to delete the card.)
    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.confirmation_message_to_delete_card, cardName /* represented by \'%1$s\'? in the strings.xml */))
        builder.setIcon(R.drawable.ic_baseline_warning_24)
        builder.setPositiveButton("Yes"){ dialogInterface,_ ->
            dialogInterface.dismiss()
            // TODO Deleting And Updating Cards (Step 7: Call the function to delete the card.)
            // START
            deleteCard()
            // END
        }
        builder.setNegativeButton("No"){ dialogInterface,_ ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI

    }

    // TODO Deleting And Updating Cards (Step 5: Create a function to delete the card from the task list.)
    // START
    /**
     * A function to delete the card from the task list.
     */
    private fun deleteCard() {
        // Here we have got the cards list from the task item list using the task list position.
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        // Here we will remove the item from cards list using the card position.
        cardsList.removeAt(mCardPosition)
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)
        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog("Deleting card...")
        FireStoreClass().addTaskList(this@CardDetailsActivity,mBoardDetails)
        /** In fireStore updates/changes can't be made to individual fields...the entire document must be updated */
    }

    // TODO Setting the Color and Updating the Card (Step 1: Create a function to add some static label colors in the list.)
    /**
     * A function to add some static label colors in the list.
     */
    private fun colorsList(): ArrayList<String> {
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    // TODO Setting the Color and Updating the Card  (Step 2: Create a function to remove the text and set the label color to the TextView.)
    /**
     * A function to remove the text and set the label color to the TextView.
     */
    private fun setColor() {
        binding.tvSelectLabelColor.text = ""
        binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor = mSelectedColor
    }

    // TODO Setting the Color and Updating the Card  (Step 4: Create a function to launch the label color list dialog.)
    /**
     * A function to launch the label color list dialog.
     */
    private fun labelColorsListDialog() {
        val colorsList: ArrayList<String> = colorsList()
        val listDialog = object : LabelColorListDialog(
            this,
            colorsList,
            "Select label color",
            mSelectedColor  // TODO Adding Colors to Our Cards (Step 2: Pass the selected color to show it as already selected with tick icon in the list.)
        )
        {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor() // The global variable has been set and therefore the code will run as normal
            }
        }
        listDialog.show() /** Always remember this */
    }

    // TODO Preparing and Passing the Card members Dialog (Step 6: Create a function to launch the Members list dialog.)
    // START
    /**
     * A function to launch and setup assigned members detail list into recyclerview.
     */
    private fun membersListDialog() {
        // Here we get the updated assigned members list
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        if (cardAssignedMembersList.size > 0) {
            // Here we got the details of assigned members list from the global members list which is passed from the Task List screen.
            for (i in mMembersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if (mMembersDetailList[i].id == j) {
                        // Engage the tick to all members whose ID match the ID of the card
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }
        val listDialog = object : MembersListDialog(
            this@CardDetailsActivity,
            mMembersDetailList,
            "Select Member"
        ) {
            override fun onItemSelected(user: User, action: String) {
                // TODO Finish the Add Members Feature (Step 2: Use the action done by the user to alter the assignedTo field and the select property of SelectedMembers model accordingly)
                if(action == Constants.SELECT) {
                    // If the selected user is not assigned the card...do so
                    if(!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)) {
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)
                    }
                } else { // If the action is Constants.UnSelect remove the user and set the select property to false to remove the tick
                   mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)
                   // G through all the members,select the one we removed and set their select property to false
                    for(i in mMembersDetailList.indices) {
                        if(mMembersDetailList[i].id == user.id)
                            mMembersDetailList[i].selected = false
                    }
                }
                // Refresh the recyclerview
                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }

    // TODO Prepare the Add Members Feature (Step 3: Create a function to setup the recyclerView for card assigned members.)
// START
    /**
     * A function to setup the recyclerView for card assigned members.
     */
    private fun setupSelectedMembersList() {
        // Assigned members of the Card.
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        // A instance of selected members list.
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
        // Here we got the detail list of members and add it to the selected members list as required.
        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }
        if (selectedMembersList.size > 0) {
            // This is for the last item to show. The add icon
            selectedMembersList.add(SelectedMembers("", ""))
            binding.tvSelectMembers.visibility = View.GONE
            binding.rvSelectedMembersList.visibility = View.VISIBLE
            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this@CardDetailsActivity,6)
            val adapter = CardMembersAdapter(this@CardDetailsActivity,selectedMembersList)
            binding.rvSelectedMembersList.adapter = adapter
            adapter.setOnClickListener(
                object : CardMembersAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }

                }
            )
        } else {
            binding.tvSelectMembers.visibility = View.VISIBLE
            binding.rvSelectedMembersList.visibility = View.GONE
        }
    }
}