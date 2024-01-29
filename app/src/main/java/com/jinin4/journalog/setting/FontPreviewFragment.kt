package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.jinin4.journalog.utils.BaseFragment
import com.jinin4.journalog.databinding.FragmentFontPreviewBinding
//반정현 수정 - 24.01.26
class FontPreviewFragment : BaseFragment() {

    private lateinit var binding: FragmentFontPreviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFontPreviewBinding.inflate(inflater, container, false)
        binding.backButton.setOnClickListener{
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction
                .replace(androidx.fragment.R.id.fragment_container_view_tag ,
                    SettingFragment()
                ).commit()
        }
        return binding.root
    }

}
