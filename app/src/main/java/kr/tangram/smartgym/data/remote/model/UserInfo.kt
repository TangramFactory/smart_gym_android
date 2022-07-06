package kr.tangram.smartgym.data.remote.model

import java.io.Serializable


data class UserInfo(
    val userUid: String,
    val userEmail: String,
    val userName: String,
    val userGender: String?,
    val userBirthday: String?,
    val userNickname: String,
    val userIntroduce: String?,
    val userHwUnit: String? = "0",
    val userHeight: String? = "0",
    val userWeight: Int? = 0,
    val userDailyGoal: Int?=0,
    val userCountry: String?,
    val userLanguage: String?,
    val userTimezone: String?,
    val userTimezoneOffset: Int?,
    val userJuniorYn: String?,
    val userParentsUid: String?,
    val insertDt: String?,
    val updateDt: String?,
) : Serializable {

    fun getHeightFt() = if (userHeight?.contains("\'") == true) userHeight.split("\'")[0]
    else {
        if (!userHeight.isNullOrEmpty()){
            var inches = userHeight.toFloat()/2.54
            var feet = (inches/12).toInt()

            feet.toString()
        }else{
            ""
        }
    }
    fun getHeightIn() = if (userHeight?.contains("\'") == true) userHeight.split("\'")[1] else {
        if (!userHeight.isNullOrEmpty()){
            var inches = userHeight.toFloat()/2.54
            var feet = (inches/12).toInt()
            inches -= 12 * feet

            String.format("%.2f", inches)
        }else{
            ""
        }
    }
    fun getHeightCm() = if (userHeight?.contains("\'") == false) userHeight else {
        if (!userHeight.isNullOrEmpty()) {
            var cm = getHeightFt().toFloat() * 30.48 + getHeightIn().toFloat() + 2.54

            String.format("%.2f", cm)
        }else{
            ""
        }
    }
}