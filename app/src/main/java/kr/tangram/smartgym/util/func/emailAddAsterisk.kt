package com.tangramfactory.smartrope.common.func

fun emailAddAsterisk (emailString:String): String {
    var email = emailString
    val emailArray = email.split("@")
    if(emailArray.count()>1) {
        var id = emailArray.get(0)
        val domain = emailArray.get(1)
        if (id.length > 7) id = id.substring(0, id.length - 4) + "****"
        if (id.length > 3) id = id.substring(0, id.length - 3) + "***"
        if (id.length > 2) id = id.substring(0, id.length - 2) + "**"
        if (id.length > 1) id = id.substring(0, id.length - 1) + "*"
        if (id.length == 1) id = "*"
        email = "$id@$domain"
    } else {
        // 서버에서 이메일 주소가 오지 않을 경우 오류 !!
        email = ""
    }
    return email
}
