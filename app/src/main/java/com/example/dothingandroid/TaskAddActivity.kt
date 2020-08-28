package com.example.dothingandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskAddActivity : AppCompatActivity() {

    private lateinit var editGroupView: EditText

    private val GroupAddActivityRequestCode = 2

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)
        editGroupView = findViewById(R.id.edit_task)
        val itemAddHeader = findViewById<TextView>(R.id.taskheader)

        val parent_group: String? = intent.getStringExtra(EXTRA_REPLY)

        itemAddHeader.text = "Add Item To " + parent_group


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editGroupView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val task = editGroupView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, "$task/$parent_group")
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()

        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.dothingandroid.REPLY"
    }
}