package com.smartherd.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartherd.projemanag.R
import com.smartherd.projemanag.adapter.TaskListAdapter
import com.smartherd.projemanag.databinding.ActivityTaskListBinding
import com.smartherd.projemanag.firebase.FireStoreClass
import com.smartherd.projemanag.models.Board
import com.smartherd.projemanag.models.Card
import com.smartherd.projemanag.models.Task
import com.smartherd.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {
    lateinit var binding : ActivityTaskListBinding
    // TODO Create Lists Inside a Board (Step 2: Create a global variable for Board Details.)
    // START
    // A global variable for Board Details.
    private lateinit var mBoardDetails: Board
    // END
    private lateinit var mBoardDocumentId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = resources.getColor(R.color.colorPrimary);
        }

        // TODO The TaskListActivity (Step 5: Get the board documentId through intent.)
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        // TODO The TaskListActivity (Step 7: Call the function to get the Board Details.)
        // START
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this@TaskListActivity, mBoardDocumentId)
        // END
    }

    // TODO The TaskListActivity (Step 9: Create a function to setup action bar.)
    // START
    /**
     * A function to setup action bar
     */
    private fun setupActionBar(title: String) {

        setSupportActionBar(binding.toolbarTaskListActivity)
        //Set typeface
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding.toolbarTitle.typeface = typeface
        binding.toolbarTitle.text = title

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = null
        }

        binding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }
    // END

    // TODO The TaskListActivity (Step 8: Create a function to get the result of Board Detail.)
    // START
    /**
     * A function to get the result of Board Detail.
     */
    fun boardDetails(board: Board) {
        // TODO Create Lists Inside a Board (Step 3: Initialize and Assign the value to the global variable for Board Details.
        //  After replace the parameter variable with global so from onwards the global variable will be used.)
        // START
        mBoardDetails = board
        // END
        hideProgressDialog()
        // TODO The TaskListActivity (Step 10: call the setup actionbar function.)
        // START
        // Call the function to setup action bar.
        setupActionBar(mBoardDetails.name)
        // END
        // TODO The TaskList Adapter (Step 3: Setup the task list view using the adapter class and task list of the board.)
        // Here we are appending an item view for adding a list task list for the board.
        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)
        binding.rvTaskList.layoutManager = LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvTaskList.setHasFixedSize(true)
        // Create an instance of TaskListItemsAdapter and pass the task list to it.
        val adapter = TaskListAdapter(this@TaskListActivity, mBoardDetails.taskList)
        binding.rvTaskList.adapter = adapter
    }

    // TODO Create Lists Inside a Board (Step 6: Create a function to get the result of add or updating the task list.)
    // START
    /**
     * A function to get the result of add or updating the task list.
     */
    fun addTaskListSuccess() {
        // Here get the updated board details.
        FireStoreClass().getBoardDetails(this@TaskListActivity,mBoardDetails.documentId)
    }

    // TODO Create Lists Inside a Board (Step 9: Create a function to get the task list name from the adapter class which we will be using to create a new task list in the database.)
    // START
    /**
     * A function to get the task list name from the adapter class which we will be using to create a new task list in the database.
     */
    fun createTaskList(taskListName: String) {
        // Create and Assign the task details
        val task = Task(taskListName, FireStoreClass().getCurrentUserID())
        mBoardDetails.taskList.add(0, task) // Add task to the first position of ArrayList
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1) // Remove the last position as we have added the item manually for adding the TaskList. Manual addition necessary since without data no view is shown
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addTaskList(this@TaskListActivity, mBoardDetails)
    }

    // TODO Editing And Deleting Lists (Step 3: Create a function to update the taskList.)
    // START
    /**
     * A function to update the taskList
     */
    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy)
        mBoardDetails.taskList[position] = task // Remember before this the taskList was blank
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1) // Note/review usage..commented since the default task has also been commented
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addTaskList(this@TaskListActivity, mBoardDetails)
    }

    // TODO Editing And Deleting Lists (Step 5: Create a function to delete the task list.)
    // START
    /**
     * A function to delete the task list from database.
     */
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        Log.e("DeleteTaskList", "${mBoardDetails.taskList.size}")
        if(mBoardDetails.taskList.size == 1 || mBoardDetails.taskList.size == 0){
            mBoardDetails.taskList.removeAt(0)
        } else {
            mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        }
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addTaskList(this@TaskListActivity, mBoardDetails)
    }

    // TODO Adding Cards to Lists (Step 6: A function to create a card and update it in the task list.)
    /**
     * A function to create a card and update it in the task list.
     */
    fun addCardToTaskList(position: Int, cardName: String) {
        // Remove the last item....will investigate why
        // Here we are setting the assignedTo property of a card
        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FireStoreClass().getCurrentUserID())
        // Create a card
        val card = Card(cardName, FireStoreClass().getCurrentUserID(), cardAssignedUsersList)
        // Add the created card to the Task cardList
        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)
        // This newly created task with a card will replace the existing one that didn't have a card
        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )
        // mBoardDetails.taskList.add(position,task)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1) // Remove the extra list created when adding cards
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        // Add the card by updating the board
        FireStoreClass().addTaskList(this@TaskListActivity, mBoardDetails)
    }

    // TODO Preparing the Members Activity step 1
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // TODO Preparing the Members Activity step 1
    // Enables the items in the menu to respond to user clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_members -> {
                // TODO Creating the Member Item and Toolbar (Step 2: Pass the board details through intent.)
                // START
                val intent = Intent(this@TaskListActivity, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                // TODO Reloading Board Details On Change (Step 2: Start activity for result.)
                // START
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // TODO Reloading Board Details On Change (Step 7: Add the onActivityResult function add based on the requested document get the updated board details.)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // TODO Adding The Delete Card Menu and Populating the Edittext (Step 6: Get the success result from Card Details Activity.)
        // START
        if (resultCode == Activity.RESULT_OK && (requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE))
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardDetails(this@TaskListActivity, mBoardDocumentId)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    // TODO Adding a Detail Screen For Cards(Step 1: Create a function for viewing and updating card details.)
    // START
    /**
     * A function for viewing and updating card details.
     */
    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        // TODO Loading Card Details to Set Card Title (Step 2: Send all the required details to CardDetailsActivity through intent.)
        val intent = Intent(this@TaskListActivity, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        // TODO Adding The Delete Card Menu and Populating the Edittext (Step 5: Update the intent using the start activity for result.)
        // START
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
        // END
    }

    // TODO Reloading Board Details On Change (Step 1: Create a companion object and declare a constant for starting an MembersActivity for result.)
    // START
    /**
     * A companion object to declare the constants.
     */
    companion object {
        //A unique code for starting the activity for result
        const val MEMBERS_REQUEST_CODE: Int = 13
        // TODO Adding The Delete Card Menu and Populating the Edittext (Step 4: Add a unique request code for starting the activity for result.)
        // START
        const val CARD_DETAILS_REQUEST_CODE: Int = 14
        // END
    }
    // END


}