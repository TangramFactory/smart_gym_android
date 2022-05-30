package kr.tangram.smartgym.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.tangram.smartgym.data.local.dao.TableNameDao
import kr.tangram.smartgym.data.domain.model.TableName

@Database(entities = [TableName::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tableNameDAO(): TableNameDao
}