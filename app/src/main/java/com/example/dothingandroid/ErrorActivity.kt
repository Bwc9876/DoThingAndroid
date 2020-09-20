package com.example.dothingandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ErrorActivity : AppCompatActivity() {

    private lateinit var groupViewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        groupViewModel = ViewModelProvider(this).get(GroupViewModel::class.java)

        val details = intent.getStringExtra(ErrorActivity.EXTRA_REPLY)
        findViewById<TextView>(R.id.errorText).text = details

        findViewById<Button>(R.id.errorRetry).setOnClickListener {
            val backintent = Intent(this, MainActivity::class.java)
            startActivity(backintent)
        }

        findViewById<Button>(R.id.errorLogout).setOnClickListener {
            GlobalScope.launch {
                val db = DBManager(
                    PreferenceManager.getDefaultSharedPreferences(this@ErrorActivity)
                        .getBoolean("diffIP", false),
                    PreferenceManager.getDefaultSharedPreferences(this@ErrorActivity)
                        .getString("customIP", "None") as String,
                    groupViewModel,
                    this@ErrorActivity,
                    bypassCheck = true
                )
                db.Logout()
            }
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.dothingandroid.REPLY"
    }
}