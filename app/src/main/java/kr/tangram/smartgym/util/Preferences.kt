package kr.tangram.smartgym.util

import android.content.Context
import android.content.SharedPreferences
import kr.tangram.smartgym.base.BaseApplication
import org.json.JSONArray
import org.json.JSONObject

object Preferences {

    private val tag: String = javaClass.simpleName
    private var pref: SharedPreferences = BaseApplication.context.getSharedPreferences(null, Context.MODE_PRIVATE)
    private var edit = pref.edit()

    private fun editor(bind: SharedPreferences.Editor.() -> Unit) {
        edit.bind()
        edit.apply()
//        try {
//            if (pref != null) {
//                val edit = pref.edit()
//                edit.bind()
//                edit.apply()
//            } else {
//                log(tag, "SharedPreferences is null. 1")
//            }
//        } catch (e: Exception) {
//            log(tag, "$e")
//            /**
//             * 간혹 타입이 다른 경우 에러가 발생하므로 예외처리 추가.
//             * 동일한 key 에 저장되는 데이터의 타입이 다른 경우 발생하는 문제
//             * 이전 버전에서 사용중이였으나 삭제 된 경우 추적이 힘들기 때문에 예외처리를 하는것이 좋음.
//             */
//        }
    }

    // 프리퍼런스 저장데이터 모두 삭제 >>>>> LOGOUT
    fun clear() = editor { clear() }

    fun clear(k: String) = editor { remove(k) }

    fun set(k: String, v: Boolean) = editor { putBoolean(k, v) }

    fun set(k: String, v: String) = editor { putString(k, v) }

    fun set(k: String, v: Int) = editor { putInt(k, v) }

    fun set(k: String, v: Long) = editor { putLong(k, v) }

    fun set(k: String, v: Float) = editor { putFloat(k, v) }

    fun set(k: String, v: JSONObject?) {
        if (v != null) editor { putString(k, v.toString()) }
    }

    fun set(k: String, v: JSONArray?) {
        if (v != null) editor { putString(k, v.toString()) }
    }

    //===================================================================//
    private fun <V> getValue(bind: SharedPreferences.() -> V): V {
        return pref.bind()
//        return if (pref != null) {
//            pref.bind()
//        } else {
//            log(tag, "SharedPreferences is null. 2")
//            null
//        }
    }

    fun getBoolean(k: String): Boolean = getValue { getBoolean(k, false) }

    fun get(k: String):String = getValue { getString(k,"").toString() }

    fun getInt(k: String):Int = getValue { getInt(k, 0) }

    fun getLong(k: String):Long = getValue { getLong(k, 0) }

    fun getFloat(k: String):Float = getValue { getFloat(k, 0f) }

    fun getJson(k: String): JSONObject {
        val getString = getValue { getString(k, "{}") }
        return if (getString != null) JSONObject(getString) else JSONObject()
    }

    fun getJsonArray(k: String): JSONArray {
        val getString = getValue { getString(k, "[]") }
        return if (getString != null) JSONArray(getString) else JSONArray()
    }

}