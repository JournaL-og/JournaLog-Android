package com.jinin4.journalog.db.memo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// 이상원 - 24.01.19
@Dao
interface MemoDao {
    @Query("select memo_id,content,timestamp,color_id from memo order by timestamp")
    fun getAllMemoAsc(): List<MemoEntity>
    @Query("select memo_id,content,timestamp,color_id from memo order by timestamp desc")
    fun getAllMemo(): List<MemoEntity>
    @Query("select memo_id,content,timestamp,color_id from memo where memo_id = :id")
    fun getMemoById(id: Int): MemoEntity
    // 최성혁 수정 - 24.01.22
    @Query("select memo_id,content,timestamp,color_id from memo where color_id = :id order by timestamp")
    fun getMemosByColorIdAsc(id: Int): List<MemoEntity>
    @Query("select memo_id,content,timestamp,color_id from memo where color_id = :id order by timestamp desc")
    fun getMemosByColorId(id: Int): List<MemoEntity>


    @Query("SELECT memo_id, content, strftime('%H:%M', timestamp) as timestamp, color_id FROM memo WHERE date(timestamp) = :timestamp order by timestamp desc")
    fun getMemoByTimestamp(timestamp: String): List<MemoEntity>
    @Query("SELECT memo_id, content, strftime('%H:%M', timestamp) as timestamp, color_id FROM memo WHERE date(timestamp) = :timestamp order by timestamp")
    fun getMemoByTimestampAsc(timestamp: String): List<MemoEntity>
    @Query("SELECT DISTINCT date(timestamp) AS timestamp FROM memo")
    fun getDistinctCalendarDays(): List<String>

    @Insert
    fun insertMemo(memo: MemoEntity): Long

    @Delete
    fun deleteMemo(memo: MemoEntity)

    // 최성혁 - 24.01.22
    @Query("DELETE FROM memo")
    fun deleteAllMemos()

    @Query("Delete from memo where memo_id = :id")
    fun deleteMemoByMemoId(id : Int)

    @Update
    fun updateMemo(memo: MemoEntity)
}