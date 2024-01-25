package com.jinin4.journalog.db.photo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// 이상원 작성 - 24.01.23
@Dao
interface MemoPhotoDao {

//    @Query("select photo_id,memo_id,photo_url from photo")
//    fun getAllPhotos(): List<PhotoEntity>
//
//    @Query("select photo_id,memo_id,photo_url from photo where memo_id = :memo_id")
//    fun getPhotoById(memo_id: Int): List<PhotoEntity>

    @Insert
    fun insertMemoPhoto(memoPhoto: MemoPhotoEntity)

    @Delete
    fun deletePhoto(memoPhoto: MemoPhotoEntity)
    @Query("delete from Memo_Photo where memo_id = :memo_id")
    fun deleteMemoPhotoByMemoId(memo_id: Int)

    @Query("delete from Memo_Photo where memo_id = :memo_id and photo_id = :photo_id")
    fun deleteMemoPhotoByMemoIdAndPhotoId(memo_id: Int, photo_id:Int)
    @Update
    fun updatePhoto(memoPhoto: MemoPhotoEntity)
}