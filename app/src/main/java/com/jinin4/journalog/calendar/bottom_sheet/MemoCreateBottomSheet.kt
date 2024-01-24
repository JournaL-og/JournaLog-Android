package com.jinin4.journalog.calendar.bottom_sheet

import MultiImageAdapter
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


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


    var insertedDateTime: String? = null
    var insertedColorId: Int = 1
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val uriList = ArrayList<Uri>()

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
            openDatePicker()
        }
        binding.txtTime.setOnClickListener{
            showTimePicker()
        }

        binding.btnCamera.setOnClickListener{
            openCamera()
        }
        binding.btnGallery.setOnClickListener{
            openGallery()
        }


        binding.redBtn.setOnClickListener { filterMemosByColorId(1) }
        binding.orangeBtn.setOnClickListener { filterMemosByColorId(2) }
        binding.yellowBtn.setOnClickListener { filterMemosByColorId(3) }
        binding.greenBtn.setOnClickListener { filterMemosByColorId(4) }
        binding.blueBtn.setOnClickListener { filterMemosByColorId(5) }
        binding.indigoBtn.setOnClickListener { filterMemosByColorId(6) }
        binding.puppleBtn.setOnClickListener { filterMemosByColorId(7) }
        binding.redBtn.performClick()
        return binding.root
    }
    private fun filterMemosByColorId(colorId:Int) {
        val layerDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.rounded_textview_background_content) as LayerDrawable
        val memoColor =  when (colorId) {
            1 -> R.color.red_sw
            2 -> R.color.orange_sw
            3 -> R.color.yellow_sw
            4 -> R.color.green_sw
            5 -> R.color.blue_sw
            6 -> R.color.navi_sw
            7 -> R.color.purple_sw
            else -> R.color.red_sw
        }
        binding.edtContent.background=
            MemoRightColorSetting.changeRightColor(
                ContextCompat.getColor(binding.root.context, memoColor),layerDrawable)
        insertedColorId = colorId
    }




    companion object {
        const val TAG = "BasicBottomModalSheet"
    }


    /**
     * bottom sheet 생성 후 300의 delay 후 키보드 생성 및 edittext로 focus
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editText = binding.edtContent
        editText.postDelayed({
            editText.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 300)
    }

    /**
     * 메모 db에 insert
     * 사진이 있다면 사진도 insert
     * insert 성공되면 callback줘서 메인쓰레드가 메모가 추가되었음을 알림
     */
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

                val insertDate = insertedDateTime ?: strDateTime
                val insertTime = binding.txtTime.text
                val memoEntity = MemoEntity(null, edtCon, "${insertDate} ${insertTime}:00", insertedColorId)

                // 일단 컬러 id 1로 넣음
                val insertedMemoId = memoDao.insertMemo(memoEntity).toInt()
                if (uriList.isNotEmpty()) {
                    val pathList = uploadImageToFireBase(uriList)
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


    /**
     * 파이어베이스 storage에 이미지 추가
     */
    private fun uploadImageToFireBase(uriList: ArrayList<Uri>): ArrayList<String> {
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

                }
            )
        }
        return pathList

    }


    /****************************************************************************************************
     * 여기부터 bottom sheet 카메라, 이미지 처리 부분
     */

    /**
     * 액티비티 result를 감지, 성공 후 분기하여 카메라에서 받은 건지 갤러리에서 받은 건지에 따라 다르게 처리
     */
    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 처리할 로직
                val data: Intent? = result.data

                if (data != null && data.clipData != null) {
                    // 갤러리에서 가져온 경우
                    handleImageSelection(data)
                } else {
                    // 카메라로 찍은 경우
                    handleCameraImage(result)
                }
            } else {
//                Toast.makeText(binding.root.context, "이미지 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }


    /**
     * 사진첩 열기
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
//            type = MediaStore.Images.Media.CONTENT_TYPE //이미지 파일만 선택가능
            type = "image/*" // 동영상 등 모든 미디어 파일 가능
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        someActivityResultLauncher.launch(intent)
    }

    /**
     * 카메라 열기
     */
    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 이미 권한이 허용된 경우
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            someActivityResultLauncher.launch(intent)
        } else {
            // 권한이 없는 경우 권한을 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

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

    /**
     * 카메라에서 찍은 사진을 처리하는 로직

     */
    private fun handleCameraImage(result: ActivityResult) {
        val data: Intent? = result.data
        val takenImage: Bitmap? = data?.extras?.get("data") as? Bitmap
        if (takenImage != null) {
            val imageUri = getImageUriFromBitmap(takenImage)
            uriList.add(imageUri)
            updateAdapter()
        }
    }

    /**
     * 이미지를 얻어 Uri로 변환
     */
    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val imageFile = File(requireContext().cacheDir, "camera_image.jpg")
        imageFile.createNewFile()

        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()

        return FileProvider.getUriForFile(
            requireContext(),
            "com.jinin4.journalog.fileprovider",
            imageFile
        )
    }

    /**
     * 리사이클러 뷰 어댑터 업데이트
     */
    private fun updateAdapter() {
        val recyclerView = binding.rvImageSlide
        imageAdapter = MultiImageAdapter(uriList, binding.root.context)
        recyclerView.adapter = imageAdapter
        recyclerView.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 승인된 경우
                    openCamera()
                } else {
                    // 권한이 거부된 경우
                    Toast.makeText(
                        requireContext(),
                        "카메라 권한이 필요합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openDatePicker() {
        // 현재 날짜 가져오기
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            binding.root.context,
            R.style.datePickerDialogStyle,
            { view, year, month, dayOfMonth ->
                // 사용자가 날짜를 선택했을 때의 처리
                // year, month, dayOfMonth를 사용하여 작업 수행
                var strMonth = "${month + 1}"
                var strDay = "${dayOfMonth}"

                if (month < 9) {
                    strMonth = "0${month + 1}"
                }
                if (dayOfMonth < 10) {
                    strDay = "0${dayOfMonth}"
                }

                if (year == calendar.get(Calendar.YEAR)) {
                    binding.txtDate.text = "${strMonth}월 ${strDay}일"
                } else {
                    binding.txtDate.text = "${year}년 ${strMonth}월 ${strDay}일"
                }
                insertedDateTime = "${year}-${strMonth}-${strDay}"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
        val textColor = ContextCompat.getColor(requireActivity(), R.color.colorPrimary)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(textColor)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(textColor)
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            binding.root.context,
            R.style.datePickerDialogStyle,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                var strHour = "${hourOfDay}"
                var strMinute = "${minute}"

                if (hourOfDay < 10) {
                    strHour = "0${hourOfDay}"
                }
                if (minute < 10) {
                    strMinute = "0${minute}"
                }

                binding.txtTime.text = "$strHour:$strMinute"
            },
            currentHour,
            currentMinute,
            true // 24시간 형식 사용 여부
        )

        timePickerDialog.show()
        val textColor = ContextCompat.getColor(requireActivity(), R.color.colorPrimary)
        timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(textColor)
        timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(textColor)
    }

}