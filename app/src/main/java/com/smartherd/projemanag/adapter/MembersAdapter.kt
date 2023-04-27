package com.smartherd.projemanag.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartherd.projemanag.R
import com.smartherd.projemanag.databinding.MemberItemBinding
import com.smartherd.projemanag.models.Card
import com.smartherd.projemanag.models.User

// TODO Fetching and Displaying the Members of a Board (Step 1: Create an adapter class for Member Items.)
class MembersAdapter(private val context: Context, private var list:ArrayList<User>) : RecyclerView.Adapter<MembersAdapter.MembersViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class MembersViewHolder(itemBinding : MemberItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val ivMemberImage = itemBinding.ivMemberImage
        val tvMemberName = itemBinding.tvMemberName
        val tvMemberEmail = itemBinding.tvMemberEmail
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
        fun onClick(position: Int, card: Card)
    }
}