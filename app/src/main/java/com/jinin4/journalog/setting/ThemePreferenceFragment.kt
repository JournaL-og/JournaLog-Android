package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
                    val newFragment=ThemePreviewFragment()
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container_preview, newFragment)
                        ?.commit()
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

        // 처음 화면이 생성될 때 ThemePreviewFragment를 불러옵니다.
        val newFragment = ThemePreviewFragment()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container_preview, newFragment)
            ?.commit()

        // Preview container를 보이게 합니다.
        activity?.findViewById<FrameLayout>(R.id.container_preview)?.visibility = View.VISIBLE

        // RecyclerView에 위쪽 패딩 추가
        val recyclerView = listView

        // 화면의 높이를 가져옵니다.
        val metrics = resources.displayMetrics
        val screenHeight = metrics.heightPixels

        // 화면 높이의 80%를 계산합니다.
        val halfScreenHeight = (screenHeight * 0.8).toInt()
        recyclerView.setPadding(0, halfScreenHeight, 0, 0)
        recyclerView.clipToPadding = false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        val previewFragment = parentFragmentManager.findFragmentById(R.id.container_preview)
        if (previewFragment != null) {
            parentFragmentManager.beginTransaction()
                .remove(previewFragment)
                .commit()
        }
        // Preview container를 숨깁니다.
        activity?.findViewById<FrameLayout>(R.id.container_preview)?.visibility = View.GONE
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
