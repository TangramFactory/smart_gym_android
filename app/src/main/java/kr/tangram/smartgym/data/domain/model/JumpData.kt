package kr.tangram.smartgym.data.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "jump_data")
data class JumpData (
    @SerializedName("wid") @ColumnInfo(name = "wid") val wid : String? = "",
    @SerializedName("did") @ColumnInfo(name = "did") val did : String? = "",
    @SerializedName("mid") @ColumnInfo(name = "mid") val mid : String? = "",
    @SerializedName("jump") @ColumnInfo(name = "jump") val jump : String? = "",
    @SerializedName("avg") @ColumnInfo(name = "avg") val avg : String? = "",
    @SerializedName("calorie") @ColumnInfo(name = "calorie") val calorie : String? = "",
    @SerializedName("duration") @ColumnInfo(name = "duration") val duration : String? = "",
    @SerializedName("finish") @ColumnInfo(name = "finish") val finish : String? = "",
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") @ColumnInfo(name = "id") var id : Int = 0

)