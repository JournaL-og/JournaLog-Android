package com.jinin4.journalog.calendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.BottomSheetModalBinding
import com.jinin4.journalog.databinding.FragmentCalendarBinding
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
import com.prolificinteractive.materialcalendarview.CalendarDay
import net.developia.todolist.db.JournaLogDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ModalBottomSheet(val selectedDate: CalendarDay) : BottomSheetDialogFragment()  {

    private lateinit var binding: BottomSheetModalBinding
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = firebaseStorage.reference

    lateinit var db: JournaLogDatabase
    lateinit var memoDao: MemoDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =BottomSheetModalBinding.inflate(inflater, container, false)

        db = JournaLogDatabase.getInstance(binding.root.context)!!
        memoDao = db.getMemoDao()
        val editText = binding.edtContent
        val formatter_datetime = org.threeten.bp.format.DateTimeFormatter.ofPattern("MM-dd")
        binding.txtDate.text = selectedDate.date.format(formatter_datetime).toString()
        binding.fabMemoInsert.setOnClickListener{
            insertMemo()
        }

        return binding.root
    }

    companion object {
        const val TAG = "BasicBottomModalSheet"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editText = binding.edtContent
        editText.postDelayed({
            editText.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 250)
    }

    fun insertMemo() {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)
        val edtCon = binding.edtContent.text.toString()



        if (edtCon.isBlank()) {
            requireActivity().runOnUiThread{
                    Toast.makeText(binding.root.context, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Thread{
                var memoEntity = MemoEntity(null, edtCon, formattedDateTime, 1)
                // 일단 현재 시각으로 넣음, 일단 컬러 id 1로 넣음
                memoDao.insertMemo(memoEntity)
//                requireActivity().runOnUiThread{
//                    Toast.makeText(binding.root.context, "추가되었습니다.", Toast.LENGTH_SHORT).show()
//                }
                requireActivity().runOnUiThread {
                    dismiss()
                }
            }.start()
        }


    }
}