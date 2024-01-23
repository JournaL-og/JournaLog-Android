package com.jinin4.journalog.calendar.bottom_sheet

import MultiImageAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
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
import com.jinin4.journalog.db.photo.MemoPhotoDao
import com.jinin4.journalog.db.photo.MemoPhotoEntity
import com.jinin4.journalog.db.photo.PhotoDao
import com.jinin4.journalog.db.photo.PhotoEntity
import com.jinin4.journalog.firebase.storage.FirebaseFileManager
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
    lateinit var memoPhotoDao: MemoPhotoDao
    lateinit var photoDao: PhotoDao
    lateinit var strDateTime : String
    lateinit var strDateMonthDay : String
    lateinit var str_time : String
    lateinit var imageAdapter: MultiImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetMemoCreateBinding.inflate(inflater, container, false)

        db = JournaLogDatabase.getInstance(binding.root.context)!!
        memoDao = db.getMemoDao()
        memoPhotoDao = db.getMemoPhotoDao()
        photoDao = db.getPhotoDao()


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

        if (edtCon.isBlank() && uriList.isEmpty()) {
            requireActivity().runOnUiThread{
                    showToast("메모나 사진을 추가해주세요.")
                }
        } else {
            Thread{
                val memoEntity = MemoEntity(null, edtCon, "${strDateTime} ${str_time}:00", 3)

                // 일단 컬러 id 1로 넣음
                val insertedMemoId = memoDao.insertMemo(memoEntity).toInt()
                if (uriList.isNotEmpty()) {
                    val pathList = uploadImageToFireBaseAndInsertPhoto(uriList)
                    for ((i, path) in pathList.withIndex()) {
                        val photoEntity = PhotoEntity(null,insertedMemoId,path)
                        val insertedPhotoId = photoDao.insertPhoto(photoEntity).toInt()
                        val memoPhotoEntity = MemoPhotoEntity(null,insertedMemoId, insertedPhotoId)
                        memoPhotoDao.insertMemoPhoto(memoPhotoEntity)
                    }
                }
                requireActivity().runOnUiThread {
                    callback.onMemoInserted()
                    dismiss()
                }
            }.start()
        }
    }


    private fun uploadImageToFireBaseAndInsertPhoto(uriList: ArrayList<Uri>): ArrayList<String> {
//        val serialNumber = Build.SERIAL
        val androidID: String = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "default"
        val pathList = ArrayList<String>()
        for ((i, uri) in uriList.withIndex()) {
            val path = "${androidID}/${System.currentTimeMillis()}_${i+1}.jpg"
            pathList.add(path)
            FirebaseFileManager.uploadImage(
                uri,
                path,
                onSuccess = {

                },
                onFailure = { exception ->
                    // 업로드 실패 시
                    // TODO: 업로드 실패에 따른 추가 작업 수행
//                    showToast("이미지 업로드 실패: ${exception.message}")
                }
            )
        }
        return pathList

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
            if (clipData.itemCount > 12) {
                showToast("사진은 12장까지 선택할 수 있습니다.")
                return
            }
            for (i in 0 until clipData.itemCount) {
                val imageUri: Uri = clipData.getItemAt(i).uri
                uriList.add(imageUri)
            }

            updateAdapter()
        }
    }

    private fun updateAdapter() {
        val recyclerView = binding.rvImageSlide
        imageAdapter = MultiImageAdapter(uriList, binding.root.context)
        recyclerView.adapter = imageAdapter
        recyclerView.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}