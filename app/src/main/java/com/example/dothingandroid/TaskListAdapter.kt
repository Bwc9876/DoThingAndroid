package com.example.dothingandroid


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial


class TaskListAdapter internal constructor(context: Context) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>()  {


    private var mContext: Context? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var tasks = emptyList<Task>()
    var groupName = "NONE"

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupItemView: SwitchMaterial = itemView.findViewById(R.id.task_text)
    }

    fun TaskListAdapter(context: Context?){
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = inflater.inflate(R.layout.recyclertask, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = tasks[position]
        holder.groupItemView.text = current.name
        holder.groupItemView.isChecked = current.done

        holder.groupItemView.setOnClickListener {
            if (mContext is TaskList) {
                (mContext as TaskList).ToggleDone(groupName, current.id.toString(), holder.groupItemView.isChecked)
            }
        }

    }

    internal fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    fun updateGroup(group: String){
        groupName = group
    }

    override fun getItemCount() = tasks.size

}