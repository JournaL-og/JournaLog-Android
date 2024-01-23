package com.jinin4.journalog.db.photo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 이상원 작성 - 24.01.23
@Entity(tableName = "Memo_Photo")
data class MemoPhotoEntity(
    @PrimaryKey(autoGenerate = true) var memoPhoto_id : Int? = null,
    @ColumnInfo(name="memo_id") val memo_id : Int,
    @ColumnInfo(name="photo_id") val photo_id : Int
)