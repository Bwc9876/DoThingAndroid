package com.example.dothingandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment

class EditGroupActivity : AppCompatActivity(), ConfirmDeleteDialog.NoticeDialogListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_group)
        val editGroupView = findViewById<EditText>(R.id.new_task_name)
        val itemAddHeader = findViewById<TextView>(R.id.title_edit)

        val old_name: String? = intent.getStringExtra(TaskAddActivity.EXTRA_REPLY)

        itemAddHeader.text = "Change Name Of " + old_name


        val button = findViewById<Button>(R.id.save_task_edit)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editGroupView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val new_name = editGroupView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, "$new_name/$old_name")
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()

        }
        findViewById<Button>(R.id.delete_button).setOnClickListener {
            ConfirmDeleteDialog().show(supportFragmentManager, "Confirm")
        }
    }


    override fun onDialogPositiveClick(dialog: DialogFragment) {
        Log.d("DEBUG", "Owen shit himself")
        val old_name: String? = intent.getStringExtra(TaskAddActivity.EXTRA_REPLY)
        val reply = Intent()
        reply.putExtra(EXTRA_REPLY, "**DEL**/$old_name")
        setResult(Activity.RESULT_OK, reply)
        finish()
    }


    companion object {
        const val EXTRA_REPLY = "com.example.dothingandroid.REPLY"
    }
}