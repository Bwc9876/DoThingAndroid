package com.example.dothingandroid

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.cachapa.expandablelayout.ExpandableLayout


class GroupListAdapter internal constructor(context: Context) : RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var groups = emptyList<Group>()

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupItemView: TextView = itemView.findViewById(R.id.textView)
        val groupExpandView: ExpandableLayout = itemView.findViewById(R.id.expandable_layout)
        val group: ViewGroup = itemView.findViewById(R.id.expandable_layout) as ViewGroup
        val itemViewer: View = itemView
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerviewtasks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val current = groups[position]
        holder.groupItemView.text = current.Name
        holder.groupItemView.setOnClickListener {
            holder.groupExpandView.toggle()
        }
        val tasks = current.Items.split('/')
        val taskobjs: MutableList<Task> = ArrayList()
        for (task in tasks){
            val tasksplit = task.split(',')
            if (tasksplit[0] != "NONE") {
                taskobjs.add(Task(tasksplit[0].toInt(), tasksplit[1], tasksplit[2].toBoolean()))
            }
        }
        if(taskobjs.isEmpty()) {
            holder.groupItemView.text = current.Name + " (Empty)"
        }
        val taskAdapter = TaskListAdapter(holder.itemViewer.context)
        taskAdapter.setTasks(taskobjs)
        holder.recyclerView.adapter = taskAdapter
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemViewer.context)
    }

    internal fun setGroups(groups: List<Group>) {
        this.groups = groups
        notifyDataSetChanged()

    }

    override fun getItemCount() = groups.size

}