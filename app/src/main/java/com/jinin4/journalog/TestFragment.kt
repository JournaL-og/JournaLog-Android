package com.jinin4.journalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.jinin4.journalog.databinding.FragmentPhotoBinding
import com.jinin4.journalog.databinding.FragmentTestBinding
import com.jinin4.journalog.firebase.storage.FirebaseFileManager


//이상원 - 24.01.19
class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = firebaseStorage.reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestBinding.inflate(inflater, container, false)
        binding.btnCall.setOnClickListener {
            FirebaseFileManager.loadImageIntoImageView("photo/test.png", binding.imageFire)
        }
        return binding.root
    }
}