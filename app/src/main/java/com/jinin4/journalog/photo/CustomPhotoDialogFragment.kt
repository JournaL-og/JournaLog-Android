package com.jinin4.journalog.photo


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.jinin4.journalog.R
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.db.photo.PhotoEntity


// 이지윤 작성 - 24.01.26
class CustomPhotoDialogFragment(private val photoList: MutableList<PhotoEntity>, private val memoList:  MutableList<MemoEntity>, private val position: Int) : DialogFragment() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_photo_dialog, container, false)

        val statusBarHeight = getStatusBarHeight()
        val navigationBarHeight = getNavigationBarHeight()

        // FrameLayout 찾기 및 상단 패딩 설정
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)
        constraintLayout.setPadding(0, statusBarHeight, 0, navigationBarHeight)

        // 날짜 텍스트
        val dateTextView = view.findViewById<TextView>(R.id.tvDate)
        dateTextView.text = memoList[position].timestamp
        // 추가 텍스트
        val additionalTextView = view.findViewById<TextView>(R.id.tvAdditionalText)
        additionalTextView.text = memoList[position].content
        // 이미지 뷰 설정
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        viewPager.adapter = ImageSliderAdapter(requireContext(), photoList)

        // 필요한 경우, 현재 선택된 이미지 위치로 초기 스크롤
        viewPager.setCurrentItem(position, false)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 날짜와 텍스트 업데이트
                dateTextView.text = memoList[position].timestamp
                additionalTextView.text = memoList[position].content
            }
        })

        val closeButton = view.findViewById<ImageButton>(R.id.btnClose)
        closeButton.setOnClickListener { dismiss() }

        // 이지윤 - 24.01.26 공유하기 기능 추가
        val btnShare = view.findViewById<ImageButton>(R.id.btnShare)
        btnShare.setOnClickListener {
            val currentPosition = viewPager.currentItem
            val currentImageUri = photoList[currentPosition].photo_uri.toUri()

            Log.d("share currentImageUri",currentImageUri.toString())

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, currentImageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
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
