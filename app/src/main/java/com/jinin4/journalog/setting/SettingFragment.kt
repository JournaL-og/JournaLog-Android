package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.FragmentSettingBinding
import com.jinin4.journalog.utils.FontUtils
import com.jinin4.journalog.utils.PasswordUtils
import kotlinx.coroutines.launch

//이상원 - 24.01.19, 반정현 - 24.01.22 수정
//반정현 수정 - 24.01.26
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
        when (view.tag as String) {
            "display_category" -> replaceFragment(DisplayPreferenceFragment())
            "font_category" -> replaceFragment(FontPreferenceFragment())
            "theme_category" -> replaceFragment(ThemePreferenceFragment())
            "privacy_category" -> showPrivacyOptions()
            else -> throw IllegalArgumentException("잘못된 태그")
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_tag, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showPrivacyOptions() {
        lifecycleScope.launch {
            val password = PasswordUtils.getPasswordFromDataStore(requireContext())

            if (password.isEmpty()) {
                // 비밀번호가 설정되어 있지 않으면 비밀번호 설정 다이얼로그 표시
                showPasswordSetupDialog()
            } else {
                // 비밀번호가 설정되어 있으면 비밀번호 확인 다이얼로그 표시
                showPasswordVerificationDialog()
            }
        }
    }

    private fun showPasswordSetupDialog() {
        val passwordSetupDialog = PasswordSetupDialogFragment { newPassword ->
            lifecycleScope.launch {
                if (PasswordUtils.isValidPassword(newPassword) && isFirstPasswordAttempt(newPassword)) {
                    // 첫 번째 시도
                    PasswordUtils.showToast(requireContext(),"비밀번호를 확인해주세요")
                } else {
                    // 두 번째 시도
                    if (PasswordUtils.isValidPassword(newPassword)) {
                        PasswordUtils.savePasswordToDataStore(requireContext(),newPassword)
                        PasswordUtils.showToast(requireContext(),"비밀번호가 설정되었습니다")
                    } else {
                        PasswordUtils.showToast(requireContext(),"4자리 숫자로 비밀번호를 설정해주세요")
                    }
                }
            }
        }

        // 초기 타이틀 설정
        val initialTitle = "4자리 숫자를 입력해주세요"
        passwordSetupDialog.setTitle(initialTitle)

        passwordSetupDialog.show(parentFragmentManager, "PasswordSetupDialog")
    }

    private fun isFirstPasswordAttempt(newPassword: String): Boolean {
        return newPassword.isEmpty() || !PasswordUtils.isValidPassword(newPassword)
    }

    private fun showPasswordVerificationDialog() {
        val passwordVerificationDialog = PasswordVerificationDialogFragment { isPasswordCorrect ->
            lifecycleScope.launch {
                if (isPasswordCorrect) {
                    // 비밀번호가 맞으면 비밀번호 해제 메시지 표시하고 비밀번호 초기화
                    PasswordUtils.showToast(requireContext(),"비밀번호가 비활성되었습니다")
                    PasswordUtils.clearPasswordInDataStore(requireContext())
                }
            }
        }
        passwordVerificationDialog.show(parentFragmentManager, "PasswordVerificationDialog")
    }

}

