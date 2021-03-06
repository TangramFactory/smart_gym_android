package kr.tangram.smartgym.data.remote.model

import java.io.Serializable

data class JumpToDay(
    val userUid: String,
    val jumpCount: Int,
    val jumpSet: Int,
    val jumpAvg: Int?,
    val jumpCalorie: Int?,
    val jumpDuration: Int,
    val insertDt: String,
    val updateDt: String,
): Serializable
