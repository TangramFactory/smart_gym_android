package kr.tangram.smartgym.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.tangram.smartgym.data.local.dao.DeviceRegisterDao
import kr.tangram.smartgym.data.domain.model.DeviceRegister

@Database(entities = [DeviceRegister::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceRegisterDAO(): DeviceRegisterDao

}