package com.example.dothingandroid

import android.content.ClipData
import android.content.ClipDescription
import android.util.Log
import android.view.DragEvent
import android.view.View

class DragActions {

    fun GroupBorder(index: Int, mContext: TaskList): View.OnDragListener {
        val Group_Border_listen = View.OnDragListener { v, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    if (dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) && dragEvent.clipDescription.label.toString() == "GROUP") {
                        Log.d("DEBUG", "DRAG TRIGGER")
                        v.setBackgroundResource(R.color.border_ready)
                        v.scaleY = 5f
                        v.invalidate()
                        true
                    } else {
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.setBackgroundResource(R.color.border_over)
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION ->
                    true
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.setBackgroundResource(R.color.border_ready)
                    true
                }
                DragEvent.ACTION_DROP -> {
                    v.setBackgroundResource(R.color.group_header_border)
                    v.scaleY = 1f
                    val taskItem: ClipData.Item = dragEvent.clipData.getItemAt(0)
                    Log.i("INFO", taskItem.text.toString())
                    val taskraw = taskItem.text.toString().split("/")
                    mContext.MoveGroup(taskraw[1], index)
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.setBackgroundResource(R.color.group_header_border)
                    v.scaleY = 1f
                    true
                }
                else -> {
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    false
                }
            }

        }
        return Group_Border_listen
    }

    fun Group(group: String, mContext: TaskList): View.OnDragListener {
        val Group_listen = View.OnDragListener { v, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    if (dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) && dragEvent.clipDescription.label.toString()
                            .split("/")[0] == "TASK" && dragEvent.clipDescription.label.toString()
                            .split("/")[1] != group
                    ) {
                        Log.d("DEBUG", "DRAG TRIGGER")
                        val pL = v.paddingLeft
                        val pR = v.paddingRight
                        val pT = v.paddingTop
                        val pB = v.paddingBottom
                        v.setBackgroundResource(R.drawable.group_header_rounded_ready)
                        v.setPadding(pL, pT, pR, pB)
                        true
                    } else {
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    val pL = v.paddingLeft
                    val pR = v.paddingRight
                    val pT = v.paddingTop
                    val pB = v.paddingBottom
                    v.setBackgroundResource(R.drawable.group_header_rounded_over)
                    v.setPadding(pL, pT, pR, pB)
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION ->
                    true
                DragEvent.ACTION_DRAG_EXITED -> {
                    val pL = v.paddingLeft
                    val pR = v.paddingRight
                    val pT = v.paddingTop
                    val pB = v.paddingBottom
                    v.setBackgroundResource(R.drawable.group_header_rounded_ready)
                    v.setPadding(pL, pT, pR, pB)
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val pL = v.paddingLeft
                    val pR = v.paddingRight
                    val pT = v.paddingTop
                    val pB = v.paddingBottom
                    v.setBackgroundResource(R.drawable.group_header_rounded)
                    v.setPadding(pL, pT, pR, pB)
                    val taskItem: ClipData.Item = dragEvent.clipData.getItemAt(0)
                    Log.i("INFO", taskItem.text.toString())
                    val taskraw = taskItem.text.toString().split("/")
                    mContext.MoveTask(taskraw[1], group, taskraw[2])
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    val pL = v.paddingLeft
                    val pR = v.paddingRight
                    val pT = v.paddingTop
                    val pB = v.paddingBottom
                    v.setBackgroundResource(R.drawable.group_header_rounded)
                    v.setPadding(pL, pT, pR, pB)
                    true
                }
                else -> {
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    false
                }
            }

        }
        return Group_listen

    }

    fun TaskBorder(
        group: String,
        task: String,
        index: Int,
        mContext: TaskList
    ): View.OnDragListener {
        val Task_Border_listen = View.OnDragListener { v, dragEvent ->

            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    if (dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) && dragEvent.clipDescription.label.toString()
                            .split("/")[0] == "TASK" && dragEvent.clipDescription.label.toString()
                            .split("/")[1] == group
                    ) {
                        Log.d("DEBUG", "DRAG TRIGGER")
                        v.setBackgroundResource(R.color.border_ready)
                        v.scaleY = 20f
                        v.invalidate()
                        true
                    } else {
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.setBackgroundResource(R.color.border_over)
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION ->
                    true
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.setBackgroundResource(R.color.border_ready)
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    v.setBackgroundResource(R.color.group_header_border)
                    v.scaleY = 1f
                    v.invalidate()
                    val taskItem: ClipData.Item = dragEvent.clipData.getItemAt(0)
                    Log.i("INFO", taskItem.text.toString())
                    val taskraw = taskItem.text.toString().split("/")
                    mContext.ChangeTaskOrder(taskraw[2], index, taskraw[1], group)
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.setBackgroundResource(R.color.group_header_border)
                    v.scaleY = 1f
                    v.invalidate()
                    true
                }
                else -> {
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    false
                }
            }

        }
        return Task_Border_listen
    }
}