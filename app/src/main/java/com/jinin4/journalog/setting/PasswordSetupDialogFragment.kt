package com.jinin4.journalog.setting

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.jinin4.journalog.R
import com.jinin4.journalog.dataStore
import com.jinin4.journalog.utils.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PasswordSetupDialogFragment(private val onPasswordEntered: (String) -> Unit) : DialogFragment() {

    private var customTitle: String? = null
    private lateinit var editTextPassword: EditText
    private var isFirstAttempt = true

    fun setTitle(title: String) {
        customTitle = title
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        // 커스텀 레이아웃을 이용하여 다이얼로그 생성
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_password_setup, null)

        editTextPassword = view.findViewById(R.id.editTextPassword)

        // 동적으로 제목을 설정
        customTitle?.let {
            builder.setTitle(it)
        }

        builder.setView(view)
            .setNegativeButton("취소", null) // 클릭 이벤트를 null로 설정
            .setPositiveButton("확인", null)  // 클릭 이벤트를 null로 설정

        val dialog = builder.create()

        dialog.setOnShowListener {
            val negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE)
            negativeButton.setOnClickListener {
                lifecycleScope.launch {
                    PasswordUtils.clearPasswordInDataStore(requireContext())
                    dialog.dismiss()
                }
            }

            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                handleConfirmButtonClick(dialog)
            }
        }

        return dialog
    }

    private fun handleConfirmButtonClick(dialog: Dialog) {
        val password = editTextPassword.text.toString()

        lifecycleScope.launch {
            if (isFirstAttempt) {
                handleFirstAttempt(password,dialog)
            } else {
                handleSecondAttempt(password, dialog)
            }
        }
    }

    private suspend fun handleFirstAttempt(password: String, dialog: Dialog) {
        // 첫 번째 시도에서는 타이틀만 변경
        savePasswordToDataStore(password)
        editTextPassword.text.clear()
        dialog.setTitle("비밀번호를 확인해주세요")
        isFirstAttempt = false
    }

    private suspend fun handleSecondAttempt(password: String, dialog: Dialog) {
        // 두 번째 시도에서는 실제 비밀번호 확인
        if (PasswordUtils.checkPassword(requireContext(),password)) {
            savePasswordToDataStore(password)
            PasswordUtils.showToast(requireContext(),"비밀번호가 설정되었습니다")
            dialog.dismiss()
        } else {
            PasswordUtils.showToast(requireContext(),"비밀번호를 확인해주세요")
            editTextPassword.text.clear()
        }
    }

    private suspend fun savePasswordToDataStore(newPassword: String) {
        context?.dataStore?.updateData { preferences ->
            preferences.toBuilder().setPassword(newPassword).build()
        }
    }

}
