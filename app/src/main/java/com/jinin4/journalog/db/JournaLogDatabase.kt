package net.developia.todolist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
// 이상원 - 24.01.19
@Database(entities = [MemoEntity::class], version = 1)
abstract class JournaLogDatabase : RoomDatabase() {
    abstract fun getMemoDao(): MemoDao

    companion object {
        val databaseName = "journalog"
        var journaLogDatabase: JournaLogDatabase? = null

        fun getInstance(context: Context): JournaLogDatabase? {
            if (journaLogDatabase == null) {
                journaLogDatabase = Room.databaseBuilder(
                    context, JournaLogDatabase::class.java, databaseName).build()
            }
            return journaLogDatabase
        }
    }
}