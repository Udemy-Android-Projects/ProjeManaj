package com.smartherd.projemanag.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartherd.projemanag.R
import com.smartherd.projemanag.databinding.SelectedMemberCardItemBinding
import com.smartherd.projemanag.models.SelectedMembers
import com.smartherd.projemanag.models.User

// TODO Prepare the Add Members Feature (Step 2: Create a adapter class for selected members list.)
open class CardMembersAdapter(
    private val context: Context,
    private var list: ArrayList<SelectedMembers>
) : RecyclerView.Adapter<CardMembersAdapter.CardMembersViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class CardMembersViewHolder(itemBinding: SelectedMemberCardItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val ivSelectedMemberImage = itemBinding.ivSelectedMemberImage
        val ivAddMember = itemBinding.ivAddMember
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardMembersViewHolder {
        return CardMembersViewHolder(SelectedMemberCardItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: CardMembersViewHolder, position: Int) {
        val model = list[position]
        if (position == list.size - 1) { // We have a dummy item therefore this equality represents an empty list
            holder.ivAddMember.visibility = View.VISIBLE
            holder.ivSelectedMemberImage.visibility = View.GONE
        } else {
            holder.ivAddMember.visibility = View.GONE
            holder.ivSelectedMemberImage.visibility = View.VISIBLE
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(holder.ivSelectedMemberImage)
        }

        // Initializing entity for this adapter's onClickListener
        holder.itemView.setOnClickListener {
            if(onClickListener != null)
                onClickListener!!.onClick()
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
    // TODO  Preparing and Passing the Card members Dialog (Step 2: Update the parameters of onclick function.)
    interface OnClickListener { // Prepare the onClickListener
        fun onClick()
    }
}