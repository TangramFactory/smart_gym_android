package kr.tangram.smartgym.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Html
import android.text.Spanned
import android.text.format.DateFormat
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kr.tangram.smartgym.util.func.Alert
import kr.tangram.smartgym.BuildConfig
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ble.BleSmartRopeConnect
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs


/**
 * Created by sanghyun on 2018. 3. 7..
 */

//
var DEBUG = BuildConfig.DEBUG

enum class FlavorType { LIVE, DEV, PROXY_LIVE, PROXY_DEV }
var FLAVOR = when(BuildConfig.FLAVOR) {
    "LIVE" -> FlavorType.LIVE
    "DEV" -> FlavorType.DEV
    "PROXY_LIVE" -> FlavorType.PROXY_LIVE
    "PROXY_DEV" -> FlavorType.PROXY_DEV
    else -> FlavorType.DEV // default
}

// 친구 초대 코드
// Splash > DeepLink 에서 받아들임
var InvitaionCode: String? = null
var InvitaionType: String? = null

//
@SuppressLint("StaticFieldLeak")
//var SRDecive = SmartRopeDevice()
var bleConnection = BleSmartRopeConnect()

//DFU 다운로드 경로
var DFUURL: String? = "http://tangramfactory.com/application/smartrope/firmware/firmware19.zip"
var ROOKIEDFUURL: String? = "http://tangramfactory.com/application/smartrope/firmware/rookie10.zip"

//DFU 모드
var DFU_MODE: Boolean = false

// 기본 골 데이터 //
var defaultGoal = 300

// Voice Output
@SuppressLint("StaticFieldLeak")
var vc: VoiceCounter? = null

// API 서버
var FireStoreServer: String = getMainServer() //getFirstServer()

// 작동되고 있는 라이브 서버에서 >> DB선택
fun getMainServer(): String = ""
//    return  BuildConfig.SERVER_LIVE
    /*
    return if (DEBUG) {
        BuildConfig.SERVER_DEV
//        "https://us-central1-tangram-smartrope.cloudfunctions.net/devSmartrope/" // DEVELOPER SERVER
//        "https://us-central1-tangram-smartrope.cloudfunctions.net/liveSmartrope/" // LIVE SERVER
    } else {
        BuildConfig.SERVER_LIVE
//        "https://us-central1-tangram-smartrope.cloudfunctions.net/liveSmartrope/"
    }
    */
//}

//
//fun getProxyServer(): String {
//    return if(FLAVOR==FlavorType.PROXY_LIVE){
//        BuildConfig.SERVER_LIVE
//    } else {
//        BuildConfig.SERVER_DEV
//    }
//    return if (DEBUG) {
////        "http://02.api.smartrope.tangramfactory.com/dev/"
//        BuildConfig.SERVER_DEV
//    } else {
//        BuildConfig.SERVER_LIVE
////        "http://02.api.smartrope.tangramfactory.com/live/"
//    }


//// 펑션을 개발 백업 서버
//fun getSecondServer():String{
//    //return "http://10.1.1.38:9000/tangram-development/us-central1/devSmartrope/"
//    return "https://us-central1-tangram-development.cloudfunctions.net/devSmartrope/"
//}

// -- 위 데이터들이 셋업 되고 나와야 함
// 개인정보데이터 //
var personInfo: PersonInfo = PersonInfo()

//var locationServicePop = LocationServiceCheckPopup()

//
fun log(tag: String, message: String) {
    if (DEBUG) Log.d(tag, "✅ $message")
}

fun loge(tag: String, message: String) {
    if (DEBUG) Log.e(tag, "⛔ $message")
}

// 로컬시간 로컬 오프셋을 더해준다
fun getLocalTime(): Long {
    return Date().time + TimeZone.getDefault().rawOffset
}

fun getLocalCal(): Calendar {
    val offSet = TimeZone.getDefault().rawOffset
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.timeInMillis = cal.timeInMillis + offSet
    return cal
}

fun getTime(): Long {
    return Date().time
}

fun getCountry(): String {
    // 2 STRING
    return Locale.getDefault().country
}

fun getTimeZone(): String {
    return TimeZone.getDefault().id.toString()
}

fun getLanguage(): String {
    return Locale.getDefault().language
}

fun getTimeOffSet(): Int {

    var offSet = TimeZone.getDefault().rawOffset
     return offSet
}

fun setZeroHour(cal: Calendar) {
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
}

fun setFullHour(cal: Calendar) {
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.add(Calendar.DAY_OF_YEAR, 1)
    cal.add(Calendar.MILLISECOND, -1)
}

fun shakeItBaby(context: Context, shakeTime:Long) {
    if (Build.VERSION.SDK_INT >= 26) {
        (context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(shakeTime, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        (context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator).vibrate(shakeTime)
    }
}

//
class FBAnalytics {
    companion object {
        private var mFirebaseAnalytics: FirebaseAnalytics? = null
        private var TAG: String = "FBAnalytics"
        fun init(c: Context) {
            Log.d(TAG, "init")
            if (checkGooglePlayService(c)) mFirebaseAnalytics = FirebaseAnalytics.getInstance(c)
        }
        fun event(activity: String, action: String, summary: String) {
            // Create New Log to Google Analytics
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, activity)
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, action)
            bundle.putString(FirebaseAnalytics.Param.CONTENT, summary)
            mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        }
    }
}

// 크래쉬리틱스에 키셋
class FBCrashlyticsSetKey() {
    private var tag = "FBCrashlyticsKeySet"
    init {
        if(FirebaseAuth.getInstance().currentUser!=null) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            log(tag, "uid : " + currentUser?.uid.toString() )
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("uid", currentUser?.uid.toString() )
                setCustomKey("email", currentUser?.email.toString() )
                setCustomKey("name", currentUser?.displayName.toString() )
            }
        } else {
            log(tag, "uid : null" )
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("uid", "null" )
                setCustomKey("email", "null" )
                setCustomKey("name", "null" )
            }
        }
    }
}


//
fun checkInternetAlert(c: Context, f: (status: Boolean) -> Unit) {
    if (checkInternetConnection(c)) {
        f(true)
    } else {
        Alert.show(c, c.getString(R.string.message_internet_denied)) { f(false) }
    }
}

//
fun checkInternetConnection(c: Context): Boolean {
    val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

//
fun checkGooglePlayService(c: Context): Boolean {

    // Google Service Available Check
    val gApi = GoogleApiAvailability.getInstance()
    val resultCode = gApi.isGooglePlayServicesAvailable(c)

    // Check up  & Snackbar message
    return if (resultCode == ConnectionResult.SUCCESS) {
        true
    } else {
        Alert.show(c, c.getString(R.string.message_update_googleservice))
        false
    }
}


//
fun lbsTokg(w: Float): Float {
    return w * 0.45359237f
}

fun gunhTokg(w: Float): Float {
    return w * 0.5f
}

// 베터리 이미지 세트
fun setBatteryImage(imageview: ImageView, percent: Int) {

    when {
        percent > 90 -> {
            imageview.setImageResource(R.drawable.ic_icon_bat_100)
        }
        percent > 70 -> {
            imageview.setImageResource(R.drawable.ic_icon_bat_80)
        }
        percent > 50 -> {
            imageview.setImageResource(R.drawable.ic_icon_bat_60)
        }
        percent > 30 -> {
            imageview.setImageResource(R.drawable.ic_icon_bat_40)
        }
        percent > 10 -> {
            imageview.setImageResource(R.drawable.ic_icon_bat_20)
        }
        else -> {
            imageview.setImageResource(R.drawable.ic_icon_bat_0)
        }
    }

    if (percent == 0) {
        imageview.alpha = .0f
    } else {
        imageview.alpha = 1.0f
    }

}

// 팝업 윈도우 띄었을 때 배경 DOM
fun dimBehind(popupWindow: PopupWindow) {
    //
    val container: View = if (popupWindow.background == null) {
            popupWindow.contentView.parent as View
    } else {
            popupWindow.contentView.parent.parent as View
    }

    //
    val context = popupWindow.contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    //
    val p = container.layoutParams as WindowManager.LayoutParams
    p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
    p.dimAmount = 0.6f

    //
    wm.updateViewLayout(container, p)
}


// 칼로리 계산
// 시간갭, 몸무게
//fun calCalorieHistory(time_gap: Long, weight_kg: Float): Float = Calories.calCalorieHistory(time_gap, weight_kg)
fun calCalorieHistory(time_gap: Long, weight_kg: Float): Float {
    // 기준치에 맞는 경우 정상적으로 계산
    // time gap 이 기준치에 맞지 않는 경우 일반적인 경우의 수로 계산
    // 스마트로프 기기에서 받아온 시간이기 때문에 옳지 않을 수가 있다
    if (time_gap in 1..3000) {
        return calCalorie(time_gap, weight_kg)
    }
    return calCalorie(400, weight_kg)
}

//fun calCalorie(time_gap: Long, weight_kg: Float): Float = Calories.calCalorie(time_gap, weight_kg)
fun calCalorie(time_gap: Long, weight_kg: Float): Float {
    var oneCalorie = 0.0f

    // 어디서 왔는지 모르겠지만 기존 펌웨어의 공식을 대입하기로 함!!!
    if (time_gap in 1..3000) {
        val minRPM = 60000.0f / time_gap

        val avrCalorie = when {
            minRPM < 70 -> {
                0.074f
            }
            minRPM < 90 -> {
                0.075f
            }
            minRPM < 110 -> {
                0.077f
            }
            minRPM < 125 -> {
                0.080f
            }
            else -> {
                0.085f
            }
        }

        // LBS로 계산되기 때문에 KG기준 > LBS기준으로 바꿈
        oneCalorie = (weight_kg / 0.45359237f) * avrCalorie / 60.0f / 1000.0f * time_gap
    }
    return oneCalorie
}

//
fun htmlCompact(str: String): Spanned {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return Html.fromHtml(
        str,
        Html.FROM_HTML_MODE_COMPACT
    )
    return Html.fromHtml(str)
}

//
fun getStringLoc(context: Context, resourceId: Int, requestedLocale: Locale): String {
    val result: String
    val config = Configuration(context.resources.configuration)
    config.setLocale(requestedLocale)
    result = context.createConfigurationContext(config).getText(resourceId).toString()
    return result
}

//
var touchCountTime: Long = 0
var touchCountCount = 0

@SuppressLint("ClickableViewAccessibility")
fun touchCountAction(view: View, count: Int, func: (view: View) -> Unit) {
    view.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - touchCountTime) < 300) {
                touchCountCount++
                if (touchCountCount >= count) {
                    touchCountTime = 0
                    touchCountCount = 0
                    func(view)
                }
            } else {
                touchCountCount = 0
            }
            touchCountTime = System.currentTimeMillis()
        }
        return@setOnTouchListener true
    }
}

//
fun touchAlphaAction(view: View, func: (view: View) -> Unit) {
    touchAlphaAction(view, 0.2f, 0, func, null)
}

fun touchAlphaAction(view: View, alpha: Float, func: (view: View) -> Unit) {
    touchAlphaAction(view, alpha, 0, func, null)
}

fun touchAlphaAction(view: View, alpha: Float, time: Long, func: (view: View) -> Unit) {
    touchAlphaAction(view, alpha, time, null, func)
}

fun touchAlphaActionRemove(view: View) {
    view.setOnTouchListener(null)
}

fun touchAlphaAction(
    view: View,
    alpha: Float,
    time: Long,
    funcShort: ((view: View) -> Unit)?,
    funcLong: ((view: View) -> Unit)?
) {

    //
    var mx = 0.0f
    var my = 0.0f
    var touchTime: Long = 0
    lateinit var mTimer: Timer

    //
    view.setOnTouchListener(object : View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent?): Boolean {
            if (event?.action == MotionEvent.ACTION_DOWN) {

                v.animate()?.alpha(alpha)?.setDuration(50)?.interpolator = KAccelerateDecelerateInterpolator()
                mx = event.x
                my = event.y

                // 롱프레스 구현 ..
                if (time > 0L) {
                    mTimer = Timer()
                    mTimer.schedule(timerTask {
                        // Main Thread Event Touch ~~~~~~~~~~
                        var c = view.context
                        while (c !is Activity && c is ContextWrapper) c = c.baseContext
                        (c as Activity).runOnUiThread {
                            if (funcLong != null) funcLong(view)
                        }
                    }, time)
                }

            } else if (event?.action == MotionEvent.ACTION_UP) {

                v.animate()?.alpha(1.0f)?.setDuration(150)?.interpolator = KAccelerateDecelerateInterpolator()

                //
                if (Date().time - touchTime < 500) return false // 0.5초 안에 다시 누르면 간다
                val gx = abs(event.x - mx)
                val gy = abs(event.y - my)

                //
                if (time == 0L) {
                    if (gx < 50 && gy < 50) if (funcShort != null) funcShort(view) // 터치하고 움직였으면 이벤트 취소 //
                    touchTime = Date().time
                } else {
                    mTimer.cancel()
                }

            } else if (event?.action == MotionEvent.ACTION_CANCEL) {

                v.animate()?.alpha(1.0f)?.setDuration(150)?.interpolator =
                    KAccelerateDecelerateInterpolator()
                if (time != 0L) mTimer.cancel()

            }
            return true
        }
    })

}

// Screen Utils
var density = 0f
fun toPx(c: Context, dp: Float): Int {
    density = c.resources.displayMetrics.density
    return (dp * density).toInt()
}

fun toDp(c: Context, px: Int): Int {
    density = c.resources.displayMetrics.density
    return (px / density).toInt()
}

fun toPx(dp: Float): Int {
    return if (density != 0f) {
        (dp * density).toInt()
    } else {
        0
    }
}

fun toDp(px: Int): Int {
    if (density != 0f) {
        return (px / density).toInt()
    } else {
        return 0
    }
}

//
fun scWidthPercent(c: Context, p: Float): Float {
    return c.resources.displayMetrics.widthPixels * p / 100f
}


