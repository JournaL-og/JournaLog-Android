package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jinin4.journalog.utils.BaseFragment
import com.jinin4.journalog.databinding.FragmentFontPreviewBinding

class FontPreviewFragment : BaseFragment() {

    private lateinit var binding: FragmentFontPreviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFontPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

}
