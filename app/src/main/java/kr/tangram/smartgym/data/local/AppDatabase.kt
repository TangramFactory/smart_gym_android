package kr.tangram.smartgym.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.tangram.smartgym.data.local.dao.DeviceRegisterDao
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.data.domain.model.JumpData
import kr.tangram.smartgym.data.local.dao.JumDataDao

@Database(entities = [DeviceRegister::class, JumpData::class],  version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceRegisterDAO(): DeviceRegisterDao
    abstract fun jumpDataDAO(): JumDataDao

}