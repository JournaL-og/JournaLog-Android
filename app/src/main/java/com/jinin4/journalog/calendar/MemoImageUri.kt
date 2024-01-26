package com.jinin4.journalog.calendar

import android.net.Uri
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.db.photo.PhotoEntity

data class MemoImageUri(
    val memoEntity: MemoEntity,
    val uris: List<String>
)