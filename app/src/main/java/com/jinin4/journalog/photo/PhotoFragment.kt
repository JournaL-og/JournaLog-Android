package com.jinin4.journalog.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jinin4.journalog.databinding.FragmentPhotoBinding
//이상원 - 24.01.19
class PhotoFragment : Fragment() {

    private lateinit var binding: FragmentPhotoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }
}