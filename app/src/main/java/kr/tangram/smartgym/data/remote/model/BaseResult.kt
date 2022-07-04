package kr.tangram.smartgym.data.remote.model

data class BaseResult(
    val result: ResultMsg
)
data class ResultMsg(
    val resultCode: Int,
    val resultMsg: String
)



