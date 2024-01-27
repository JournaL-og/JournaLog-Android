package com.jinin4.journalog.setting

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        // 커스텀 레이아웃을 이용하여 다이얼로그 생성
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_password_verification, null)

        val editTextPassword: EditText = view.findViewById(R.id.editTextPassword)
        val buttonCancel: Button = view.findViewById(R.id.buttonCancel)
        val buttonVerify: Button = view.findViewById(R.id.buttonVerify)

        builder.setView(view)
            .setTitle("비밀번호 확인")
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonVerify.setOnClickListener {
            val enteredPassword = editTextPassword.text.toString()

            // 코루틴을 사용하여 비밀번호 확인
            lifecycleScope.launch {
                val isPasswordCorrect = checkPassword(enteredPassword)

                // 결과를 전달하거나 필요한 작업을 수행
                onVerificationResult(isPasswordCorrect)
            }

            dialog.dismiss()
        }

        return dialog
    }

    private suspend fun getPasswordFromDataStore(): String {
        return withContext(Dispatchers.IO) {
            val preferences = context?.dataStore?.data?.first()
            preferences?.password ?: ""
        }
    }

// ...

    private suspend fun checkPassword(enteredPassword: String): Boolean {
        return withContext(Dispatchers.IO) {
            val storedPassword = getPasswordFromDataStore()
            enteredPassword == storedPassword
        }
    }
}
