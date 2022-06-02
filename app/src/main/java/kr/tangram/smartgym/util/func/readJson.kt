package com.tangramfactory.smartrope.common.func

import android.content.res.Resources
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter

fun readJSON(resources:Resources,id:Int): JSONObject {
    val res = resources.openRawResource(id)
    val writer = StringWriter()
    val buffer = CharArray(1024)
//        is.use { is ->
    val reader = BufferedReader(InputStreamReader(res, "UTF-8"))
    var n: Int
    while (reader.read(buffer).also { n = it } != -1) {
        writer.write(buffer, 0, n)
    }
//        }
    return  JSONObject( writer.toString() )

}