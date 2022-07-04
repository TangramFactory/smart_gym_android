package kr.tangram.smartgym.data.domain.model

import java.io.Serializable

data class GymInfo(
    val gymName : String,
    val address : String,
    val engagementUser : Int,
    val distance : Float? = null,
    val gymImageUrl : String? = null,
    val gymMarkUrl : String? =null
):Serializable
