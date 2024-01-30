package com.jinin4.journalog.calendar

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.jinin4.journalog.R

class ImageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val imageResourceId = intent.getStringExtra("imageResourceId")
        imageView.setImageURI(imageResourceId!!.toUri())
    }
}