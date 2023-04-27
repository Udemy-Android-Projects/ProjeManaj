package com.smartherd.projemanag.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartherd.projemanag.databinding.DialogListBinding
import com.smartherd.projemanag.databinding.ItemLabelColorBinding
import com.smartherd.projemanag.models.Card

// TODO Preparing the Cards Color Dialog and Adapter (Step 3: Create an adapter class for selection of card label color using the "item_label_color".)
class ColorListAdapter(private val context: Context,private var list: ArrayList<String>,private var mSelectedColor: String) : RecyclerView.Adapter<ColorListAdapter.ColorListViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class ColorListViewHolder(itemBinding : ItemLabelColorBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val ivSelectedColor = itemBinding.ivSelectedColor
        val viewMain = itemBinding.viewMain
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorListViewHolder {
        return ColorListViewHolder(ItemLabelColorBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: ColorListViewHolder, position: Int) {
        val item = list[position]
        holder.viewMain.setBackgroundColor(Color.parseColor(item))
        if(item == mSelectedColor) {
            holder.ivSelectedColor.visibility = View.VISIBLE
        } else {
            holder.ivSelectedColor.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item)
            }
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
        fun onClick(position: Int, color: String)
    }
}