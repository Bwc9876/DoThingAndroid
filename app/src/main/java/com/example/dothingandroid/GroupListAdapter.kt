package com.example.dothingandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupListAdapter internal constructor(context: Context) : RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var groups = emptyList<Group>()

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val current = groups[position]
        holder.groupItemView.text = current.Name
    }

    internal fun setGroups(groups: List<Group>) {
        this.groups = groups
        notifyDataSetChanged()

    }

    override fun getItemCount() = groups.size

}