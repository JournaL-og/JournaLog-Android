package com.jinin4.journalog.utils

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

//반정현 작성 - 24.01.24
abstract class BaseFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val typeface = FontUtils.getFontType(requireContext())
            val fontSize = FontUtils.getFontSize(requireContext())
            FontUtils.applyFont(view, typeface, fontSize)
        }
    }
}

