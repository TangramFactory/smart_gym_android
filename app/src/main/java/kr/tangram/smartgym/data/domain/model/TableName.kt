package kr.tangram.smartgym.data.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "table_name", primaryKeys = ["id"])
data class TableName(
    @SerializedName("id") @ColumnInfo(name = "id") val id : Int? = null,
    @SerializedName("name") @ColumnInfo(name = "name") val name : String? = null,
)
