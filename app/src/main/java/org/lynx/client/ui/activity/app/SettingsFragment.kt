package org.lynx.client.ui.activity.app

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.lynx.client.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}