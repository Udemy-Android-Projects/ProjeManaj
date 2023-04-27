package com.smartherd.projemanag.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartherd.projemanag.R
import com.smartherd.projemanag.adapter.ColorListAdapter
import com.smartherd.projemanag.databinding.DialogListBinding
import com.smartherd.projemanag.databinding.DialogProgressBinding

// TODO Preparing the Cards Color Dialog and Adapter (Step 4: Create an dialogs package and a class for showing the label color list dialog.)
abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor: String = ""
) : Dialog(context) {
    lateinit var binding : DialogListBinding
    private var adapter: ColorListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view : View) {
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val rvList = view.findViewById<RecyclerView>(R.id.rvList)
        tvTitle.text = title
        rvList.layoutManager = LinearLayoutManager(context)
        adapter = ColorListAdapter(context,list,mSelectedColor)
        rvList.adapter = adapter

        adapter!!.setOnClickListener(
            object : ColorListAdapter.OnClickListener{
                override fun onClick(position: Int, color: String) {
                    // The color selected is set and passed to the onItemSelected
                    // Once passed whenever the onItemSelected is called the variable will be present
                    dismiss()
                    onItemSelected(color)
                }

            }
        )

    }

    protected abstract fun onItemSelected(color: String)
}