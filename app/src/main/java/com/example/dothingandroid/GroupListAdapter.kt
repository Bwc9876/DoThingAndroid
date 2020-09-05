package com.example.dothingandroid

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.cachapa.expandablelayout.ExpandableLayout


class GroupListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    private var mContext: Context? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var groups = emptyList<Group>()

    private val TaskAddActivityRequestCode = 2

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupItemView: TextView = itemView.findViewById(R.id.textView)
        val groupExpandView: ExpandableLayout = itemView.findViewById(R.id.expandable_layout)
        val groupAddTaskButton: Button = itemView.findViewById(R.id.button_task_add)
        val group: ViewGroup = itemView.findViewById(R.id.expandable_layout) as ViewGroup
        val itemViewer: View = itemView
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerviewtasks)
        val groupBorderTop: View = itemView.findViewById(R.id.group_seperator_top)
        val groupBorderBottom: View = itemView.findViewById(R.id.group_seperator_bottom)
        val editgroup: View = itemView.findViewById(R.id.edit_group_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return GroupViewHolder(itemView)
    }

    fun GroupListAdapter(context: Context) {
        this.mContext = context
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val current = groups[position]
        holder.groupItemView.text = current.Name
        Log.i("INFO", current.Position.toString())
        if (current.Position == 0) {
            holder.groupBorderTop.visibility = View.VISIBLE
            holder.groupBorderTop.setOnDragListener(
                DragActions().GroupBorder(
                    0,
                    mContext as TaskList
                )
            )
        } else {
            holder.groupBorderTop.visibility = View.GONE
        }

        holder.editgroup.setOnClickListener {
            if (mContext is TaskList) {
                (mContext as TaskList).StartGroupEdit(current.Name)
            }
        }


        holder.groupAddTaskButton.setOnClickListener {
            Log.d("DEBUG", mContext.toString())
            if (mContext is TaskList) {
                (mContext as TaskList).StartTaskAdd(current.Name)
            }
        }
        holder.groupItemView.setOnClickListener {
            holder.groupExpandView.toggle()
        }

        val clipDataString = "GROUP/${current.Name}"

        holder.groupItemView.setOnLongClickListener { v: View ->
            val item = ClipData.Item(clipDataString as CharSequence)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData("GROUP", mimeTypes, item)
            val dragshadow = ShadowMaker(v)
            v.startDragAndDrop(
                data, dragshadow, v, 0
            )
        }

        val tasks = current.Items.split('/')
        val taskobjs: MutableList<Task> = ArrayList()
        if (current.Items != "NONE") {
            for (task in tasks) {
                val tasksplit = task.split(',')
                if (tasksplit[0] != "NONE") {
                    taskobjs.add(Task(tasksplit[0].toInt(), tasksplit[1], tasksplit[2].toBoolean()))
                }
            }
        }
        if (taskobjs.isEmpty()) {
            holder.groupItemView.text = current.Name + " (Empty)"
        }
        holder.groupItemView.setOnDragListener(
            DragActions().Group(
                current.Name,
                mContext as TaskList
            )
        )
        if (position == groups.size - 1) {
            holder.groupBorderBottom.setOnDragListener(
                DragActions().GroupBorder(
                    position,
                    mContext as TaskList
                )
            )
        } else {
            holder.groupBorderBottom.setOnDragListener(
                DragActions().GroupBorder(
                    position + 1,
                    mContext as TaskList
                )
            )
        }

        val taskAdapter = TaskListAdapter(holder.itemViewer.context)
        taskAdapter.TaskListAdapter(mContext)
        taskAdapter.updateGroup(current.Name)
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