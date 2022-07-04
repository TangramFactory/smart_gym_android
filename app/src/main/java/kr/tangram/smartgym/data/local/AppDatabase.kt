package kr.tangram.smartgym.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.tangram.smartgym.data.local.dao.DeviceRegisterDao
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.data.domain.model.UserEmailCache
import kr.tangram.smartgym.data.local.dao.UserEmailCacheDao

@Database(entities = [DeviceRegister::class, UserEmailCache::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceRegisterDAO(): DeviceRegisterDao
    abstract fun userEmailCacheDAO(): UserEmailCacheDao

}