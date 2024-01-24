package com.jinin4.journalog.setting

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jinin4.journalog.R
import com.jinin4.journalog.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
// 반정현 작성 - 24.01.22
class FontPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.font_preference, rootKey)

        //fonttype 설정
        val fontTypePreference = findPreference<ListPreference>("fontType")
        fontTypePreference?.summary = fontTypePreference?.entry
        fontTypePreference?.setOnPreferenceChangeListener { preference, newValue ->
            val selectedFont = newValue.toString()

            lifecycleScope.launch {
                context?.dataStore?.updateData { preferences ->
                    preferences.toBuilder().setFontType(selectedFont).build()
                }
            }
            val index = fontTypePreference?.findIndexOfValue(newValue.toString())
            preference.summary = fontTypePreference?.entries?.get(index ?: 0)
            // 값이 변경될 때마다 FontPreviewFragment를 다시 불러옵니다.
            val newFragment = FontPreviewFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container_preview, newFragment)
                ?.commit()
            true
        }

        // fontSize 설정
        val fontSizePreference = findPreference<SeekBarPreference>("fontSize")
        fontSizePreference?.setOnPreferenceChangeListener { preference, newValue ->
            val selectedFontSize = (newValue as Int) / 4

            lifecycleScope.launch {
                context?.dataStore?.updateData { preferences ->
                    preferences.toBuilder().setFontSize(selectedFontSize).build()
                }
            }
            // 값이 변경될 때마다 FontPreviewFragment를 다시 불러옵니다.
            val newFragment = FontPreviewFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container_preview, newFragment)
                ?.commit()
            true
        }

    }
    // 이 화면에서는 하단 네비게이션 바 비활성화
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        hideBottomNavigation(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면이 생성될 때 FontPreviewFragment를 불러옵니다.
        val newFragment = FontPreviewFragment()
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

        // 화면 높이의 50%를 계산합니다.
        val halfScreenHeight = screenHeight / 2
        recyclerView.setPadding(0, halfScreenHeight, 0, 0)
        recyclerView.clipToPadding = false
        viewLifecycleOwner.lifecycleScope.launch {
            val typeface = getFontType(requireContext())
            applyFont(view, typeface)
        }


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
        // FontPreviewFragment를 제거합니다.
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

    private suspend fun getFontType(context: Context): Typeface? {
        val fontType = context.dataStore.data.first().fontType ?: "notosans"
        return when (fontType) {
            "notosans" -> resources.getFont(R.font.notosans)
            "nanumgothic" -> resources.getFont(R.font.nanumgothic)
            "nanummyeongjo" -> resources.getFont(R.font.nanummyeongjo)
            else -> resources.getFont(R.font.notosans)
        }
    }

    private fun applyFont(view: View, typeface: Typeface?) {
        when (view) {
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    applyFont(view.getChildAt(i), typeface)
                }
            }
            is TextView -> {
                view.typeface = typeface
            }
        }
    }

}