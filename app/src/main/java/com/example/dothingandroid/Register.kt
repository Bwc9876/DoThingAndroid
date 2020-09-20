package com.example.dothingandroid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Register : Fragment() {

    private lateinit var groupViewModel: GroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        groupViewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        return inflater.inflate(R.layout.register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.register_button).setOnClickListener {
            val usernamefield = view.findViewById<EditText>(R.id.Register_Username)
            val passwordfield = view.findViewById<EditText>(R.id.Register_Password)
            Log.d("DEBUG", "REGISTER PRESSED")
            GlobalScope.launch {
                val db = DBManager(
                    PreferenceManager.getDefaultSharedPreferences(view.context)
                        .getBoolean("diffIP", false),
                    PreferenceManager.getDefaultSharedPreferences(view.context)
                        .getString("customIP", "None") as String,
                    groupViewModel,
                    activity as MainActivity
                )
                if (db.valid) {
                    db.Register(
                        usernamefield.text.toString(),
                        passwordfield.text.toString(),
                    )
                }
            }
        }

    }


}