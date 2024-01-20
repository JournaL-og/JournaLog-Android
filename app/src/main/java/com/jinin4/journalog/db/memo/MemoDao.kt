package com.jinin4.journalog.db.memo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// 이상원 - 24.01.19
@Dao
interface MemoDao {
    @Query("select memo_id,content,timestamp,color_id from memo")
    fun getAllMemo(): List<MemoEntity>

    @Query("select memo_id,content,timestamp,color_id from memo where color_id = :color_id")
    fun getMemoById(color_id: Int): List<MemoEntity>

//    select date('now') as date,
//    time('now') as time,
//    datetime('now') as datetime;
//      date        time        datetime
//      ----------  ----------  -------------------
//      2019-11-06  15:03:33    2019-11-06 15:03:33

    @Insert
    fun insertMemo(memo: MemoEntity)

    @Delete
    fun deleteMemo(memo: MemoEntity)

    @Update
    fun updateMemo(memo: MemoEntity)
}