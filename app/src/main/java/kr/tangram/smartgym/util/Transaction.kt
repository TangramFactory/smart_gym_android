package kr.tangram.smartgym.util


import com.github.kittinunf.fuel.Fuel
import com.tangramfactory.smartrope.common.func.SAuth
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseApplication
import kr.tangram.smartgym.util.func.Frank
import org.json.JSONException
import org.json.JSONObject

abstract class Transaction {

    companion object {

        private val tag: String = "Transaction"
        private var transactionCounter = 0
        private var transactionTimerOver = 2 // ==> 서버에 N 번까지 Call ..!

        //
        fun getJson(command: String, json: JSONObject, onComplete: (json: JSONObject) -> Unit) {
            transactionCounter = 0
            transaction(command, json, onComplete)
        }

        //
        private fun transaction(
            command: String,
            json: JSONObject,
            onComplete: (json: JSONObject) -> Unit
        ) {

            log(tag, "transaction send : " + command + " / " + json)

            //
            transactionCounter++

            //
            val headerMap: MutableMap<String, String> = hashMapOf()
            headerMap["Content-Type"] = "application/json"

            // -- 중국서버 !!
//            SAuth.type = SAuth.AUTH_TYPE.CHINA
            log(tag, "server : " + FireStoreServer + command)
            if (SAuth.type == SAuth.AUTH_TYPE.CHINA && SAuth.token != null) headerMap["smartrope-token"] =
                SAuth.token.toString()

            //
            Fuel.post(FireStoreServer + command).body(json.toString()).header(headerMap)
                .responseString { request, response, result ->
                    //Fuel.post(FBStoreServer+command).body(json.toString()).header("Content-Type" to "application/json").responseString { request, response, result ->

                    //
                    result.fold(success = { jsonString ->

                        log(tag, "transaction receive : " + command + " / " + jsonString)

                        var receiveJson: JSONObject
                        try {
                            receiveJson = JSONObject(jsonString)
                        } catch (e: JSONException) {
                            receiveJson = JSONObject("{ success:false, result:1 }")

                            // JSON 파일을 올바로 받지 못해서, PARSE가 안되는 경우
                            // Google 서버에 접근이 되지 않아서 이상한 HTML 페이지가 불러지는 경우 > 파싱 오류 발생
                            //Toast.makeText( App.context , "Sorry! Internal Server Error. (1) ", Toast.LENGTH_LONG  ).show()
                            Frank.show(BaseApplication.context.getString(R.string.error_network_connection) + " (1)")

                        }
                        onComplete(receiveJson)

                    }, failure = { error ->

                        loge(tag, "failure > " + error.toString() + " / " + transactionCounter)

                        if (transactionCounter >= transactionTimerOver) { //****

                            if (error.toString().contains("500 Internal Server Error")) {

                                // 사용자 데이터가 없는 경우 500 에러가 발생합니다.
                                // 신규로 가입한 경우 서버에 데이터가 없기 때문에 이 에러가 발생!!
                                if (SAuth.logined) Frank.show(BaseApplication.context.getString(R.string.error_network_connection) + " (500)")

                            } else {

                                // 로그인 했는데 서버가 응답하지 않는 경우에
                                if (SAuth.logined) Frank.show(BaseApplication.context.getString(R.string.error_network_connection) + " (2)")

                            }

                        } else {

                            // 다시 시도 //
                            android.os.Handler().postDelayed({
                                log(tag, "SERVER ERROR ... " + transactionCounter)
                                transaction(command, json, onComplete)
                            }, 20)

                        }

                    })
                }
        }
    }
}