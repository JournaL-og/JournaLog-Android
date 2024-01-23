package com.jinin4.journalog.calendar.bottom_sheet

import MultiImageAdapter
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jinin4.journalog.MemoRightColorSetting
import com.jinin4.journalog.R
import com.jinin4.journalog.calendar.MemoInsertCallback
import com.jinin4.journalog.databinding.BottomSheetMemoCreateBinding
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
import com.prolificinteractive.materialcalendarview.CalendarDay
import net.developia.todolist.db.JournaLogDatabase
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
    lateinit var adapter: MultiImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetMemoCreateBinding.inflate(inflater, container, false)

        db = JournaLogDatabase.getInstance(binding.root.context)!!
        memoDao = db.getMemoDao()


        val layerDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.rounded_textview_background_content) as LayerDrawable
        binding.edtContent.background=
            MemoRightColorSetting.changeRightColor(
                ContextCompat.getColor(binding.root.context, R.color.red_sw),layerDrawable)
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
                var memoEntity = MemoEntity(null, edtCon, "${strDateTime} ${str_time}:00", 3)
                // 일단 컬러 id 1로 넣음
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


    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 처리할 로직
                val data: Intent? = result.data
                handleImageSelection(data)

                // 결과 처리
                Toast.makeText(binding.root.context, "테스트 성공~", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(binding.root.context, "실패~~", Toast.LENGTH_SHORT).show()
            }
        }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = MediaStore.Images.Media.CONTENT_TYPE
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        someActivityResultLauncher.launch(intent)
    }
    private val uriList = ArrayList<Uri>()
    private fun handleImageSelection(data: Intent?) {

        if (data?.clipData == null) {     // 이미지를 하나만 선택한 경우0
            val imageUri: Uri = data?.data!!
            uriList.add(imageUri)
            updateAdapter()
        } else {      // 이미지를 여러장 선택한 경우
            val clipData: ClipData = data.clipData!!
            for (i in 0 until clipData.itemCount) {
                val imageUri: Uri = clipData.getItemAt(i).uri
                uriList.add(imageUri)
            }

            updateAdapter()
        }
    }

    private fun updateAdapter() {
        val recyclerView = binding.rvImageSlide
        adapter = MultiImageAdapter(uriList, binding.root.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
    }

}