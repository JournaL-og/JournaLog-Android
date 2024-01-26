package com.jinin4.journalog.setting

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jinin4.journalog.Preference
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.FragmentThemePreferenceBinding
import com.jinin4.journalog.utils.FontUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
// 반정현 - 24.01.23
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
        val themeEntries= resources.getStringArray(R.array.theme_entries)
        for (themeValue in themeValues) {
            val button = Button(requireContext())
            button.text = themeEntries[themeValue.toString().toInt()]
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            val params = GridLayout.LayoutParams()
            params.width = GridLayout.LayoutParams.WRAP_CONTENT
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.setMargins(5, 8, 5, 8)
            button.layoutParams = params

            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_border_button)
            button.setPadding(16, 0, 16, 0)

            button.setOnClickListener {
                val selectedTheme = Preference.Theme.forNumber(themeValue.toString().toInt())
                lifecycleScope.launch {
                    viewModel.changeTheme(selectedTheme)
                    val newFragment = ThemePreviewFragment()
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container_preview, newFragment)
                        ?.commit()
                }
            }

            themeContainer.addView(button)
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
