package com.jinin4.journalog.firebase.storage


import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask


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


    // TODO:기타 파일 업로드, 다운로드 등의 함수추가피료함

}