package kr.tangram.smartgym.data.remote.response

import kr.tangram.smartgym.data.remote.model.JumpToDay
import java.io.Serializable

class JumpLoadDayResponse : BaseResponse(){
    val resultList: List<JumpToDay>?=null
}

