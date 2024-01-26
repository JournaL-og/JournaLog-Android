package com.jinin4.journalog.photo


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.jinin4.journalog.R
import com.jinin4.journalog.db.memo.MemoEntity
import java.io.File
import kotlin.math.abs

class CustomPhotoDialogFragment(private val imageUri: Uri, private val memoInfo: MemoEntity) : DialogFragment() {

    private var touchStartY = 0f
    private var touchCurrentY = 0f
    private val minDragDistance = 350f // 최소 드래그 거리 (픽셀 단위)
    private val maxDragDistance = 700f // 최대 드래그 거리 (픽셀 단위)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_photo_dialog, container, false)

        val statusBarHeight = getStatusBarHeight()
        val navigationBarHeight = getNavigationBarHeight()

        // FrameLayout 찾기 및 상단 패딩 설정
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)
        constraintLayout.setPadding(0, statusBarHeight, 0, navigationBarHeight)

        Log.d("Firebase dialog url", imageUri.toString())
        // 이미지 뷰 설정
        val imageView = view.findViewById<ImageView>(R.id.ivCustomImage)
        Glide.with(this).load(imageUri).into(imageView)

        val closeButton = view.findViewById<ImageButton>(R.id.btnClose)
        closeButton.setOnClickListener { dismiss() }

        // 날짜 텍스트 설정
        val dateTextView = view.findViewById<TextView>(R.id.tvDate)
        dateTextView.text = memoInfo.timestamp

        // 추가 텍스트 설정
        val additionalTextView = view.findViewById<TextView>(R.id.tvAdditionalText)
        additionalTextView.text = memoInfo.content

        // 이지윤 - 24.01.26 공유하기 기능 추가
        val btnShare = view.findViewById<Button>(R.id.btnShare)
        btnShare.setOnClickListener {
            Log.d("share imageUri",imageUri.toString())

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        }

        imageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    touchCurrentY = event.rawY
                    val deltaY = touchCurrentY - touchStartY
                    val translationY = v.translationY + deltaY
                    v.translationY = translationY.coerceIn(-maxDragDistance, maxDragDistance)
                    touchStartY = touchCurrentY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (abs(v.translationY) > minDragDistance) {
                        dismiss() // 드래그 거리가 임계값을 초과하면 다이얼로그 닫기
                    } else {
                        resetDialogPosition(imageView) // 이미지 뷰를 원래 위치로 되돌림
                    }
                    true
                }
                else -> false
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BlackBackgroundDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.also { window ->
            // 다이얼로그가 전체 화면을 차지하도록 설정
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            // 화면 경계 제한 없이 드래그가 가능하도록 설정
            val params = window.attributes
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            window.attributes = params
        }
    }

    private fun resetDialogPosition(imageView: ImageView) {
        imageView.translationY = 0f // 이미지 뷰를 원래 위치로 되돌림
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun getNavigationBarHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }
}
