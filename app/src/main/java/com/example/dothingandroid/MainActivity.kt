package com.example.dothingandroid

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var groupViewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        groupViewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        GlobalScope.launch {
            val db = DBManager(
                PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                    .getBoolean("diffIP", false),
                PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                    .getString("customIP", "None") as String,
                groupViewModel,
                this@MainActivity,
                bypassCheck = true
            )
            if (db.valid) {
                db.Continue_if_Data()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.welcome_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
                false
            }
        }
    }
}
