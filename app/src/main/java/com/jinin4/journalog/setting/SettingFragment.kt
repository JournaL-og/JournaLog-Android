package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.FragmentSettingBinding
import com.jinin4.journalog.utils.FontUtils
import kotlinx.coroutines.launch

//이상원 - 24.01.19, 반정현 - 24.01.22 수정

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            val typeface = FontUtils.getFontType(requireContext())
            FontUtils.applyFont(view, typeface,17f)
        }

        // Set click listeners using view binding
        binding.apply {
            displayCategory.setOnClickListener { onPreferenceClick(displayCategory) }
            fontCategory.setOnClickListener { onPreferenceClick(fontCategory) }
            themeCategory.setOnClickListener { onPreferenceClick(themeCategory) }
            privacyCategory.setOnClickListener { onPreferenceClick(privacyCategory) }
        }
    }

    private fun onPreferenceClick(view: View) {
        val fragment: Fragment = when (view.tag as String) {
            "display_category" -> DisplayPreferenceFragment()
            "font_category" -> FontPreferenceFragment()
            "theme_category" -> ThemePreferenceFragment()
            "privacy_category" -> DisplayPreferenceFragment()
            else -> throw IllegalArgumentException("Invalid tag")
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_tag, fragment)
            .addToBackStack(null)
            .commit()
    }
}

