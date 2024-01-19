package com.jinin4.journalog.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jinin4.journalog.databinding.FragmentTagBinding
//이상원 - 24.01.19
class TagFragment : Fragment() {

    private lateinit var binding: FragmentTagBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =FragmentTagBinding.inflate(inflater, container, false)
        return binding.root
    }
}