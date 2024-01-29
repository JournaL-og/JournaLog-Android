package net.developia.todolist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jinin4.journalog.Converter
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.db.photo.MemoPhotoDao
import com.jinin4.journalog.db.photo.MemoPhotoEntity
import com.jinin4.journalog.db.photo.PhotoDao
import com.jinin4.journalog.db.photo.PhotoEntity

// 이상원 - 24.01.19
@Database(entities = [MemoEntity::class, PhotoEntity::class, MemoPhotoEntity::class], version = 1)
@TypeConverters(Converter::class)
abstract class JournaLogDatabase : RoomDatabase() {
    abstract fun getMemoDao(): MemoDao
    // 이지윤 추가 - 24.01.22
    abstract fun getPhotoDao(): PhotoDao
    abstract fun getMemoPhotoDao(): MemoPhotoDao
    companion object {
        val databaseName = "journalog"
        var journaLogDatabase: JournaLogDatabase? = null

        fun getInstance(context: Context): JournaLogDatabase? {
            if (journaLogDatabase == null) {
                journaLogDatabase = Room.databaseBuilder(
                    context, JournaLogDatabase::class.java, databaseName)
                    .build()
            }
            return journaLogDatabase
        }
    }
}