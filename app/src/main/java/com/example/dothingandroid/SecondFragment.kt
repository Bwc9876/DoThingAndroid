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


class SecondFragment : Fragment() {

    private lateinit var groupViewModel: GroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        groupViewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.login_button).setOnClickListener {
            val usernamefield = view.findViewById<EditText>(R.id.Login_Username)
            val passwordfield = view.findViewById<EditText>(R.id.Login_Password)
            Log.d("DEBUG", "LOGIN PRESSED")
            DBManager().Login(
                usernamefield.text.toString(),
                passwordfield.text.toString(),
                groupViewModel,
                activity as MainActivity
            )
        }
    }
}
