package com.jinin4.journalog.calendar

import com.jinin4.journalog.db.memo.MemoEntity


// 이상원 24.01.22
data class MemoImageUri(
    val memoEntity: MemoEntity,
    val uris: List<String>
)