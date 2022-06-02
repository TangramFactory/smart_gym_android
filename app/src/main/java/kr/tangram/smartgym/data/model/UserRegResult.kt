package kr.tangram.smartgym.data.model

data class UserRegResult(
    val result: ResultReg
)
data class ResultReg(
    val resultCode: Int,
    val resultMsg: ResultMsg
)
data class ResultMsg(
    val code: String,
    val errno: Int,
    val index: Int,
    val sql: String,
    val sqlMessage: String,
    val sqlState: String
)

