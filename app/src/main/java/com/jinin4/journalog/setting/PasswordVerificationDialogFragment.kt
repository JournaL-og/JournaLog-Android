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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PasswordVerificationDialogFragment(private val onVerificationResult: (Boolean) -> Unit) : DialogFragment() {

    private var customTitle: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        // 커스텀 레이아웃을 이용하여 다이얼로그 생성
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_password_setup, null)

        val editTextPassword: EditText = view.findViewById(R.id.editTextPassword)

        // 동적으로 제목을 설정
        customTitle?.let {
            builder.setTitle(it)
        }

        builder.setTitle("비밀번호를 확인해주세요")

        builder.setView(view)
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("확인", null)  // 클릭 이벤트를 null로 설정

        val dialog = builder.create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val enteredPassword = editTextPassword.text.toString()

                // 코루틴을 사용하여 비밀번호 확인
                lifecycleScope.launch {
                    val isPasswordCorrect = checkPassword(enteredPassword)

                    if (isPasswordCorrect) {
                        // 비밀번호가 맞으면 다이얼로그 닫고 결과 전달
                        dialog.dismiss()
                        onVerificationResult(isPasswordCorrect)
                    } else {
                        // 비밀번호가 틀리면 입력 필드 초기화
                        editTextPassword.text.clear()
                        showToast("비밀번호를 확인해주세요")
                    }
                }
            }
        }

        return dialog
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    private suspend fun getPasswordFromDataStore(): String {
        return withContext(Dispatchers.IO) {
            val preferences = context?.dataStore?.data?.first()
            preferences?.password ?: ""
        }
    }



    private suspend fun checkPassword(enteredPassword: String): Boolean {
        return withContext(Dispatchers.IO) {
            val storedPassword = getPasswordFromDataStore()
            enteredPassword == storedPassword
        }
    }
}
