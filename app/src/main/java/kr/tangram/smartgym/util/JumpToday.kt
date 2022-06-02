package kr.tangram.smartgym.util

import android.text.format.DateUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.tangramfactory.smartrope.common.func.SAuth
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class JumpToday {

    //
    class RopeCount {
        @SerializedName("jump")
        var jump: Int = 0

        @SerializedName("calorie")
        var calorie: Float = 0f

        @SerializedName("start")
        var start: Long = 0

        @SerializedName("end")
        var end: Long = 0

        @SerializedName("duration")
        var duration: Long = 0

        @SerializedName("goal")
        var goal: Int = 0

        @SerializedName("progress")
        var progress: Float = 0.0f

        @SerializedName("weight")
        var weight: Float = 0.0f
    }

    companion object {

        private const val tag = "JumpToday"

        var jumpToday = RopeCount()
        var jsonToday = JSONObject()

        //
        init {
            log(tag, "init")

            // 오늘의 줄넘기를 가져온다 >> 저장된 데이터가 오늘 것인가 ?
            if (Preferences.getJson("todayjump") != null) {

                log(tag, "load todayjump - complete ")

                val jsonObject = Preferences.getJson("todayjump")
                //log(tag, jsonObject.toString() )

                val jump = jsonObject?.optInt("jump", 0)

                if (jump != 0) {
                    // 데이터가 있다
                    // 있는데 오늘이 아니다?

                    val end = try {
                        jsonObject?.getLong("end")!! - getTimeOffSet()
                    } catch (e: JSONException) {
                        0L
                    }
                    //log(tag, "end " + " " + end )

                    //
                    if (DateUtils.isToday(end)) {

                        // 오늘이면
                        log(tag, "is today loaded : " + jsonObject.toString())

                        jsonToday = jsonObject!!
                        jumpToday.jump = jsonObject.optInt("jump", 0)
                        jumpToday.calorie = jsonObject.optDouble("calorie", .0).toFloat()
                        jumpToday.start = jsonObject.optLong("start", 0)
                        jumpToday.end = jsonObject.optLong("end", 0L)
                        jumpToday.duration = jsonObject.optLong("duration", 0L)
                        jumpToday.goal = jsonObject.optInt("goal", 0)
                        jumpToday.progress = jsonObject.optDouble("progress", .0).toFloat()
                        jumpToday.weight = jsonObject.optDouble("weight", .0).toFloat()

                    } else {

                        newTodayJump()

                    }


                } else {

                    // 데이터가 없다
                    newTodayJump()

                }


            } else {
                log(tag, "load todayjump - new ")
                newTodayJump()
            }
        }


        //
        fun saveData(work: RopeCount, onComplete: () -> Unit) {
            val ropeCountArray = ArrayList<RopeCount>()
            ropeCountArray.add(work)
            saveData(ropeCountArray, onComplete)
        }

        fun saveData(works: ArrayList<RopeCount>, onComplete: () -> Unit) {

            // 구글 피트니스 데이터 저장
//            if (GoogleFitLink.status()) GoogleFitLink.addDataArray(works)

            //
//            DashboardData.forceUpdate = true

            //
//            if(FirebaseAuth.getInstance().currentUser==null) {
            if (!SAuth.logined) {
                // LOGIN 안했음
                log(tag, "saveData login X")

                saveToLocal(works)
                onComplete()

                // 노티피케이션 > 데이터가 바뀔 때마다 적용 //
                Notificater.updateNotificationMain()

            } else {
                // LOGIN 했음
                log(tag, "saveData login O")

                val jsonData = JSONObject()
                jsonData.put("fid", SAuth.uid)//FirebaseAuth.getInstance().currentUser?.uid)
                jsonData.put("email", SAuth.email)//FirebaseAuth.getInstance().currentUser?.email)
                jsonData.put("device", bleConnection.INFO.SID)
                jsonData.put("now", getLocalTime())

                //
                val jsonWorks = JSONArray()
                works.forEach { ropeCount ->
                    //jsonWorks.put( JSONObject( Gson().toJson(ropeCount) ) )
//                    log(tag,"..." + JSONObject( GsonBuilder().create().toJson(ropeCount) ) )
//                    log(tag,"jump :" + ropeCount.jump )
//                    log(tag,"calorie :" + ropeCount.calorie )
                    try {
                        // Fatal Exception: java.lang.IllegalArgumentException: Infinity is not a valid double value as per JSON specification. // 희귀한 오류남
                        jsonWorks.put(JSONObject(GsonBuilder().create().toJson(ropeCount)))
                    } catch (e: Exception) {
                    }
                }
                jsonData.put("works", jsonWorks)
                //log(tag, "json work : $jsonData")

                //
                Transaction.getJson("SaveJump", jsonData) { json ->

                    val result = try {
                        json?.getInt("result")
                    } catch (e:Exception) {
                        1
                    }

                    when (result) {
                        0 -> {
                            // 서버에 저장된 데이터와 LOCAL 데이터간의 차이가 있을 수 있다 .. >> 서버 기준으로 업데이트 한다
                            // SUCCESS
                            val severWork = json?.optJSONObject("work") ?: JSONObject()
                            jumpToday.jump = severWork.optInt("jump", 0)
                            jumpToday.calorie = severWork.optDouble("calorie", .0).toFloat()
                            jumpToday.goal = severWork.optInt("goal", 0)
                            jumpToday.duration = severWork.optLong("duration", 0L)
                            try {
                                jsonToday = JSONObject(Gson().toJson(jumpToday))
                            } catch (e: Exception) {
                            }
                            //log(tag, jsonToday.toString() )
                            Preferences.set("todayjump", jsonToday)
                        }
                        else -> {
                            // 서버에 저장할 수 없으니 로컬에라도 저장
                            saveToLocal(works)
                        }
                    }
                    onComplete()

                    // 노티피케이션 > 데이터가 바뀔 때마다 적용 //
                    Notificater.updateNotificationMain()

                }

            }
        }

        //
        private fun saveToLocal(work: ArrayList<RopeCount>) {

            log(tag, "saveToLocal")

            // 기존 데이터 마지막 저장 시간이 오늘이 아닌경우 리셋
            val isYesterdayJSON = Preferences.getJson("todayjump")
            val end = try {
                isYesterdayJSON?.getLong("end")!!
            } catch (e: JSONException) {
                0L
            }

            //log(tag,"load date : " + end )

            // 저장된 데이터의 마지막 시간이 오늘이 아니면 .. >> 삭제
            if (!DateUtils.isToday(end - getTimeOffSet())) newTodayJump()

            // 데이터 합산
            work.forEach { ropeCount ->

                //
                log(tag, "jump count : " + ropeCount.jump)
                log(tag, "jump date : " + ropeCount.end)
                log(tag, "today ~~ :: " + DateUtils.isToday(ropeCount.end))

                // 오늘인 경우에 ..
                if (DateUtils.isToday(ropeCount.end - getTimeOffSet())) {

                    //
                    jumpToday.jump += ropeCount.jump
                    jumpToday.goal = ropeCount.goal
                    jumpToday.weight = ropeCount.weight
                    jumpToday.calorie += ropeCount.calorie
                    jumpToday.duration += ropeCount.duration
                    jumpToday.end = ropeCount.end

                    //
                    jumpToday.progress = jumpToday.jump.toFloat() / jumpToday.goal.toFloat()
                    if (jumpToday.progress.isNaN() || jumpToday.progress.isInfinite()) jumpToday.progress =
                        0f
                    log(tag, "start " + ropeCount.start)
                    //
                    if (jumpToday.start == 0L) jumpToday.start = ropeCount.start
                }
            }

            log(tag, "SAVE : " + jumpToday.jump)

            // 오늘 데이터 저장
            try {
                jsonToday = JSONObject(Gson().toJson(jumpToday))
            } catch (e: JSONException) {
            }

            //
            log(tag, "SAVE JSON : $jsonToday")
            Preferences.set("todayjump", jsonToday)
        }

        //
        fun newTodayJump() {

            log(tag, "new today jump")

            //
            jumpToday.weight = personInfo.weight.toFloat()
            jumpToday.goal = personInfo.daily_goal
            jumpToday.jump = 0
            jumpToday.start = getLocalTime() // 지금 시간
            jumpToday.end = getLocalTime() // 지금 시간
            jumpToday.duration = 0

            // 신규 데이터 저장 . RESET
            try {
                jsonToday = JSONObject(Gson().toJson(jumpToday))
            } catch (e: JSONException) {
            }
            log(tag, jsonToday.toString())
            Preferences.set("todayjump", jsonToday)

        }

        //
        fun reset() {
            newTodayJump()
        }

    }


}