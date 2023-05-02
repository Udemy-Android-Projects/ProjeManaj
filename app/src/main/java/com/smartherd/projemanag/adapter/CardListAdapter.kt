package com.smartherd.projemanag.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartherd.projemanag.activities.TaskListActivity
import com.smartherd.projemanag.databinding.CardItemBinding
import com.smartherd.projemanag.models.Card
import com.smartherd.projemanag.models.SelectedMembers

// TODO Displaying the Cards (Step 1: Create an adapter class for cards list.)
class CardListAdapter(private val context: Context, private var list: ArrayList<Card>) : RecyclerView.Adapter<CardListAdapter.CardViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class CardViewHolder(itemBinding: CardItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val tvCardName = itemBinding.tvCardName
        val tvMembersName = itemBinding.tvMembersName
        val viewLabelColor = itemBinding.viewLabelColor
        val rvCardSelectedMembersList = itemBinding.rvCardSelectedMembersList

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(CardItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, cardPosition: Int) {
       val model = list[cardPosition]
        holder.tvCardName.text = model.name

        // TODO Displaying the Assigned Users Per Card on ListLevel (Step 2: Fetch the global variable made public to get the selected members per card)
        if ((context as TaskListActivity).mAssignedMembersDetailList.size > 0) { // We have to specify where the variable mAssignedMembersDetailList originates
            // A instance of selected members list.
            val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
            // Here we got the detail list of members and add it to the selected members list as required.
            for (i in context.mAssignedMembersDetailList.indices) {
                for (j in model.assignedTo) {
                    if (context.mAssignedMembersDetailList[i].id == j) {
                        val selectedMember = SelectedMembers(
                            context.mAssignedMembersDetailList[i].id,
                            context.mAssignedMembersDetailList[i].image
                        )
                        selectedMembersList.add(selectedMember)
                    }
                }
            }

            // Check if the selected members list is empty
            if (selectedMembersList.size > 0) {
                // If there is only one member in the list and that member is the one who created the card don't show the recycler view
                if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                    holder.rvCardSelectedMembersList.visibility = View.GONE
                } else {
                    holder.rvCardSelectedMembersList.visibility = View.VISIBLE
                    holder.rvCardSelectedMembersList.layoutManager = GridLayoutManager(context,4)
                    // TODO Displaying the Assigned Users Per Card on ListLevel (Step 5: Set the adapter in the card item with the assigned members to make them visible at the individual card level)
                    val adapter = CardMembersAdapter(
                        context
                        ,selectedMembersList
                        , false /* Set to false since there are external members at this point. This is because the selectedMembersList has more than one person*/
                    )
                    holder.rvCardSelectedMembersList.adapter = adapter
                    adapter.setOnClickListener(
                        object : CardMembersAdapter.OnClickListener{
                            // The execution entity of the CardMembersAdapter is used to run the initialization entity of the CardListAdapter OnClickListener
                            override fun onClick() {
                                if(onClickListener != null)
                                    onClickListener!!.onClick(holder.adapterPosition)
                            }

                        })
                }
            } else {
                holder.rvCardSelectedMembersList.visibility = View.GONE
            }
        }

        // TODO Adding a Detail Screen For Cards (Step 2: Set a click listener to the card item view.)
        // START
        holder.itemView.setOnClickListener{
            if (onClickListener != null) {
                onClickListener!!.onClick(cardPosition) // Position of the card passed here
            }
        }
        // END

        // TODO Adding Colors to Our Cards (Step 3: As we have already have a View Item for label color so make it visible and set the selected label color.)
        if(model.labelColor.isNotEmpty()) {
            holder.viewLabelColor.visibility = View.VISIBLE
            holder.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
        } else {
            holder.viewLabelColor.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int)
    }


}