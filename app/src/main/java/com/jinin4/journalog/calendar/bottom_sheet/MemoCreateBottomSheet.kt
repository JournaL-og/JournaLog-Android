package com.jinin4.journalog.calendar.bottom_sheet

import MultiImageAdapter
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.util.Log
import com.bumptech.glide.request.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.GridLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jinin4.journalog.Converter
import com.jinin4.journalog.MemoRightColorSetting
import com.jinin4.journalog.MemoRightColorSetting.getColorById
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
//import com.jinin4.journalog.photo.PhotoDataHolder
import com.prolificinteractive.materialcalendarview.CalendarDay
import net.developia.todolist.db.JournaLogDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


// 이상원 - 24.01.22
class MemoCreateBottomSheet(
    val selectedDate: CalendarDay,
    val callback: MemoInsertCallback,
    val memoEntity:MemoEntity?) : BottomSheetDialogFragment(), ImageUpdatedCallback {

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

    // 이지윤 24.01.26// 카메라로 사진을 찍을 때 사용할 파일 URI를 저장할 전역 변수
    private var currentPhotoUri: Uri? = null

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

        if (memoEntity != null) {
            val editableText: Editable =
                Editable.Factory.getInstance().newEditable(memoEntity.content)
            binding.edtContent.text = editableText
            binding.btnRemoveMemo.visibility = View.VISIBLE
            filterMemosByColorId(memoEntity.color_id)
            binding.txtTime.text = memoEntity.timestamp
            Thread{
                val listPhotoEntity = photoDao.getPhotoByMemoId(memoEntity.memo_id!!)
                for (photoEntity in listPhotoEntity) {
                    uriList.add(photoEntity.photo_uri.toUri())
                }
                updateAdapter()
            }.start()

        } else {
            filterMemosByColorId(1)
            val currentDateTime = LocalDateTime.now()
            val currTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            str_time = currentDateTime.format(currTimeFormatter)
            binding.txtTime.text = str_time
        }




        val formatterDate = org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        strDateTime = selectedDate.date.format(formatterDate).toString()

        binding.fabMemoInsert.setOnClickListener{
            if (memoEntity == null) {
                insertMemo()
            } else {
                updateMemo()
            }
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
        binding.btnRemoveMemo.setOnClickListener{
            deleteMemo(memoEntity!!)
        }


        binding.redBtn.setOnClickListener { filterMemosByColorId(1) }
        binding.orangeBtn.setOnClickListener { filterMemosByColorId(2) }
        binding.yellowBtn.setOnClickListener { filterMemosByColorId(3) }
        binding.greenBtn.setOnClickListener { filterMemosByColorId(4) }
        binding.blueBtn.setOnClickListener { filterMemosByColorId(5) }
        binding.indigoBtn.setOnClickListener { filterMemosByColorId(6) }
        binding.puppleBtn.setOnClickListener { filterMemosByColorId(7) }
//        binding.redBtn.performClick()
        return binding.root
    }




    private fun filterMemosByColorId(colorId:Int) {
        val layerDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.rounded_textview_background_content) as LayerDrawable
        val memoColor =  getColorById(colorId)
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
            editText.requestFocus(1)
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
                val insertMemoEntity = MemoEntity(null, edtCon, "${insertDate} ${insertTime}:00", insertedColorId)

                val insertedMemoId = memoDao.insertMemo(insertMemoEntity).toInt()
                if (uriList.isNotEmpty()) {
                    val pathList = uploadImageToFireBase(uriList)
                    for ((i, path) in pathList.withIndex()) {
                        val photoEntity = PhotoEntity(null,insertedMemoId,path,uriList[i].toString())
                        val insertedPhotoId = photoDao.insertPhoto(photoEntity).toInt()
                        val memoPhotoEntity = MemoPhotoEntity(null,insertedMemoId, insertedPhotoId)
                        memoPhotoDao.insertMemoPhoto(memoPhotoEntity)
//                        PhotoDataHolder.isDataChanged = true
                    }
                }
                requireActivity().runOnUiThread {
                    callback.onMemoInserted()
                    dismiss()
                }
            }.start()
        }
    }
    private fun deleteMemo(memoEntity: MemoEntity) {
        val rootContext = binding.root.context
        val builder: AlertDialog.Builder = AlertDialog.Builder(rootContext)
        builder.setTitle("메모 삭제")
        builder.setMessage("메모를 삭제하시겠습니까?")
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("네",
            object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    if (memoEntity != null) {
                        Thread {
                            memoDao.deleteMemo(memoEntity)
                            photoDao.deletePhotoByMemoId(memoEntity.memo_id!!)
                            memoPhotoDao.deleteMemoPhotoByMemoId(memoEntity.memo_id!!)
                            requireActivity().runOnUiThread {
                                callback.onMemoInserted()
                                dismiss()
                            }
                        }.start()
                    }
                }
            }
        )
        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            val textColor = ContextCompat.getColor(requireActivity(), getColorById(insertedColorId))
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(textColor)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
        }
        alertDialog.show()
    }

    private fun updateMemo() {
        val edtCon = binding.edtContent.text.toString()
        if (edtCon.isBlank() && uriList.isEmpty()) {
            requireActivity().runOnUiThread{
                showToast("메모나 사진을 추가해주세요.")
            }
            return
        }

        val updateDate = insertedDateTime ?: Converter().fromCalendarDay(selectedDate)
        val updateTime = binding.txtTime.text
        var updatedMemoEntity = MemoEntity(memoEntity!!.memo_id, edtCon,"${updateDate} ${updateTime}:00",insertedColorId)

        Thread{
            memoDao.updateMemo(updatedMemoEntity)
            if (uriList.isNotEmpty()) {
                // 원래 3장이었는데 1~2장 된 경우 -> photo에서 삭제만 해주면 됨(근데 다 삭제 후 새로운 거 일수도 있음)

                // 원래 3장이었는데 4장 이상이 된 경우 -> 인서트 해줘야댐(이것도 다삭제하고 새로운 4개일 수도,,)

                // 원래 3장이었는데 그대로 3장인 경우 -> uri가 기존과 같은지 비교 후 다르면 삭제, 인서트 다해줘

                // 근데 위의 3 경우를 모두 고려하기 귀찮으니까 그냥 모두 삭제 후 urilist에 있는거로 새로 인서트해줘
                photoDao.deletePhotoByMemoId(memoEntity.memo_id!!)
                memoPhotoDao.deleteMemoPhotoByMemoId(memoEntity.memo_id!!)
                val pathList = uploadImageToFireBase(uriList)
                for ((i, path) in pathList.withIndex()) {
                    val photoEntity =
                        PhotoEntity(null, memoEntity.memo_id!!, path, uriList[i].toString())
                    val insertedPhotoId = photoDao.insertPhoto(photoEntity).toInt()
                    val memoPhotoEntity =
                        MemoPhotoEntity(null, memoEntity.memo_id!!, insertedPhotoId)
                    memoPhotoDao.insertMemoPhoto(memoPhotoEntity)
                }
            } else {
                photoDao.deletePhotoByMemoId(memoEntity.memo_id!!)
                memoPhotoDao.deleteMemoPhotoByMemoId(memoEntity.memo_id!!)
            }
            requireActivity().runOnUiThread {
                callback.onMemoInserted()
                dismiss()
            }
        }.start()
    }

    override fun onMemoImageUpdated(position: Int) {
        uriList.removeAt(position)
        updateAdapter()
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

            // 이지윤: 섬네일 파일 업로드 추가 - 24.01.24
//            resizeImage(binding.root.context, uri, 150, 150,
//                onSuccess = { bitmap ->
//                    // 조정된 이미지 업로드
//                    FirebaseFileManager.uploadImage(
//                        bitmap,
//                        thumbnailPath,
//                        onSuccess = {
//
//                        },
//                        onFailure = { exception ->
//                            // 업로드 실패 시
//                            // TODO: 업로드 실패에 따른 추가 작업 수행
////                    showToast("이미지 업로드 실패: ${exception.message}")
//                        }
//                    )
//                },
//                onFailure = { exception ->
//                    // 오류 처리
//                }
//            )

        }
        return pathList

    }

    // 이지윤: 섬네일 파일 업로드 추가 - 24.01.24
    private fun resizeImage(context: Context, uri: Uri, width: Int, height: Int, onSuccess: (Bitmap) -> Unit, onFailure: (Exception) -> Unit) {
        Glide.with(context)
            .asBitmap() // Bitmap으로 로드
            .load(uri) // Uri로부터 이미지 로드
            .apply(RequestOptions().override(width, height)) // 이미지 크기 조정
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onSuccess(resource) // 조정된 이미지를 콜백으로 반환
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    onFailure(Exception("이미지 로드 실패"))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // 필요한 경우 여기에 정리 코드 작성
                }
            })
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

            // 이지윤,카메라로 찍은 사진 처리 로직 수정 - 24.01.26
            val photoFile: File = createImageFile() // 이미지 파일을 생성하는 메소드 필요
            currentPhotoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.jinin4.journalog.fileprovider",
                photoFile
            )

            // 이미 권한이 허용된 경우
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri) // 풀 해상도 이미지를 저장할 Uri 전달
            }
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

    // 이지윤,카메라로 찍은 사진 처리 로직 수정 - 24.01.26
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // 이미지 파일 이름 생성 (예: JPEG_20230101_120000_)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        // 앱의 사진 저장 디렉토리를 가져옴
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        // 빈 파일 생성
        return File.createTempFile(
            imageFileName, /* 접두사 */
            ".jpg", /* 접미사 */
            storageDir /* 디렉토리 */
        )
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
        // 이지윤,카메라로 찍은 사진 처리 로직 수정 - 24.01.26
        currentPhotoUri?.let { uri ->
            uriList.add(uri)
            updateAdapter()
        }
    }

//    content://com.jinin4.journalog.fileprovider/camera_images/camera_image.jpg

    /**
     * 이미지를 얻어 Uri로 변환
     */
    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val imageFile = File(requireContext().cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
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
        imageAdapter = MultiImageAdapter(uriList, binding.root.context, this)
        recyclerView.adapter = imageAdapter
//        recyclerView.layoutManager = GridLayoutManager(binding.root.context,2)
        recyclerView.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }



    @Deprecated("Deprecated in Java")
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
                    showToast("카메라 권한이 필요합니다.")

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