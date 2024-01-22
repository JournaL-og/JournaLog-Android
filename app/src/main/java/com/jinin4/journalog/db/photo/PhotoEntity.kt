package com.jinin4.journalog.db.photo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//이지윤 작성 - 24.01.22
@Entity(tableName = "Photo")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) var photo_id : Int? = null,
    @ColumnInfo(name="memo_id") val memo_id : Int,
    @ColumnInfo(name="photo_url") val photo_url : String
)