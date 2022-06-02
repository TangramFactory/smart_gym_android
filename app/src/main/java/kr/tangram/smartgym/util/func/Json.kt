package com.tangramfactory.smartrope.common.func

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class Json {
    companion object {
        // JSON 값가져오기
        fun String(json: JSONObject, string:String, default:String = "" ):String {
            return try {
                json.getString(string)
            } catch (e: JSONException) {default}
        }
        fun Int(json: JSONObject, string:String, default:Int = 0 ):Int {
            return try {
                json.getInt(string)
            } catch (e: JSONException) {default}
        }
        fun Array(json: JSONObject, string:String, default: JSONArray = JSONArray()): JSONArray {
            return try {
                json.getJSONArray(string)
            } catch (e: JSONException) {default}
        }
        fun Long(json: JSONObject, string:String, default:Long = 0L):Long {
            return try {
                json.getLong(string)
            } catch (e: JSONException) {default}
        }
        fun Boolean(json: JSONObject, string:String, default:Boolean = false):Boolean {
            return try {
                json.getBoolean(string)
            } catch (e: JSONException) {default}
        }
        fun Object(json: JSONObject, string:String, default: JSONObject = JSONObject()): JSONObject {
            return try {
                json.getJSONObject(string)
            } catch (e: JSONException) {default}
        }
    }
}