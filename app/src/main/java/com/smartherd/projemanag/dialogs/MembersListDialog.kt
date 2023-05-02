package com.smartherd.projemanag.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartherd.projemanag.R
import com.smartherd.projemanag.adapter.MembersAdapter
import com.smartherd.projemanag.databinding.DialogListBinding
import com.smartherd.projemanag.models.User

// TODO Preparing and Passing the Card members Dialog (Step 5: Create a members list dialog class to show the list of members in a dialog.)
abstract class MembersListDialog (
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = "") : Dialog(context) {

    lateinit var binding : DialogListBinding
    private var adapter: MembersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val rvList = view.findViewById<RecyclerView>(R.id.rvList)
        if (list.size > 0) {
            tvTitle.text = title
            rvList.layoutManager = LinearLayoutManager(context)
            adapter = MembersAdapter(context, list)
            rvList.adapter = adapter
            adapter!!.setOnClickListener(object :
                MembersAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action:String) {
                    dismiss()
                    onItemSelected(user, action) // Initialization entity of the function onItemSelected
                }
            })

        }
    }

    protected abstract fun onItemSelected(user: User, action:String)

}