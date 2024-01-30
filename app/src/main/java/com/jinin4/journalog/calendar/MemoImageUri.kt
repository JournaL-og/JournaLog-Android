package com.jinin4.journalog.calendar

import com.jinin4.journalog.db.memo.MemoEntity

data class MemoImageUri(
    val memoEntity: MemoEntity,
    val uris: List<String>
)