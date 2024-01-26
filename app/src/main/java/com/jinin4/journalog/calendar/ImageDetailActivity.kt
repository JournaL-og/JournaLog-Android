package com.jinin4.journalog.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.net.toUri
import com.jinin4.journalog.R

class ImageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

        val imageView = findViewById<ImageView>(R.id.imageView)

        // 전달된 이미지 리소스 ID를 가져옴
        val imageResourceId = intent.getStringExtra("imageResourceId")

        // 이미지 뷰에 이미지 설정
        imageView.setImageURI(imageResourceId!!.toUri())
    }
}