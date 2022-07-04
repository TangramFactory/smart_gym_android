package kr.tangram.smartgym.data.remote.model

data class JuniorResult(
    val result: JResult
)

data class JResult(
    val resultList: List<UserInfo>,
    val resultCode: Int,
    val resultMsg: String
)
