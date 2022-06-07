package kr.tangram.smartgym.data.remote.model

data class UserExistsResult(
    val result: ResultExists
)

data class ResultExists(
    val resultCode: Int,
    val resultMsg: String,
    val userCnt: Int
)