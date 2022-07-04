package kr.tangram.smartgym.data.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "device_register")
data class DeviceRegister (
    @SerializedName("device_sid") @ColumnInfo(name = "device_sid") val device_sid : String? = "",
    @SerializedName("auto") @ColumnInfo(name = "auto") val auto : Boolean? = false,
    @SerializedName("battery") @ColumnInfo(name = "battery") val battery : Int? = 0,
    @SerializedName("connect") @ColumnInfo(name = "connect") val connect : Boolean? = false,
    @SerializedName("date_connect") @ColumnInfo(name = "date_connect") val date_connect : String? = "",
    @SerializedName("date_regist") @ColumnInfo(name = "date_regist") val date_regist : String? = "",
    @SerializedName("identifier") @ColumnInfo(name = "identifier") val identifier : String? = "",
    @SerializedName("name") @ColumnInfo(name = "name") val name : String? = "",
    @SerializedName("option") @ColumnInfo(name = "option") val option : String? = "",
    @SerializedName("type") @ColumnInfo(name = "type") val type : String? = "",
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") @ColumnInfo(name = "id") var id : Int = 0
)
