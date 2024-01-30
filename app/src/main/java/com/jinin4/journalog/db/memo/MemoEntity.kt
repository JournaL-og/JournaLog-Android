package com.jinin4.journalog.db.memo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 이상원 - 24.01.19
@Entity(tableName = "Memo") // tableName 안주면 클래스 이름으로 테이블 생성
data class MemoEntity(
    @PrimaryKey(autoGenerate = true) var memo_id : Int? = null,
    @ColumnInfo(name="content") val content : String,
    @ColumnInfo(name="timestamp") val timestamp : String,
    @ColumnInfo(name="color_id") val color_id : Int
)
