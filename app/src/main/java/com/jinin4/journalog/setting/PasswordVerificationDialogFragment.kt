package com.jinin4.journalog.setting

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.jinin4.journalog.databinding.DialogPasswordVerificationBinding
import com.jinin4.journalog.utils.PasswordUtils
import kotlinx.coroutines.launch

// 반정현 작성 - 24.01.27
class PasswordVerificationDialogFragment(private val onVerificationResult: (Boolean) -> Unit) : DialogFragment() {
    private lateinit var binding: DialogPasswordVerificationBinding

    private var customTitle: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        binding = DialogPasswordVerificationBinding.inflate(LayoutInflater.from(context))

        // 동적으로 제목을 설정
        customTitle?.let {
            builder.setTitle(it)
        }

        builder.setTitle("비밀번호를 확인해주세요.")

        builder.setView(binding.root)
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("확인", null)

        val dialog = builder.create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val enteredPassword = binding.editTextPassword.text.toString()

                // 코루틴을 사용하여 비밀번호 확인
                lifecycleScope.launch {
                    val isPasswordCorrect = PasswordUtils.checkPassword(requireContext(),enteredPassword)

                    if (isPasswordCorrect) {
                        // 비밀번호가 맞으면 다이얼로그 닫고 결과 전달
                        dialog.dismiss()
                        onVerificationResult(isPasswordCorrect)
                    } else {
                        // 비밀번호가 틀리면 입력 필드 초기화
                        binding.editTextPassword.text.clear()
                        PasswordUtils.showToast(requireContext(),"비밀번호를 확인해주세요.")
                    }
                }
            }
        }

        return dialog
    }

}
