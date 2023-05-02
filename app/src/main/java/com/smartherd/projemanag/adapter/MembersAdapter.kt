package com.smartherd.projemanag.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartherd.projemanag.R
import com.smartherd.projemanag.databinding.MemberItemBinding
import com.smartherd.projemanag.models.Card
import com.smartherd.projemanag.models.User
import com.smartherd.projemanag.utils.Constants

// TODO Fetching and Displaying the Members of a Board (Step 1: Create an adapter class for Member Items.)
class MembersAdapter(private val context: Context, private var list:ArrayList<User>) : RecyclerView.Adapter<MembersAdapter.MembersViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class MembersViewHolder(itemBinding : MemberItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val ivMemberImage = itemBinding.ivMemberImage
        val tvMemberName = itemBinding.tvMemberName
        val tvMemberEmail = itemBinding.tvMemberEmail
        val ivSelectedMember = itemBinding.ivSelectedMember
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        return MembersViewHolder(MemberItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        val model = list[position]
        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(holder.ivMemberImage)

        holder.tvMemberName.text = model.name
        holder.tvMemberEmail.text = model.email

        if(model.selected) {
            holder.ivSelectedMember.visibility = View.VISIBLE
        } else {
            holder.ivSelectedMember.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            // TODO Preparing and Passing the Card members Dialog (Step 3: Pass the constants here according to the selection.)
            // START
            if (model.selected) { // If initially true then clicked change action to unselected
                onClickListener!!.onClick(position, model, Constants.UN_SELECT) // Initializing the onClickListener. The initialization entity has been created
            } else { // If initially false then clicked change action to selected
                onClickListener!!.onClick(position, model, Constants.SELECT)
            }
            // END
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
        fun onClick(position: Int,user: User, action: String)
    }
}