package kr.tangram.smartgym.data.remote.request

import kr.tangram.smartgym.base.BaseApplication.Companion.kronosClock

data class JumpSaveObject(
    val uid : String,
    val works: List<JumpSaveData>
) {
    data class JumpSaveData (
        var row : String,
        var sdate : String,
        val wid : String,
        val did : String,
        val mid : String,
        val jump: String,
        val avg : String,
        val calorie: String,
        val duration : String,
        val finish : String,
        var checked : Boolean ? = true
    )
}