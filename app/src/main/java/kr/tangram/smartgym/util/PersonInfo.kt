package kr.tangram.smartgym.util

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import com.tangramfactory.smartrope.common.func.SAuth
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PersonInfo {

    val tag:String = "PersonInfo"
    var personJson = JSONObject()
        set(value) {
            field = value
            //
            name = try {
                field.getString("name")
            } catch (e:JSONException) {""}

            gender = try {
                field.getInt("gender")
            } catch (e:JSONException) {0}

            birthday_year = try {
                field.getInt("birthday-year")
            } catch (e:JSONException) {1970}

            birthday_month = try {
                field.getInt("birthday-month")
            } catch (e:JSONException) {1}

            birthday_day = try {
                field.getInt("birthday-day")
            } catch (e:JSONException) {1}

            height = try {
                field.getString("height")
            } catch (e:JSONException) {"0"}

            height_unit = try {
                field.getInt("height-unit")
            } catch (e:JSONException) {0}

            weight = try {
                field.getInt("weight")
            } catch (e:JSONException) { 50}

            weight_unit = try {
                field.getInt("weight-unit")
            } catch (e:JSONException) {0}

            daily_goal = try {
                field.getInt("daily-goal")
            } catch (e:JSONException) {defaultGoal}

            update_time = try {
                field.getLong("update-time")
            } catch (e:JSONException) {0}

            profile_image_uid = try {
                field.getString("profile-image-uid")
            } catch (e:JSONException) {""}

            country = try {
                field.getString("country")
            } catch (e:JSONException) {""}

            timezone = try {
                field.getString("timezone")
            } catch (e:JSONException) {""}
        }


    // 기본 값
    var name:String = ""
        set(value) {
            field = value
            personJson.put("name",field)
        }
    var gender:Int = 0
        set(value) {
            field = value
            personJson.put("gender",field)
        }
    var birthday_year:Int = 1990
        set(value) {
            field = value
            personJson.put("birthday-year",field)
        }
    var birthday_month:Int = 1
        set(value) {
            field = value
            personJson.put("birthday-month",field)
        }
    var birthday_day:Int = 1
        set(value) {
            field = value
            personJson.put("birthday-day",field)
        }
    var height:String = "120"
        set(value) {
            field = value
            personJson.put("height",field)
        }
    var height_unit:Int = 0
        set(value) {
            field = value
            personJson.put("height-unit",field)
        }
    var weight:Int = 50
        set(value) {
            field = value
            personJson.put("weight",field)
        }
    var weight_unit:Int = 0
        set(value) {
            field = value
            personJson.put("weight-unit",field)
        }
    var daily_goal:Int = defaultGoal
        set(value) {
            field = value
            personJson.put("daily-goal",field)
        }
    var update_time:Long = 0
        set(value) {
            field = value
            personJson.put("update-time",field)
        }
    var profile_image_uid:String? = ""
        set(value) {
            field = value
            personJson.put("profile-image-uid",field)
        }
    var country = ""
        set(value) {
            field = value
            personJson.put("country",field)
        }
    var timezone = ""
        set(value) {
            field = value
            personJson.put("timezone",field)
        }

    // -- INIT
    init {

        log(tag,"init")

        // 이미 저장된 데이터가 있으면 적용하고 ..
//        val localPersonData = Preferences.getJson("personinfo")
//        if(localPersonData!=null) personJson = localPersonData

        log(tag,"init .. " + personJson )
    }

    //
    fun sync(func:(json:JSONObject?)->Unit) {

        loge(tag,"--------- INIT PERSON DATA - PersonInfo")

        // 로그인 되어 있는 경우 -- 서버에서 데이터를 가져와 업데이트 //
        //if (FirebaseAuth.getInstance().currentUser != null) {
        if(SAuth.logined) {

            var reload = true

            // 저장된 데이터가 있는 경우 // 60초 안에 새로 불러오지 않는다
            val saveTime = try {
                personJson.getLong("save_time")
            } catch (e:JSONException) { Calendar.getInstance().timeInMillis }
            val timeGap = ( Calendar.getInstance().timeInMillis - saveTime ) / 1000
            log(tag,"time gap " + timeGap )

            // -- 60초 제한
            if(timeGap in 1..60) reload = false

            //
            if(reload) {
                loadServer(){ json ->
                    log(tag," 서버에서 가져옴 .... " + json )
                    func(json)
                }
            } else {
                log(tag," 있던 값 사용 " + personJson )
                func(personJson)
            }
        }
    }

    //
    private fun updateLocal(jsonPerson:JSONObject) {
        log(tag,"saveLocal ..")

        // 받은 값을 그대로 일단 저장
        jsonPerson.put("save_time", Calendar.getInstance().timeInMillis )
//        Preferences.set("personinfo",jsonPerson)
        personJson = jsonPerson

        //
        log(tag,"name " + name )
//        log(tag,"saved data " +  Preferences.getJson("personinfo") )
    }
    //
    fun updateLocal() {
        updateLocal(personJson)
    }

    //
    fun reset() {
        // 저장된 데이터 삭제
        personJson = JSONObject()
//        Preferences.set("personinfo",personJson)
    }

    // -- sync to server
    fun loadServer( func:(json:JSONObject?)->Unit ) {
        log(tag,"loadServer")

        // 로그인하지 않아 보낼 값이 없는 경우 //
//        if( FirebaseAuth.getInstance().currentUser?.uid.isNullOrEmpty() ) func(null)
        if(!SAuth.logined) func(null)

        // 서버에서 개인 정보 받아 오기 //
        val sendJson = SAuth.makeJSON()
//        sendJson.put("email", FirebaseAuth.getInstance().currentUser?.email )
//        sendJson.put("fid", FirebaseAuth.getInstance().currentUser?.uid )

//        Transaction.getJson("PersonInfo",sendJson) { json ->
//
//            log(tag, "PersonInfo syncServer > " + json )
//
//            if(json?.getInt("result")==0) {
//                //
//                try {
//
//                    val personData = try {
//                        json.getJSONObject("person")
//                    } catch (e:JSONException) { JSONObject() }
//
//                    // 서버에서 불러온 개인 정보를 반영한다 //
//                    updateLocal(personData)
//
//                    // 데이터가 필요하면 반환
//                    func(personData)
//
//                } catch (e:Exception) {
//                    loge(tag,"person info json result error. - person not found.")
//                }
//
//            } else {
//                loge(tag,"get persion info error ..")
//            }
//
//        }

    }

    //
    fun saveServer(func:(json:JSONObject?)->Unit) {
        log(tag,"saveServer")

        // 서버에 데이터 저장
        val sendJson = JSONObject()

        // 불필요한 데이터 삭제
        val sendPersonJson = personJson
//        sendJson.put("fid", FirebaseAuth.getInstance().currentUser?.uid )
//        sendJson.put("email", FirebaseAuth.getInstance().currentUser?.email )
        sendJson.put("fid", SAuth.uid)
        sendJson.put("email", SAuth.email)

        sendPersonJson.remove("save_time")
        sendJson.putOpt("person",sendPersonJson)


        log(tag,"SEND JSON : " + sendJson.toString() )

        //
//        Transaction.getJson("PersonInfoUpdate",sendJson) { json ->
//            when(json?.getInt("result")){
//                0->{
//                    func(json)
//                }
//                else->{
//                    func(json)
//                }
//            }
//        }
    }




}