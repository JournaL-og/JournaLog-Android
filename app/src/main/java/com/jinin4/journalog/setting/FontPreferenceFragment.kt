package com.jinin4.journalog.setting


import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jinin4.journalog.R
import com.jinin4.journalog.dataStore
import com.jinin4.journalog.databinding.FragmentFontPreferenceBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
//반정현 작성 - 24.01.22
//반정현 수정 - 24.01.26
class FontPreferenceFragment : Fragment() {

    private lateinit var binding: FragmentFontPreferenceBinding
    private lateinit var fontTypeContainer: GridLayout
    private lateinit var fontSizeSeekBar: SeekBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigation(true)
        binding = FragmentFontPreferenceBinding.inflate(inflater, container, false)
        val view = binding.root

        fontTypeContainer = binding.fontTypeContainer
        fontSizeSeekBar = binding.fontSizeSeekBar

        // 버튼 추가
        val fontTypes = resources.getStringArray(R.array.font_values)
        for (fontType in fontTypes) {
            val button = Button(requireContext())
            button.text = fontType

            // 현재 폰트에 대한 Typeface 얻기
            val typeface = ResourcesCompat.getFont(requireContext(), resources.getIdentifier(fontType, "font", requireContext().packageName))
            button.setTypeface(typeface)
            if (fontType == "nanumpen") button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
            else button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

            val params = GridLayout.LayoutParams()
            params.width = GridLayout.LayoutParams.WRAP_CONTENT
            params.height = 100
            params.setMargins(5, 8, 5, 8)
            button.layoutParams = params

            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border_button)
            button.setPadding(16, 0, 16, 0)

            button.setOnClickListener {
                val selectedFont = fontType

                // 저장된 데이터를 업데이트
                lifecycleScope.launch {
                    context?.dataStore?.updateData { preferences ->
                        preferences.toBuilder().setFontType(selectedFont).build()
                    }
                }
            }

            fontTypeContainer.addView(button)
        }

        // 초기화 시에 저장된 데이터를 읽어와서 SeekBar의 progress 설정
        lifecycleScope.launch {
            val preferences = context?.dataStore?.data?.first()
            var initialFontSize = preferences?.fontSize ?: 17
            if (initialFontSize<=0){
                initialFontSize=17
            }
            fontSizeSeekBar.progress = initialFontSize
        }

        fontSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 사용자가 조작하는 동안 실시간으로 DataStore에 업데이트
                if (fromUser) {
                    val selectedFontSize = progress
                    lifecycleScope.launch {
                        context?.dataStore?.updateData { preferences ->
                            preferences.toBuilder().setFontSize(selectedFontSize).build()
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면이 생성될 때 FontPreviewFragment를 불러옵니다.
        val newFragment = FontPreviewFragment()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container_preview, newFragment)
            ?.commit()

        // Preview container 보이게 설정
        activity?.findViewById<FrameLayout>(R.id.container_preview)?.visibility = View.VISIBLE

        val fontSettingsFlow = context?.dataStore?.data?.map { preferences ->
            Pair(preferences.fontType, preferences.fontSize)
        }

        // DataStore의 fontType,fontSize 변경을 감지(observe 메서드)하고 이를 반영하여 UI를 업데이트
        fontSettingsFlow?.asLiveData()?.observe(viewLifecycleOwner) { (fontType, fontSize)  ->
            val newFragment = FontPreviewFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container_preview, newFragment)
                ?.commit()
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        // FontPreviewFragment를 제거
        val previewFragment = parentFragmentManager.findFragmentById(R.id.container_preview)
        if (previewFragment != null) {
            parentFragmentManager.beginTransaction()
                .remove(previewFragment)
                .commit()
        }

        // Preview container를 숨김
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