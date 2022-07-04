package kr.tangram.smartgym.data.remote.response


open  class BaseResponse {
    open val result: Result? = null

    class Result{
        val resultCode: Int? = 0
        val resultMsg: String? = null
    }
}

