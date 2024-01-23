package com.jinin4.journalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.jinin4.journalog.databinding.FragmentPhotoBinding
import com.jinin4.journalog.databinding.FragmentTestBinding
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.firebase.storage.FirebaseFileManager
import net.developia.todolist.db.JournaLogDatabase
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


//이상원 - 24.01.19
class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = firebaseStorage.reference

    lateinit var db: JournaLogDatabase
    lateinit var memoDao: MemoDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestBinding.inflate(inflater, container, false)
        db = JournaLogDatabase.getInstance(binding.root.context)!!
        memoDao = db.getMemoDao()

        binding.btnCall.setOnClickListener {
            FirebaseFileManager.loadImageIntoImageView("photo/test.png", binding.imageFire)
        }
        binding.btnCallDb.setOnClickListener{
            val koreaTimeZone = ZoneId.of("Asia/Seoul")

            // 현재 시각 가져오기 (한국 시간 기준)
            val currentDateTime = LocalDateTime.now(koreaTimeZone)
            val currentDateTime2 = LocalDateTime.now(koreaTimeZone)
            // 출력 포맷 지정
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            // 포맷에 맞게 문자열로 변환
            val formattedDateTime = currentDateTime.format(formatter)

            // sqlite 에는 Date 타입이 없어 스트링으로 넣어야 함!! 참고하세요
            var memoEntity = MemoEntity(null, "아 아주 나른한 오후네요", formattedDateTime, 1)

            Thread{
                memoDao.insertMemo(memoEntity)
                requireActivity().runOnUiThread{
                    Toast.makeText(binding.root.context, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }.start()
        }
        //최성혁 - 24.01.22
        binding.btnDeleteDb.setOnClickListener {
            Thread{
                memoDao.deleteAllMemos()
            }.start()
        }

//        Thread{
//            val memo1 = memoDao.getMemoById(1)
//
//            requireActivity().runOnUiThread{
//                binding.testIdDe.text = memo1.content
//            }
//        }.start()






        return binding.root
    }
}