package com.example.dothingandroid

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText

class GroupAddActivity : AppCompatActivity() {

    private lateinit var editGroupView: EditText

    private val GroupAddActivityRequestCode = 1

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)
        editGroupView = findViewById(R.id.edit_group)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editGroupView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }
            else {
                val group = editGroupView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, group)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()

        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.dothingandroid.REPLY"
    }
}