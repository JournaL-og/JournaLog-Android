package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jinin4.journalog.R
import com.jinin4.journalog.utils.FontUtils
import kotlinx.coroutines.launch

//이상원 - 24.01.19, 반정현 - 24.01.22 수정

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        val displayPreference = findPreference<Preference>("display_category")
        displayPreference?.setOnPreferenceClickListener {
            val fragment = DisplayPreferenceFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view_tag, fragment)
                .addToBackStack(null)
                .commit()
            true
        }
        val fontPreference = findPreference<Preference>("font_category")
        fontPreference?.setOnPreferenceClickListener {
            val fragment = FontPreferenceFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view_tag, fragment)
                .addToBackStack(null)
                .commit()
            true
        }
        val themePreference = findPreference<Preference>("theme_category")
        themePreference?.setOnPreferenceClickListener {
            val fragment = ThemePreferenceFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view_tag, fragment)
                .addToBackStack(null)
                .commit()
            true
        }
    }
}
