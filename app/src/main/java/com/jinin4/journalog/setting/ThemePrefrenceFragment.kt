package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jinin4.journalog.Preference
import com.jinin4.journalog.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
// 반정현 - 24.01.23
@AndroidEntryPoint
class ThemePreferenceFragment : PreferenceFragmentCompat() {
    private val viewModel: ThemeViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.theme_preference, rootKey)
        val themePreference = findPreference<ListPreference>("Theme")

        themePreference?.onPreferenceChangeListener =
            androidx.preference.Preference.OnPreferenceChangeListener { preference, newValue ->
                val selectedTheme = Preference.Theme.forNumber(newValue.toString().toInt())
                lifecycleScope.launch {
                    viewModel.changeTheme(selectedTheme)
                }
                true
            }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        hideBottomNavigation(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView에 위쪽 패딩 추가
        val recyclerView = listView
        val padding = resources.getDimensionPixelSize(R.dimen.toolbar_height)
        recyclerView.setPadding(0, padding, 0, 0)
        recyclerView.clipToPadding = false
    }

    //액션바 뒤로가기 버튼 활성화
    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //액션바 뒤로가기 버튼 비활성화
    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(false)
    }

    private fun hideBottomNavigation(isHide: Boolean) {
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (isHide) {
            bottomNavigation?.visibility = View.GONE
        } else {
            bottomNavigation?.visibility = View.VISIBLE
        }
    }
}
