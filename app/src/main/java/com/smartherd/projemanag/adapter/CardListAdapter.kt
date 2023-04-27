package com.smartherd.projemanag.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartherd.projemanag.databinding.CardItemBinding
import com.smartherd.projemanag.models.Card

// TODO Displaying the Cards (Step 1: Create an adapter class for cards list.)
class CardListAdapter(private val context: Context, private var list: ArrayList<Card>) : RecyclerView.Adapter<CardListAdapter.CardViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class CardViewHolder(itemBinding: CardItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val tvCardName = itemBinding.tvCardName
        val tvMembersName = itemBinding.tvMembersName
        val viewLabelColor = itemBinding.viewLabelColor

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(CardItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, cardPosition: Int) {
       val model = list[cardPosition]
        holder.tvCardName.text = model.name
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