package com.example.dothingandroid

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat


class fragPref : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
    }
}