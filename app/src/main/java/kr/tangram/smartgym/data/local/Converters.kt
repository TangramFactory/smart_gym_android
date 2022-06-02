package kr.tangram.smartgym.data.local

import androidx.room.TypeConverter
import kr.tangram.smartgym.data.domain.model.TableName
import kr.tangram.smartgym.data.domain.type.TableNameType

class Converters {

    companion object {

        @TypeConverter
        @JvmStatic
        fun fromTableNameType(tableNameType: TableName?) = tableNameType?.name

        @TypeConverter
        @JvmStatic
        fun toTableNameType(tableNameType: String?): TableNameType? = tableNameType?.let(TableNameType::valueOf)
        
    }
}