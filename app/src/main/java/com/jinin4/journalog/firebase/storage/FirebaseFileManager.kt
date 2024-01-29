package com.jinin4.journalog.firebase.storage


import android.net.Uri
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


//이상원 - 24.01.19
object FirebaseFileManager {

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = firebaseStorage.reference

    fun uploadImage(imageUri: Uri, storagePath: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val imagesRef: StorageReference = storageRef.child(storagePath)
        val uploadTask: UploadTask = imagesRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            // 업로드 성공 시 처리
            onSuccess.invoke()
        }.addOnFailureListener { exception ->
            // 업로드 실패 시 처리
            onFailure.invoke(exception)
        }
    }

    // 이지윤: uploadImage 오버로딩 - 24.01.24
    fun uploadImage(imageBitmap: Bitmap, storagePath: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val imagesRef: StorageReference = storageRef.child(storagePath)
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask: UploadTask = imagesRef.putBytes(data)

        uploadTask.addOnSuccessListener {
            // 업로드 성공 시 처리
            onSuccess.invoke()
        }.addOnFailureListener { exception ->
            // 업로드 실패 시 처리
            onFailure.invoke(exception)
        }
    }

    //이미지뷰에 스토어의 이미지 넣는 함수
    fun loadImageIntoImageView(imageRefPath: String, imageView: ImageView) {
        val imagesRef: StorageReference = storageRef.child(imageRefPath)
        imagesRef.downloadUrl.addOnSuccessListener { uri ->
            //이미지 뷰에 설정
            Glide.with(imageView.context)
                .load(uri)
                .into(imageView)
        }.addOnFailureListener {
            // 다운로드 실패 시 처리
            Toast.makeText(imageView.context, "이미지 불러오기 실패", Toast.LENGTH_SHORT).show()
        }
    }

    // 이지윤 작성 - 24.01.24
    fun loadImageFromFirebase(storagePath: String, onComplete: (MutableList<String>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)
            val urls = mutableListOf<String>()

            try {
                val listResult = storageRef.listAll().await() // Wait for listAll to complete
                Log.d("FirebaseFileManager", "Items found: ${listResult.items.size}")

                val deferreds = listResult.items.map { item ->
                    async {
                        try {
                            val url = item.downloadUrl.await().toString()
                            urls.add(url)
                            Log.d("FirebaseFileManager", "URL added: $url")
                        } catch (e: Exception) {
                            Log.e("FirebaseFileManager", "Failed to get download URL")
                        }
                    }
                }
                deferreds.awaitAll() // Wait for all async tasks to complete
                urls.sort()
                onComplete(urls)
            } catch (e: Exception) {
                Log.e("FirebaseFileManager", "Failed to list items: ${e.message}")
            }
        }
    }



    // TODO:기타 파일 업로드, 다운로드 등의 함수추가피료함

}