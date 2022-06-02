package kr.tangram.smartgym.util

import org.json.JSONException
import org.json.JSONObject

class AppSetting {
    companion object {

        //
        private val tag = "AppSetting"
        private var jsonData = JSONObject()

        //
        var notification_widget = true
            get() {
                field = try {
                    jsonData.getBoolean("notification_widget")
                } catch (e:JSONException) {
                    //loge(tag,". "+e)
                    true
                }
                return field
            }
            set(value) {
                jsonData.put("notification_widget",value)
//                Preferences.set("appsetting", jsonData)
                field = value
            }

        //
        var notification = true
            get() {
                field = try {
                    jsonData.getBoolean("notification")
                } catch (e:JSONException) {
                    //loge(tag,"+ "+e)
                    true
                }
                return field
            }
            set(value) {
                jsonData.put("notification",value)
//                Preferences.set("appsetting", jsonData)
                field = value
            }

        var sound_connect = 20
            get() {
                try {
                    jsonData.getInt("sound_connect")
                } catch (e:JSONException) {
                    //loge(tag,". "+e)
                    20
                }.also { field = it }
                return field
            }
            set(value) {
                jsonData.put("sound_connect",value)
//                Preferences.set("appsetting", jsonData)
                field = value
            }


        var sound_jump = 20
            get() {
                try {
                    jsonData.getInt("sound_jump")
                } catch (e:JSONException) {
                    20
                }.also { field = it }
                return field
            }
            set(value) {
                jsonData.put("sound_jump",value)
//                Preferences.set("appsetting", jsonData)
                field = value
            }

        var sound_voicecount = 0
            get() {
                field = try {
                    jsonData.getInt("sound_voice")
                } catch (e:Exception) {
                    0
                }
                return field
            }
            set(value) {
                jsonData.put("sound_voice",value)
//                Preferences.set("appsetting", jsonData)
                field = value
            }

        var ble_powersave = 1
            get() {
                field = try {
                    jsonData.getInt("ble_powersave")
                } catch (e:Exception) {
                    //log(tag,"."+e)
                    jsonData.put("ble_powersave",field) // 기본값이 없으면
                    1
                }
                return field
            }
            set(value) {
                jsonData.put("ble_powersave",value)
//                Preferences.set("appsetting", jsonData)
                field = value
            }

        //
        init {
            jsonData = try {
                Preferences.getJson("appsetting")!!

            }catch (e:Exception) { JSONObject() }
        }

    }
}