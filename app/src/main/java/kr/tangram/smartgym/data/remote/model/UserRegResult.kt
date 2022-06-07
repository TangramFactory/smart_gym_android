package kr.tangram.smartgym.data.remote.model

data class UserRegResult(
    val result: ResultReg
)
data class ResultReg(
    val resultCode: Int,
    val resultMsg: String
)



//{
//    "result": {
//    "resultCode": -1,
//    "resultMsg": {
//        "code": "ER_DUP_ENTRY",
//        "errno": 1062,
//        "sqlMessage": "Duplicate entry 'UP5sqdnvddUPJjR8qXnCPyBwqza2-bwh@tangram.kr' for key 'TB_USER.PRIMARY'",
//        "sqlState": "23000",
//        "index": 0,
//        "sql": "INSERT INTO\n  TB_USER\nVALUES\n  (\n    'UP5sqdnvddUPJjR8qXnCPyBwqza2',\n    'bwh@tangram.kr',\n    'bwh@tangram.kr',\n    CURRENT_TIMESTAMP,\n    0,\n    0,\n    0,\n    0,\n    0,\n    0,\n    'KR',\n    'America/New_York',\n    CURRENT_TIMESTAMP,\n    CURRENT_TIMESTAMP\n  )"
//    }
//}
//}
