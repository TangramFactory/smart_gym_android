package kr.tangram.smartgym.util

import android.provider.Settings
import kr.tangram.smartgym.base.BaseApplication
import java.math.BigInteger


fun isHex(hex: String): Int {
    return try {
        val value : BigInteger? = BigInteger(hex, 16)
        value!!.toInt()
    } catch (e: NumberFormatException) {
        0
    }
}

fun calCalorieHistory(time_gap: Long, weight_kg: Float): Float {
    // 기준치에 맞는 경우 정상적으로 계산
    // time gap 이 기준치에 맞지 않는 경우 일반적인 경우의 수로 계산
    // 스마트로프 기기에서 받아온 시간이기 때문에 옳지 않을 수가 있다
    if (time_gap in 1..3000) {
        return calCalorie(time_gap, weight_kg)
    }
    return calCalorie(400, weight_kg)
}


fun calCalorie(time_gap: Long, weight_kg: Float): Float {
    var oneCalorie = 0.0f

    // 어디서 왔는지 모르겠지만 기존 펌웨어의 공식을 대입하기로 함!!!
    if (time_gap in 1..3000) {
        val minRPM = 60000.0f / time_gap

        val avrCalorie = when {
            minRPM < 70 -> {
                0.074f
            }
            minRPM < 90 -> {
                0.075f
            }
            minRPM < 110 -> {
                0.077f
            }
            minRPM < 125 -> {
                0.080f
            }
            else -> {
                0.085f
            }
        }

        // LBS로 계산되기 때문에 KG기준 > LBS기준으로 바꿈
        oneCalorie = (weight_kg / 0.45359237f) * avrCalorie / 60.0f / 1000.0f * time_gap
    }
    return oneCalorie
}


fun getAndroidId(): String {
    return Settings.Secure.getString(BaseApplication.context.getContentResolver(), Settings.Secure.ANDROID_ID)
}