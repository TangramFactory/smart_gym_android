package kr.tangram.smartgym.data.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "user_email_cache")
data class UserEmailCache(
    @SerializedName("email") @ColumnInfo(name = "email") var email : String?
){
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") @ColumnInfo(name = "id") var id : Int = 0
}