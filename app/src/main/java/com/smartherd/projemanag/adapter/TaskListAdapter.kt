package com.smartherd.projemanag.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartherd.projemanag.R
import com.smartherd.projemanag.activities.TaskListActivity
import com.smartherd.projemanag.databinding.TaskItemBinding
import com.smartherd.projemanag.models.Task

// TODO The TaskList Adapter (Step 1: Create an adapter class for Task List Items in the TaskListActivity.)
class TaskListAdapter(private val context: Context, private var list: ArrayList<Task>) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>()
{

    inner class TaskViewHolder(itemBinding: TaskItemBinding) : RecyclerView.ViewHolder(itemBinding.root){
        val tvAddTaskList = itemBinding.tvAddTaskList
        val llTaskItem = itemBinding.llTaskItem
        val tvTaskListTitle = itemBinding.tvTaskListTitle
        val cvAddTaskListName = itemBinding.cvAddTaskListName
        val ibDoneListName = itemBinding.ibDoneListName
        val ibCloseListName = itemBinding.ibCloseListName
        val etTaskListName = itemBinding.etTaskListName
        val ibEditListName = itemBinding.ibEditListName
        val ibDeleteList = itemBinding.ibDeleteList
        val etEditTaskListName = itemBinding.etEditTaskListName
        val cvEditTaskListName = itemBinding.cvEditTaskListName
        val llTitleView = itemBinding.llTitleView
        val ibCloseEditableView = itemBinding.ibCloseEditableView
        val ibDoneEditListName = itemBinding.ibDoneEditListName
        val tvAddCard = itemBinding.tvAddCard
        val cvAddCard = itemBinding.cvAddCard
        val ibCloseCardName = itemBinding.ibCloseCardName
        val ibDoneCardName = itemBinding.ibDoneCardName
        val etCardName = itemBinding.etCardName
        val rvCardList = itemBinding.rvCardList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // TODO The TaskList Adapter (Step 2: Here we have done some additional changes to display the item of the task list item in 70% of the screen size.)
        val view = TaskItemBinding.inflate(LayoutInflater.from(context),parent,false)
        // Here the layout params are converted dynamically according to the screen size as width is 70% and height is wrap_content.
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), /* Width of the view */
            LinearLayout.LayoutParams.WRAP_CONTENT  /* Height of the view */
        )
        // Here the dynamic margins are applied to the view.
        layoutParams.setMargins((15.toDp()).toPx()/* Margin left*/, 0, (40.toDp()).toPx()/* Margin right*/, 0)
        // Set the layout parameters defined are the ones to be used to our view
        view.root.layoutParams = layoutParams
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // For onBindViewHolder to work the list passes in the adapter must have data...that is why when the adapter is first used a dummy task is added
        val model = list[position]
        Log.e("ListsizeTLAdapter","${list.size}")
        if (position == list.size - 1) { // Only the dummy task exists in the list passed
            // If (position == 0) only the dummy task is shown...if you add A then B....B will be shown in place of A...add C then B will be shown in place of C
            holder.tvAddTaskList.visibility = View.VISIBLE // Displayed only if there are no items/ tasks
            holder.llTaskItem.visibility = View.GONE
        } else {
            holder.tvAddTaskList.visibility = View.GONE
            holder.llTaskItem.visibility = View.VISIBLE
        }

        // TODO Create Lists Inside a Board (Step 4: Add a click event for showing the view for adding the task list name. And also set the task list title name.)
        // START
        holder.tvTaskListTitle.text = model.title
        holder.tvAddTaskList.setOnClickListener {
            holder.tvAddTaskList.visibility = View.GONE
            holder.cvAddTaskListName.visibility = View.VISIBLE
        }
        // TODO Create Lists Inside a Board (Step 5: Add a click event for hiding the view for closing the task list name.)
        // START
        holder.ibCloseListName.setOnClickListener {
            holder.tvAddTaskList.visibility = View.VISIBLE
            holder.cvAddTaskListName.visibility = View.GONE
        }
        // TODO Create Lists Inside a Board (Step 10: Add a click event to create a task list.)
        holder.ibDoneListName.setOnClickListener {
            val listName = holder.etTaskListName.text.toString()
            if(context is TaskListActivity) {
                context.createTaskList(listName)
            } else {
                Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
            }
        }
        // END

        // TODO  Editing And Deleting Lists (Step 1: Add a click event for iv_edit_list for showing the editable view.)
        // START
        holder.ibEditListName.setOnClickListener {
            holder.etEditTaskListName.setText(model.title)
            holder.llTitleView.visibility = View.GONE
            holder.cvEditTaskListName.visibility = View.VISIBLE
        }

        // TODO Editing And Deleting Lists (Step 2: Add a click event for iv_close_editable_view for hiding the editable view.)
        holder.ibCloseEditableView.setOnClickListener {
            holder.llTitleView.visibility = View.VISIBLE
            holder.cvEditTaskListName.visibility = View.GONE
        }

        holder.ibDoneEditListName.setOnClickListener {
            // TODO Editing And Deleting Lists (Step 4:Implement the editing functionality in the ibDoneEditListName onClickListener.)
            val listName = holder.etEditTaskListName.text.toString()
            if (listName.isNotEmpty()) {
                if (context is TaskListActivity) {
                    context.updateTaskList(position, listName, model)
                }
            } else {
                Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
            }
        }

        holder.ibDeleteList.setOnClickListener {
            if(context is TaskListActivity) {
                // TODO Editing And Deleting Lists (Step 7: Add a click event for ib_delete_list for deleting the task list.)
                alertDialogForDeleteList(position,model.title)
            }
        }

        // TODO Adding Cards to Lists (Step 3: Add a click event for adding a card in the task list.)
        holder.tvAddCard.setOnClickListener {
            holder.tvAddCard.visibility = View.GONE
            holder.cvAddCard.visibility = View.VISIBLE
        }
        // TODO Adding Cards to Lists (Step 4: Add a click event for closing the view for card add in the task list.)
        holder.ibCloseCardName.setOnClickListener {
            holder.tvAddCard.visibility = View.VISIBLE
            holder.cvAddCard.visibility = View.GONE
        }
        // TODO Adding Cards to Lists (Step 5: Add a click event for adding a card in the task list.)
        holder.ibDoneCardName.setOnClickListener {
            val cardName = holder.etCardName.text.toString()
            if(cardName.isNotEmpty()) {
                // TODO Adding Cards to Lists (Step 7: Call the addCard function at this point.)
                if(context is TaskListActivity) {
                    context.addCardToTaskList(position,cardName)
                }
            } else {
                Toast.makeText(context, "Please Enter Card Name.", Toast.LENGTH_SHORT).show()
            }
        }

        // TODO Displaying the Cards (Step 2: Load the cards list in the recyclerView.)
        holder.rvCardList.layoutManager = LinearLayoutManager(context)
        holder.rvCardList.setHasFixedSize(true)
        val adapter = CardListAdapter(context,model.cards)
        holder.rvCardList.adapter = adapter
        // TODO (Step 3: Add a click event on card items for card details.)
        adapter.setOnClickListener(
            object : CardListAdapter.OnClickListener{
                override fun onClick(cardPosition: Int) {
                    if (context is TaskListActivity) {
                        context.cardDetails(holder.adapterPosition /* TaskListPosition */,cardPosition /* CardPosition */)
                    }
                }
            }
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function to get density pixel from pixel
     * Enables one to get the density of the screen to enable one to properly adjust the width,or any dimension for that matter, of the adapter
     */
    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A function to get pixel from density pixel
     */
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    // TODO Editing And Deleting Lists (Step 6: Create a function to show an alert dialog for deleting the task list.)
    // START
    /**
     * Method is used to show the Alert Dialog for deleting the task list.
     */
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(R.drawable.ic_baseline_warning_24)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
    // END
}