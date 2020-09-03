package com.example.dothingandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_Splash_to_Login)
        }
        Log.d("DEBUG", view.findViewById<Button>(R.id.button_register).text.toString())
        view.findViewById<Button>(R.id.button_register).setOnClickListener {
            findNavController().navigate(R.id.action_Splash_to_Register)
        }
        view.findViewById<Button>(R.id.button_settings).setOnClickListener {
            val intent = Intent(this.context, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
