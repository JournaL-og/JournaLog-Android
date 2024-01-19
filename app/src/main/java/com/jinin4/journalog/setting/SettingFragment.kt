package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jinin4.journalog.databinding.FragmentSettingBinding
//이상원 - 24.01.19
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }
}