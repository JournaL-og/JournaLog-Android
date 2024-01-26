package com.jinin4.journalog.db.photo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// 이지윤 작성 - 24.01.22, // 이상원 24-01-25 추가
@Dao
interface PhotoDao {

    @Query("select photo_id,memo_id,photo_url,photo_uri from photo")
    fun getAllPhotos(): List<PhotoEntity>

    @Query("select photo_id,memo_id,photo_url,photo_uri from photo where memo_id = :memo_id")
    fun getPhotoByMemoId(memo_id: Int): List<PhotoEntity>

    @Query("select photo_uri from photo where memo_id = :memo_id")
    fun getUrisByMemoId(memo_id: Int): List<String>

    @Query("SELECT photo_id FROM photo WHERE memo_id = :memoId AND photo_uri = :uri LIMIT 1")
    fun getPhotoIdByMemoIdAndUri(memoId: Int, uri: String): Int?

    @Insert
    fun insertPhoto(photo: PhotoEntity): Long

    @Query("SELECT photo_url FROM Photo WHERE memo_id = :memoId")
    fun getPhotoUrlsByMemoId(memoId: Int): List<String>

    @Delete
    fun deletePhoto(photo: PhotoEntity)

    @Query("delete from photo where memo_id = :memo_id")
    fun deletePhotoByMemoId(memo_id: Int)

    @Query("delete from photo where memo_id= :memo_id and photo_uri = :uri")
    fun deletePhotoByMemoIdAndUri(memo_id: Int, uri: String)

    @Update
    fun updatePhoto(photo: PhotoEntity)
}