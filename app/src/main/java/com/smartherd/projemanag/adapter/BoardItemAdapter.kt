package com.smartherd.projemanag.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartherd.projemanag.R
import com.smartherd.projemanag.databinding.BoardItemBinding
import com.smartherd.projemanag.models.Board

// TODO Preparing RecycleView Elements such as adapters and item view layouts (Step 3: Create an adapter class for Board Items in the MainActivity.
//  Context is the class where the adapter will be used)
open class BoardItemAdapter(private val context: Context, private var list: ArrayList<Board>) : RecyclerView.Adapter<BoardItemAdapter.BoardsViewHolder>(){

    // TODO Preparing RecycleView Elements such as adapters and item view layouts (Step 5: Create an object of the onClickListener interface)
    private var onClickListener: OnClickListener? = null

    inner class BoardsViewHolder(itemBinding: BoardItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val ivBoardImage = itemBinding.ivBoardImage
        val tvName = itemBinding.tvName
        val tvCreatedBy = itemBinding.tvCreatedBy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardsViewHolder {
        return BoardsViewHolder(BoardItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: BoardsViewHolder, position: Int) {
        val model = list[position]
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.ivBoardImage)
        holder.tvName.text = model.name
        holder.tvCreatedBy.text = "Created by : ${model.createdBy}"

        holder.itemView.setOnClickListener { // All the data that is passed or needed during the onClick event is instantiated here
            // User the onClickListener object
            if (onClickListener != null) {
                onClickListener!!.onClick(position, model) // Model of the board is passed at this point
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // TODO Making Single Adapter Items Clickable(Step 1: Creating a function for OnClickListener where the Interface is the expected parameter)
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // TODO Preparing RecycleView Elements such as adapters and item view layouts (Step 4: Create an interface that will be used as an onClickListener for each board item)
    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

}