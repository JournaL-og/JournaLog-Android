package com.jinin4.journalog.calendar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jinin4.journalog.databinding.BottomSheetMemoCreateBinding
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
import com.prolificinteractive.materialcalendarview.CalendarDay
import net.developia.todolist.db.JournaLogDatabase
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 이상원 - 24.01.22
class MemoCreateBottomSheet(val selectedDate: CalendarDay, val callback: MemoInsertCallback) : BottomSheetDialogFragment()  {

    private lateinit var binding: BottomSheetMemoCreateBinding
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = firebaseStorage.reference

    lateinit var db: JournaLogDatabase
    lateinit var memoDao: MemoDao
    lateinit var strDateTime : String
    lateinit var strDateMonthDay : String
    lateinit var str_time : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetMemoCreateBinding.inflate(inflater, container, false)

        db = JournaLogDatabase.getInstance(binding.root.context)!!
        memoDao = db.getMemoDao()


        val formatterMonthDay = org.threeten.bp.format.DateTimeFormatter.ofPattern("MM월 dd일")
        strDateMonthDay = selectedDate.date.format(formatterMonthDay).toString()
        binding.txtDate.text = strDateMonthDay


        val currentDateTime = LocalDateTime.now()
        val currTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        str_time = currentDateTime.format(currTimeFormatter)
        binding.txtTime.text = str_time

        val formatterDate = org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        strDateTime = selectedDate.date.format(formatterDate).toString()

        binding.fabMemoInsert.setOnClickListener{
            insertMemo()
        }

        binding.txtDate.setOnClickListener {

        }

        binding.btnGallery.setOnClickListener{
            openGallery()
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
        }, 300)
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
                var memoEntity = MemoEntity(null, edtCon, "${strDateTime} ${str_time}:00", 1)
                // 일단 현재 시각으로 넣음, 일단 컬러 id 1로 넣음
                val insertedMemoId = memoDao.insertMemo(memoEntity).toInt()
//                requireActivity().runOnUiThread{
//                    Toast.makeText(binding.root.context, "추가되었습니다.", Toast.LENGTH_SHORT).show()
//                }
                requireActivity().runOnUiThread {
                    callback.onMemoInserted()
                    dismiss()
                }
            }.start()
        }


    }

    private val REQUEST_IMAGE_PICK = 2

// ...

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }


}