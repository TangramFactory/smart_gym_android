package kr.tangram.smartgym.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tangramfactory.smartrope.views.ViewPopCounterAnim
import kr.tangram.smartgym.util.AppSetting

import kr.tangram.smartgym.R
import kr.tangram.smartgym.ble.BleSmartRopePopupEvent
import kr.tangram.smartgym.databinding.LayoutPopCounterBinding
import kr.tangram.smartgym.util.*
import kr.tangram.smartgym.util.JumpToday.*

import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask


/**
 * Created by sanghyun on 2018. 3. 23..
 */

class ViewPopCounter : ConstraintLayout {

    private val tag: String = "ViewPopCounter"
    private var H: Float = 0.0f
    private var bind : LayoutPopCounterBinding
    private var timer: Timer = Timer()
    private var defaultStatusBarColor = Color.BLACK
//    private var buttonConnect: ViewButtonConnect? = null
    private val emptyDataDisplayChar = "-"

    //
    private val viewPopCounterAnim = ViewPopCounterAnim(context)


    //
    enum class JUMP_MODE {
        JUMP,
        CALORIE,
        TIME,
        GOAL
    }

    //
    var aniQue: Long = 0
    var mode = JUMP_MODE.JUMP

    //
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        //
        setBackgroundColor(Color.BLACK)
        open()


        // 레이아웃 붙여 넣기
        bind = LayoutPopCounterBinding.inflate(LayoutInflater.from(context))
        addView(
            bind.root,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        bind.textLeftValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, scWidthPercent(context, 4.6f))
        bind.textLeftLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, scWidthPercent(context, 3f))
        bind.textRightValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, scWidthPercent(context, 4.6f))
        bind.textRightLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, scWidthPercent(context, 3f))

        //
        H = context.resources.displayMetrics.heightPixels / 2.0f
        translationY = H
        alpha = 0.0f
        visibility = View.GONE

        //
//        JumpToday()

        //
        makePage()

        //
        //mode = JUMP_MODE.JUMP // 기본 모드
//        mode = when (Preferences.get("countmode")) {
//            "JUMP" -> JUMP_MODE.JUMP
//            "GOAL" -> JUMP_MODE.GOAL
//            "TIME" -> JUMP_MODE.TIME
//            "CALORIE" -> JUMP_MODE.CALORIE
//            else -> JUMP_MODE.JUMP  // 기본 모드
//        }
//        setMode()

        // 자동으로 붙여 넣는다
        (context as Activity).addContentView(
            this,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun makePage() {

        // Text Switcher
        bind.textCountSwitcher.setFactory {
            val textViewCount = LayoutInflater.from(context)
                .inflate(R.layout.layout_pop_textcount, null) as TextView
            textViewCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, scWidthPercent(context, 24f))
            textViewCount.height = 800
            textViewCount
        }

        //
        viewPopCounterAnim.setMode(mode)
        bind.layoutAnim.addView(viewPopCounterAnim)

        //
        bind.textCountSwitcher.inAnimation =
            AnimationUtils.loadAnimation(context, R.anim.count_fade_in)
        bind.textCountSwitcher.outAnimation =
            AnimationUtils.loadAnimation(context, R.anim.count_fade_out)

        //
//        bind.buttonSetting.addView(buttonConnect)
        //
        bind.buttonClose.isEnabled = true
        touchAlphaAction(bind.buttonClose) {

            //
            if (AppSetting.sound_jump > 0 && AppSetting.sound_voicecount > 0) {

                //
                timer.cancel()
                bind.buttonClose.isEnabled = false
                bind.buttonClose.setColorFilter(Color.DKGRAY)
//                SoundEffect.play.pop(AppSetting.sound_jump / 100f)
                SoundEffect.play.ready(AppSetting.sound_jump / 100f)

                //
                try {
                    vc?.countComplete(ropeCount.jump, "complete") { id ->
                        log(tag, "utter ~~~~~~~~ ")
                        if (id == "complete") {
                            //
                            (context as Activity).runOnUiThread {
                                bleConnection.privateCount = 0
                                //SRDecive.privateCount = 0
                                close {}
//                                buttonConnect!!.stop()
                            }
                        }
                    }
                } catch (e: Exception) {
                }

            } else {

//                SoundEffect.play.pop(AppSetting.sound_jump / 100f)
                SoundEffect.play.ready(AppSetting.sound_jump / 100f)
                bleConnection.privateCount = 0
                //SRDecive.privateCount = 0
                close {}
//                buttonConnect!!.stop()

            }
        }


        //
        setOnClickListener {
            log(tag, "touch ~~~")
            setNextMode()
        }

    }

    //
    var mHandler = Handler(Looper.getMainLooper())
    var oldCount: Int = 0
    var mRunnable: Runnable = object : Runnable {
        override fun run() {
            //
            if (oldCount != ropeCount.jump) {
                if (aniQue % 2 == 0L) {
//                    image_dots_over.animate().alpha(1.0f).setDuration(300).setInterpolator(KAccelerateDecelerateInterpolator())
                } else {
//                    image_dots_over.animate().alpha(0.0f).setDuration(800).setInterpolator(KAccelerateDecelerateInterpolator())
                }
                aniQue++
            } else {
                //image_dots_over.animate().alpha(0.0f).setDuration(900).setInterpolator(KAccelerateDecelerateInterpolator())
            }
            oldCount = ropeCount.jump

            //
            if (alpha == 1.0f) {
                mHandler.postDelayed(this, 1000)
            } else {
                mHandler.removeCallbacks(this) // 혹시몰라 .. 핸들러 자동삭제 //
            }
        }
    }

    //
    fun open() {

        if (bleConnection.smartropeInterface?.isEnable() != true) return
        log(tag, "show ")

        //
        if (visibility == View.VISIBLE) return
        bind.buttonClose.setColorFilter(Color.WHITE)

//        // 상단 여백이 필요해요 //
//        val windowFlags = (context as Activity).window.attributes.flags
//        if ( windowFlags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0 ) {
//            val statusHeight = try { resources.getDimensionPixelSize( resources.getIdentifier("status_bar_height", "dimen", "android") ) } catch (e: NullPointerException) { 0 }
//            val buttonLayoutParams = button_setting.layoutParams as ConstraintLayout.LayoutParams
//            buttonLayoutParams.topMargin = statusHeight + 8
//            button_setting.layoutParams = buttonLayoutParams
//        }

        // 배터리 정보 표시
//        buttonConnect!!.start()

        //

        bleConnection.smartropeInterface?.onCount(BleSmartRopePopupEvent.OPEN) // 오픈이벤트
        bind.buttonClose.isEnabled = true

        //
        log(tag, "KEEP SCREEN...")
        (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //** 화면유지
        defaultStatusBarColor = (context as Activity).window.statusBarColor
        //(context as Activity).window.statusBarColor = Color.BLACK
        statusbarColorChange(Color.BLACK, 100, 450) // 상태바 컬러를 천천히 바꿉니다.

        //
        mode = when (Preferences.get("countmode")) {
            "JUMP" -> JUMP_MODE.JUMP
            "GOAL" -> JUMP_MODE.GOAL
            "TIME" -> JUMP_MODE.TIME
            "CALORIE" -> JUMP_MODE.CALORIE
            else -> JUMP_MODE.JUMP  // 기본 모드
        }
        viewPopCounterAnim.animationStart()
        viewPopCounterAnim.setMode(mode)
        setMode()


        //
        visibility = View.VISIBLE
        countingStart()
        animate().setDuration(600).translationY(0.0f).alpha(1.0f)
            .setInterpolator(KAccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    translationY = 0.0f
                    alpha = 1.0f
                    // Status Bar Color
                    //(context as Activity).window.setStatusBarColor(Color.BLACK)
                    //
                    try {
                        handler.postDelayed(mRunnable, 1000)
                    } catch (e: NullPointerException) {
                    }
                }
            })

        //
        SoundEffect.play.ready(AppSetting.sound_jump / 100f)

        // 보이스 카운터
        if (AppSetting.sound_jump > 0 && AppSetting.sound_voicecount > 0) {
            vc = VoiceCounter(context)
            vc?.create(context)
        }


    }

    //
    fun close(f: () -> Unit?) {
//        log(tag, "hide")
//        log(tag,"KEEP SCREEN...")
        (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //** 화면유지 끔
        //(context as Activity).window.statusBarColor = defaultStatusBarColor // 스테이터스바 컬러 복구
        statusbarColorChange(defaultStatusBarColor)

        //
        ropeCount.end =
            ropeCount.start + ropeCount.duration //* 종료시간 Duration은 마지막으로 운동하여 업데이트 된 시간 임 ! >> 타이트하게 !!

        //
        viewPopCounterAnim.animationStop()

        //
        mHandler.removeCallbacks(mRunnable)

        // Status Bar Color
        //(context as Activity).window.setStatusBarColor(ContextCompat.getColor(context,R.color.background)) // 원래 있던 상단 배경 유지 !

        // ROPE DATA RESET
        //SRDecive.send("CNT:RST!")
        bleConnection.write("CNT:RST!")
        //bleConnection.STATE = BleSmartRopeState.CONNECT

        // On Main Looper
        animate().setDuration(600).alpha(0f).translationY(H)
            .setInterpolator(KAccelerateDecelerateInterpolator()).alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.GONE

                    // 데이터 저장 >> 조금 천천히 업데이트
                    if (ropeCount.jump > 0) sendRopeData()

                    //
                    try {
                        f()
                    } catch (e: NullPointerException) {
                    }
                }
            })

        //
        if (AppSetting.sound_jump > 0 && AppSetting.sound_voicecount > 0) vc?.terminate()

        // 시간 제한 해제하고 나가야지
        timer.cancel()
        if (timerCircle != null) timerCircle?.stop()

    }

    //******
    var ropeCount: RopeCount = RopeCount()
    private var timeOld: Long = 0

    private fun countingStart() {

        //
        ropeCount = RopeCount()

        // 몸무게 가져오기
        var weight: Float = personInfo.weight.toFloat() //*** 기본 몸무게
        val weightUnit: Int = personInfo.weight_unit
        if (weightUnit == 1) {
            weight = lbsTokg(weight) // lbs to kg
        } else if (weightUnit == 2) {
            weight = gunhTokg(weight) // gunh to kg
        }

        // 일일 목표치
        val dailyGoal = personInfo.daily_goal

        // reset data
        ropeCount = RopeCount()
        ropeCount.weight = weight
        ropeCount.goal = dailyGoal
        ropeCount.start = getLocalTime()//Date().time
        timeOld = getLocalTime()//Date().time
        //log(tag," >>" + Preferences.getJson("person") )


    }

    fun counting() {

        //
        viewPopCounterAnim.jumping()

        // 시간 & 점프 횟수 정의
        val timeNow = getLocalTime()//Date().time
        ropeCount.duration = timeNow - ropeCount.start
        ropeCount.jump++
//        ropeCount.rpm = getRPM(ropeCount.duration,ropeCount.jump)

        if (ropeCount.jump > 0 && ropeCount.jump % 10 == 0) SoundEffect.play.pop(AppSetting.sound_jump / 100f)

        // 칼로리 계산
        val timeGap = timeNow - timeOld
        val oneCalorie = calCalorie(timeGap, ropeCount.weight)
        ropeCount.calorie += oneCalorie

        //
        var progress: Float = ropeCount.jump.toFloat() / ropeCount.goal.toFloat()
        if (progress < 0.0f) progress = 0.0f
        ropeCount.progress = progress
        //log(tag, "PROGRESS " + ropeCount.goal + " " + ropeCount.progress)

        //
        timeOld = timeNow


        //
        if (AppSetting.sound_jump > 0 && AppSetting.sound_voicecount > 0) {
            // 10 / 100 개 마다
            if (ropeCount.jump % AppSetting.sound_voicecount == 0) vc?.count(ropeCount.jump)
        }



        //
        setUpdate()
    }

    //
    private fun setNextMode() {
        mode = when (mode) {
            JUMP_MODE.GOAL -> JUMP_MODE.JUMP
            JUMP_MODE.JUMP -> JUMP_MODE.CALORIE
            JUMP_MODE.CALORIE -> JUMP_MODE.TIME
            JUMP_MODE.TIME -> JUMP_MODE.GOAL

        }
        Preferences.set("countmode", mode.toString())

        setMode()
    }

    //
    private fun setMode() {
        //
        //var fontScale = resources.displayMetrics.density / 2.625f // auto font scale
        val textLength = ropeCount.jump.toString().length // 문자열길이
        //
        bind.textCountSwitcher.animate().alpha(0.0f).setDuration(100).withEndAction {

            //
            when (mode) {
                JUMP_MODE.JUMP -> {

                    val fontSize = when {
                        textLength <= 3 -> 24f
                        textLength == 4 -> 22f
                        textLength == 5 -> 19f
                        else -> 17f
                    }
                    (bind.textCountSwitcher.getChildAt(1) as TextView).setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        scWidthPercent(context, fontSize)
                    )
                    (bind.textCountSwitcher.getChildAt(0) as TextView).setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        scWidthPercent(context, fontSize)
                    )

                    //
                    bind.textCountSwitcher.inAnimation =
                        AnimationUtils.loadAnimation(context, R.anim.count_jump_in)
                    bind.textCountSwitcher.outAnimation =
                        AnimationUtils.loadAnimation(context, R.anim.count_jump_out)

                    //
//                    basiccount_circle.setBackgroundResource(R.drawable.ic_basiccount_circle_jump)
                    bind.modeIcon.setImageResource(R.drawable.ic_home_basiccount_jump)

                    bind.textMode.text = context.getString(R.string.basiccount_title_jump)
                    bind.textMode.setTextColor(Color.parseColor(context.getString(R.string.basiccount_title_jump_color)))
                    bind.textLeftLabel.text = context.getString(R.string.word_time)
                    bind.textRightLabel.text = context.getString(R.string.word_calorie)

                    //
                    bind.imageIndicator.setImageResource(R.drawable.ic_basic_count_indicator_01)

                }
                JUMP_MODE.CALORIE -> {

                    (bind.textCountSwitcher.getChildAt(1) as TextView).setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        scWidthPercent(context, 20f)
                    )
                    (bind.textCountSwitcher.getChildAt(0) as TextView).setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        scWidthPercent(context, 20f)
                    )

                    //
                    bind.textCountSwitcher.inAnimation =
                        AnimationUtils.loadAnimation(context, R.anim.count_fade_in)
                    bind.textCountSwitcher.outAnimation =
                        AnimationUtils.loadAnimation(context, R.anim.count_fade_out)

                    //
//                    basiccount_circle.setBackgroundResource(R.drawable.ic_basiccount_circle_calorie)
                    bind.modeIcon.setImageResource(R.drawable.ic_home_basiccount_calories)
                    bind.textMode.text = context.getString(R.string.basiccount_title_calorie)
                    bind.textMode.setTextColor(Color.parseColor(context.getString(R.string.basiccount_title_calorie_color)))
                    bind.textLeftLabel.text = context.getString(R.string.word_jumps)
                    bind.textRightLabel.text = context.getString(R.string.word_time)

                    //
                    bind.imageIndicator.setImageResource(R.drawable.ic_basic_count_indicator_02)

                }
                JUMP_MODE.TIME -> {

                    (bind.textCountSwitcher.getChildAt(1) as TextView).setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        scWidthPercent(context, 17f)
                    )
                    (bind.textCountSwitcher.getChildAt(0) as TextView).setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        scWidthPercent(context, 17f)
                    )

                    //
                    bind.textCountSwitcher.inAnimation =
                        AnimationUtils.loadAnimation(context, R.anim.count_fade_in)
                    bind.textCountSwitcher.outAnimation =
                        AnimationUtils.loadAnimation(context, R.anim.count_fade_out)

                    //
//                    basiccount_circle.setBackgroundResource(R.drawable.ic_basiccount_circle_duration)
                    bind.modeIcon.setImageResource(R.drawable.ic_home_basiccount_duration)

                    bind.textMode.text = context.getString(R.string.basiccount_title_time)
                    bind.textMode.setTextColor(Color.parseColor(context.getString(R.string.basiccount_title_time_color)))
                    bind.textLeftLabel.text = context.getString(R.string.word_jumps)
                    bind.textRightLabel.text = context.getString(R.string.word_calorie)

                    //
                    bind.imageIndicator.setImageResource(R.drawable.ic_basic_count_indicator_03)

                }
                JUMP_MODE.GOAL -> {

                    (bind.textCountSwitcher.getChildAt(1) as TextView).setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        scWidthPercent(context, 20f)
                    )
                    (bind.textCountSwitcher.getChildAt(0) as TextView).setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        scWidthPercent(context, 20f)
                    )

                    //
                    bind.textCountSwitcher.inAnimation =
                        AnimationUtils.loadAnimation(context, R.anim.count_fade_in)
                    bind.textCountSwitcher.outAnimation =
                        AnimationUtils.loadAnimation(context, R.anim.count_fade_out)

                    //
//                    basiccount_circle.setBackgroundResource(R.drawable.ic_basiccount_circle_goal)
                    bind.modeIcon.setImageResource(R.drawable.ic_home_basiccount_goal)

                    bind.textMode.text =
                        context.getString(R.string.basiccount_title_goal) + " " + ropeCount.goal.toString()
                    bind.textMode.setTextColor(Color.parseColor(context.getString(R.string.basiccount_title_goal_color)))
                    bind.textLeftLabel.text = context.getString(R.string.word_calorie)
                    bind.textRightLabel.text = context.getString(R.string.word_time)

                    //
                    bind.imageIndicator.setImageResource(R.drawable.ic_basic_count_indicator_04)
                }
            }

            //
            viewPopCounterAnim.setMode(mode)
            setUpdate()

            //
            bind.textCountSwitcher.animate().alpha(1.0f).setStartDelay(150)
                .setDuration(300).interpolator = DecelerateInterpolator()

        }.interpolator = AccelerateInterpolator()


    }

    private fun setUpdate() {
        // 칼로리 표현
        val strCalorie: String = if (ropeCount.calorie < 10) { // 숫자에 따라서 소숫점 겟수 다르게
            String.format("%.2f", ropeCount.calorie)
        } else {
            String.format("%.1f", ropeCount.calorie)
        }

        // 목표량
        val strProgress: String = if (ropeCount.progress * 100.0f < 10) {
            String.format("%.2f", ropeCount.progress * 100.0f)
        } else {
            String.format("%.1f", ropeCount.progress * 100.0f)
        }

        // 시간
        val timeOffset = TimeZone.getDefault().rawOffset
        val duration = ropeCount.duration - timeOffset
        val strTimeSecond =
            SimpleDateFormat("HH:mm:ss").format(duration) //setTimeFormatSecond(ropeCount.duration)
        //val strTimeMinute = SimpleDateFormat("HH:mm").format(duration) ///setTimeFormatMinute(ropeCount.duration)


        //
        when (mode) {

            JUMP_MODE.JUMP -> {
                //
                val jumpString = String.format("%,d", ropeCount.jump)
                val textLength = jumpString.length
                val fontSize = when {
                    textLength <= 3 -> 24f
                    textLength == 4 -> 22f
                    textLength == 5 -> 19f
                    else -> 17f
                }

                (bind.textCountSwitcher.getChildAt(1) as TextView).setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    scWidthPercent(context, fontSize)
                )

                (bind.textCountSwitcher.getChildAt(0) as TextView).setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    scWidthPercent(context, fontSize)
                )

                //
                bind.textCountSwitcher.setText(jumpString)
                bind.textLeftValue.text = strTimeSecond
                bind.textRightValue.text = strCalorie
            }
            JUMP_MODE.CALORIE -> {
                bind.textCountSwitcher.setText(strCalorie)
                bind.textLeftValue.text = ropeCount.jump.toString()
                bind.textRightValue.text = strTimeSecond
            }
            JUMP_MODE.TIME -> {
                bind.textCountSwitcher.setText(strTimeSecond)
                bind.textLeftValue.text = ropeCount.jump.toString()
                bind.textRightValue.text = strCalorie
            }
            JUMP_MODE.GOAL -> {
                bind.textCountSwitcher.setText(strProgress)
                bind.textLeftValue.text = strCalorie
                bind.textRightValue.text = strTimeSecond
            }
        }

        // 5초 동안 운동하지 않으면 //
        timer.cancel()

        timer = Timer()
        timer.schedule(timerTask {
            Handler(Looper.getMainLooper()).post {
                startTimeOut()
            }
        }, 5000)
        timerCircle?.stop()

    }

    private var timerCircle: ViewTimerCircle? = null
    private fun startTimeOut() {

        //
        timerCircle = ViewTimerCircle(context)
        bind.layoutButton.addView(timerCircle)
        timerCircle?.targetMilliseconds = 7000 // 10초의 시간을 더 주겠다 !!
        timerCircle?.onComplete = {
            log(tag, "COMPLETE ")
            timerCircle?.stop()
            //
//            SoundEffect.play.pop(AppSetting.sound_jump / 100f)
            SoundEffect.play.ready(AppSetting.sound_jump / 100f)
            close {}
        }
        timerCircle?.start()

        //
        if (AppSetting.sound_jump > 0 && AppSetting.sound_voicecount > 0) {
//            SoundEffect.play.pop(AppSetting.sound_jump / 100f)
            SoundEffect.play.ready(AppSetting.sound_jump / 100f)
            vc?.countComplete(ropeCount.jump, "autoComplete") {}
        }

    }

    // 상단 스테이터스바 색상변경 에니메이션 //
    private fun statusbarColorChange(toColor: Int, duration: Long = 120, delay: Long = 0) {

        val statusBarAnim =
            ValueAnimator.ofArgb((context as Activity).window.statusBarColor, toColor)
        statusBarAnim.duration = duration
        statusBarAnim.startDelay = delay
        statusBarAnim.interpolator = AccelerateDecelerateInterpolator()

        statusBarAnim.addUpdateListener { valueAnimator ->
            (context as Activity).window.statusBarColor = valueAnimator.animatedValue as Int
        }

        statusBarAnim.start()
    }

    //*******
    fun sendRopeData() {

        JumpToday.saveData(ropeCount) {
            // Close Event **
            bleConnection.smartropeInterface?.onCount(BleSmartRopePopupEvent.CLOSE) // 클로즈 이벤트
            ropeCount.jump = 0
        }


    }

}
