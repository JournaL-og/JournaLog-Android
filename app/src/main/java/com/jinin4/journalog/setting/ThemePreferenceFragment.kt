package com.jinin4.journalog.setting

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jinin4.journalog.Preference
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.FragmentThemePreferenceBinding
import com.jinin4.journalog.utils.FontUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// 반정현 수정 - 24.01.26
@AndroidEntryPoint
class ThemePreferenceFragment : Fragment() {
    private val viewModel: ThemeViewModel by viewModels()
    private lateinit var binding : FragmentThemePreferenceBinding
    private lateinit var themeContainer: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigation(true)
        binding = FragmentThemePreferenceBinding.inflate(inflater, container, false)
        val view=binding.root
        themeContainer=binding.themeContainer
        val themeValues = resources.getStringArray(R.array.theme_values)
        val themeEntries = resources.getStringArray(R.array.theme_entries)
        val vectorDrawables = arrayOf(
            R.drawable.ic_theme_1,
            R.drawable.ic_theme_2,
            R.drawable.ic_theme_3,
            R.drawable.ic_theme_4,
            // 필요에 따라 더 많은 벡터 드로어블 추가
        )

        for ((index, themeValue) in themeValues.withIndex()) {
            val imageButton = ImageButton(requireContext())
            imageButton.setImageResource(vectorDrawables[index])
            imageButton.setBackgroundColor(Color.TRANSPARENT) // Optional: Make background transparent
            imageButton.scaleType = ImageView.ScaleType.FIT_CENTER
            imageButton.setPadding(16, 16, 16, 16)

            // Set up text below the image
            val buttonText = TextView(requireContext())
            buttonText.text = themeEntries[themeValue.toInt()]
            buttonText.gravity = Gravity.CENTER
            buttonText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

            // Create a vertical LinearLayout to hold the image and text
            val linearLayout = LinearLayout(requireContext())
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.addView(imageButton)
            linearLayout.addView(buttonText)

            // Set up ImageButton properties with GridLayout.LayoutParams for the LinearLayout
            val params = GridLayout.LayoutParams()
            params.width = GridLayout.LayoutParams.WRAP_CONTENT
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.setMargins(20, 8, 20, 8)
            linearLayout.layoutParams = params // Use GridLayout.LayoutParams for the LinearLayout

            // Set up click listener
            imageButton.setOnClickListener {
                val selectedTheme = Preference.Theme.forNumber(themeValue.toInt())
                lifecycleScope.launch {
                    viewModel.changeTheme(selectedTheme)
                    val newFragment = ThemePreviewFragment()
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container_preview, newFragment)
                        ?.commit()
                }
            }

            themeContainer.addView(linearLayout)
        }


        return view
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
        viewLifecycleOwner.lifecycleScope.launch {
            val typeface = FontUtils.getFontType(requireContext())
            FontUtils.applyFont(view, typeface, 14f)
        }
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
