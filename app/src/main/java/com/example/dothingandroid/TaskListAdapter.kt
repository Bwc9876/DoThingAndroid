package com.example.dothingandroid


import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial


class TaskListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {


    private var mContext: Context? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var tasks = emptyList<Task>()
    var groupName = "NONE"

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupItemView: SwitchMaterial = itemView.findViewById(R.id.task_text)
        val taskBorderTop: View = itemView.findViewById(R.id.task_border_top)
        val taskBorderBottom: View = itemView.findViewById(R.id.task_seperator_bottom)
        val taskEditButton: View = itemView.findViewById(R.id.edit_task_button)
    }

    fun TaskListAdapter(context: Context?) {
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = inflater.inflate(R.layout.recyclertask, parent, false)
        return TaskViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = tasks[position]
        holder.groupItemView.text = current.name
        holder.groupItemView.isChecked = current.done
        if (position == tasks.size - 1) {
            holder.taskBorderBottom.visibility = View.VISIBLE
            holder.taskBorderBottom.setOnDragListener(
                DragActions().TaskBorder(
                    groupName,
                    current.id.toString(),
                    tasks.size - 1,
                    mContext as TaskList
                )
            )
        }
        holder.groupItemView.setOnClickListener {
            if (mContext is TaskList) {
                (mContext as TaskList).ToggleDone(
                    groupName,
                    current.id.toString(),
                    holder.groupItemView.isChecked
                )
            }
        }
        holder.taskEditButton.setOnClickListener {
            if (mContext is TaskList) {
                (mContext as TaskList).StartTaskEdit(groupName, current.id.toString())
            }
        }

        val clipDataString = "TASK/$groupName/${current.id}"

        holder.groupItemView.setOnLongClickListener { v: View ->
            val item = ClipData.Item(clipDataString as CharSequence)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData("TASK/$groupName", mimeTypes, item)
            val dragshadow = ShadowMaker(v)
            v.startDragAndDrop(
                data, dragshadow, v, 0
            )
        }

        if (position == 0) {
            holder.taskBorderTop.setOnDragListener(
                DragActions().TaskBorder(
                    groupName,
                    current.id.toString(),
                    position,
                    mContext as TaskList
                )
            )
        } else {
            holder.taskBorderTop.setOnDragListener(
                DragActions().TaskBorder(
                    groupName,
                    current.id.toString(),
                    position - 1,
                    mContext as TaskList
                )
            )
        }

    }

    internal fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    fun updateGroup(group: String) {
        groupName = group
    }

    override fun getItemCount() = tasks.size

}