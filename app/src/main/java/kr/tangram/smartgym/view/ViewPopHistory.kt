package com.tangramfactory.smartrope.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ble.BleSmartRopePopupEvent
import kr.tangram.smartgym.ble.SmartRopeType
import kr.tangram.smartgym.databinding.LayoutSyncBinding
import kr.tangram.smartgym.databinding.LayoutSyncRowBinding
import kr.tangram.smartgym.util.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

import java.math.BigInteger
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewPopHistory : ConstraintLayout {

    private val tag: String = "ViewPopHistory"
    var mContext:Context?=null
    var estimateSec = 9 // + 1 SEC

    private var ropeCounts = ArrayList<JumpToday.RopeCount>()
    private var person = personInfo  //**

    private var defaultStatusBarColor = Color.BLACK
    private var bind : LayoutSyncBinding

    //
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        //
        mContext = context

        // 레이아웃 붙여 넣기
        bind = LayoutSyncBinding.inflate(LayoutInflater.from(context))

        addView(bind.root, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        //
        OverScrollDecoratorHelper.setUpOverScroll(bind.scrollView)

        //
        defaultStatusBarColor = (context as Activity).window.statusBarColor
        statusbarColorChange(Color.BLACK, 100, 0) // 상태바 컬러를 천천히 바꿉니다.

        //
        bind.buttonReject.setOnClickListener {
            close()
        }
        bind.buttonSave.alpha = 0.5f

        bind.titleBottom.visibility = GONE

        // 자동으로 붙여 넣는다
        context.addContentView(this, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    // 프로그레스 증가하기
    fun setProgress(percent:Float) {
        val progressAnimator = ObjectAnimator.ofInt(bind.progressPercent,"progress",bind.progressPercent.progress,Math.round(percent*100))
        progressAnimator.interpolator = AccelerateDecelerateInterpolator()
        progressAnimator.duration = 300
        progressAnimator.start()
    }

    private fun isHex(hex: String): Int {
        return try {
            val value : BigInteger? = BigInteger(hex, 16)
            value!!.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }


    // 데이터 뿌려주기
    fun addHistoryData(data:List<String>) {

        log(tag,"\n\n")
//        log(tag, data[1] + " " + data[2] + " " + data[3] + " " + data[4] + " " + data[5] + " " + data[6] + " " + data[7] )
//         Index : Count : Calorie : Duration(운동시간 아님.. 평균 점프 시간임) : StartedAt : EndedAt : Device Time
//         H:10:162:8466:2.009527:571768:581750:953225;
//        log(tag , "DATA : $data")

        fun dataSetup(){

            val formatter = DecimalFormat("#,###")
            val formatterComma = DecimalFormat("#,##0.00")
            val calorie: Float?

            //log(tag, "ropetype : $ropeType")
            if (bleConnection.TYPE== SmartRopeType.LED){
                // LED

                // 시간 데이터 정리
                val nowTime = Date().time //System.currentTimeMillis() / 1000
                val deviceTime = data[7].toLong()
                val startTime = data[5].toLong()
                val endTime = data[6].toLong()

                log(tag,"Duration  " + data[3].toLong() )

                // 상대시간
                val beforeStartTime =  deviceTime - startTime
                val beforeEndTime = deviceTime - endTime

                // 절대시간
                val absBeforeStartTime = nowTime - beforeStartTime
                val absBeforeEndTime = nowTime - beforeEndTime

                // 운동시간
                val durationTime = beforeStartTime - beforeEndTime
                val jumpsCount = data[2].toInt()
                val avrTime = durationTime / jumpsCount.toFloat()
                val weight = person.weight
                calorie = calCalorieHistory(avrTime.toLong(),weight.toFloat()) * jumpsCount

                log(tag, "HISTORY  $jumpsCount $durationTime $avrTime $weight $calorie")
                log(tag, "Receive data S :  $jumpsCount / $calorie / $durationTime / $absBeforeStartTime / $absBeforeEndTime")

                // 칼로리가 '0' 경우 보여줄 필요가 없다 //
                if(calorie>0) {

                    // ROW Layout
                    val bind = LayoutSyncRowBinding.inflate(LayoutInflater.from(context))
                    val viewRow = LayoutInflater.from(context).inflate(R.layout.layout_sync_row, this, false) as ConstraintLayout
                    bind.root.tag= ropeCounts.size
                    this.bind.historyLayout.addView(viewRow)

                    // 데이터 보임
                    bind.textRow.text = data[1]
                    // 시간
                    bind.textDatetime.text = getDate(nowTime - beforeEndTime)
                    bind.textJumps.text = formatter.format(jumpsCount)
                    //            viewRow.text_calories.text = formatterComma.format(data[3].toDouble()/10000) // TODO : 칼로리 계산 이상함 >> 다시해야함
                    bind.textCalories.text = formatterComma.format(calorie)

                    if(avrTime>=3000) bind.textCalories.alpha = 0.5f //

                    // 업로드 데이터 준비
                    val ropecount = JumpToday.RopeCount()
                    ropecount.jump = jumpsCount
                    ropecount.calorie = calorie
                    ropecount.goal = person.daily_goal
                    ropecount.duration = durationTime
                    ropecount.start = absBeforeStartTime
                    ropecount.end = absBeforeEndTime
                    ropecount.weight = person.weight.toFloat()
                    ropeCounts.add(ropecount)

                    // 등장 에니메이션
                    viewRow.alpha = 0.0f
                    viewRow.translationY = 30.0f
                    viewRow.animate().translationY(0.0f).setDuration(200).alpha(1.0f).setInterpolator(AccelerateDecelerateInterpolator()).start()

                    //
                    touchAction(viewRow)
                }

            }else if(bleConnection.TYPE==SmartRopeType.ROOKIE){
                // Rookie //

                log(tag, "Receive data :  " + data[1] + " / " + isHex(data[2]) + " / " + data[3] + " / " + data[4] )
                //log(tag, "Receive Parse data :  " + data[1] + " / " + Integer.toHexString(data[2].toInt()).toInt() + " / " + Integer.toHexString(data[3].toInt()).toInt() + " / " + Integer.toHexString(data[4].toInt()).toInt())

                // 시간 데이터 정리
                val nowTime = Date().time //System.currentTimeMillis() / 1000
                val startTime = isHex(data[3]) *1000
                val endTime = isHex(data[4]) *1000

//                log(tag,"--------")
//                log(tag,"start time : " + startTime)
//                log(tag,"end time : " + endTime)
//                log(tag,"--------")

                // 운동시간
                val durationTime = startTime - endTime
                val jumpsCount = isHex(data[2])
                val avrTime = durationTime / jumpsCount.toFloat()
                val weight = person.weight
                calorie = calCalorieHistory(avrTime.toLong(),weight.toFloat()) * jumpsCount

                //
                val absBeforeStartTime = nowTime - startTime.toLong()
                val absBeforeEndTime = nowTime - endTime.toLong()

                //log(tag, "HISTORY  $jumpsCount $durationTime $avrTime $weight $calorie")
                //log(tag, "Receive data S :  $jumpsCount / $calorie / $durationTime / $absBeforeStartTime / $absBeforeEndTime")

                // 칼로리가 '0' 경우 보여줄 필요가 없다 //
                if(calorie>=0) {

                    // ROW Layout
                    val bind = LayoutSyncRowBinding.inflate(LayoutInflater.from(context))
                    bind.root.tag= ropeCounts.size
                    this.bind.historyLayout.addView(bind.root)

                    // 데이터 보임
                    bind.textRow.text = (isHex(data[1]) + 1).toString()

                    // 시간
                    bind.textDatetime.text = getDate(nowTime - startTime)
                    bind.textJumps.text = formatter.format(jumpsCount)
                    bind.textCalories.text = formatterComma.format(calorie)

                    if(avrTime>=3000) bind.textCalories.alpha = 0.5f //

                    //log(tag, "Receive data S :  $jumpsCount / $calorie / $durationTime / $absBeforeStartTime / $absBeforeEndTime")

                    // 업로드 데이터 준비
                    val ropecount = JumpToday.RopeCount()
                    ropecount.jump = jumpsCount
                    ropecount.calorie = calorie
                    ropecount.goal = person.daily_goal
                    ropecount.duration = durationTime.toLong()
                    ropecount.start = absBeforeStartTime  // 상대시간 > 절대시간 GMT
                    ropecount.end = absBeforeEndTime // 상대 시간 > 절대시간 GMT
                    ropecount.weight = person.weight.toFloat()
                    ropeCounts.add(ropecount)

                    // 등장 에니메이션
                    bind.root.alpha = 0.0f
                    bind.root.translationY = 30.0f
                    bind.root.animate().translationY(0.0f).setDuration(200).alpha(1.0f).setInterpolator(AccelerateDecelerateInterpolator()).start()

                    //
                    touchAction(bind.root)

                }
            }

        }

        //
        Handler(Looper.getMainLooper()).post {
            try {
                dataSetup()
            } catch (e:Exception) {}
        }

    }

    //
    @SuppressLint("ClickableViewAccessibility")
    private fun touchAction(view:View) {
        view.setOnTouchListener { v, event ->
            // 쭈욱 긁으면 선택되고 해제되도록 하고 싶은데 복잡하다 ..
            if(event.action==MotionEvent.ACTION_UP) {
                touchRow(v)
            }
            true }
    }
    //
    private fun touchRow(view:View) {

        //
        val checkbox = view.findViewById(R.id.image_checkbox) as ImageView
        if(checkbox.alpha==1.0f) {
            view.animate().alpha(0.5f).setDuration(300).interpolator = KAccelerateDecelerateInterpolator()
            checkbox.animate().scaleX(0.5f).scaleY(0.5f).alpha(1.0f).setDuration(50).withEndAction {
                checkbox.animate().scaleX(1.0f).scaleY(1.0f).alpha(0.5f).setDuration(150).interpolator = OvershootInterpolator() // ALPHA .5
                checkbox.setImageResource(R.drawable.ic_checkbox_history_unchecked)
            }
        } else {
            view.animate().alpha(1.0f).setDuration(300).interpolator = KAccelerateDecelerateInterpolator()
            checkbox.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.5f).setDuration(50).withEndAction {
                checkbox.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(150).interpolator = OvershootInterpolator() // ALPHA 1.0
                checkbox.setImageResource(R.drawable.ic_checkbox_history_checked)
            }
        }

        //
        bind.progressbar.visibility = GONE
        bind.titleBottom.visibility = VISIBLE

        //
        mHandler.removeCallbacks(mRunable)
        bind.buttonSave.text = mContext?.getString(R.string.word_save)
    }


    // 저장 버튼 : 데이터 가저오기가 완료되면
    fun onDataReceiveComplete() {
        //
        mHandler.postDelayed(mRunable,1000)
        bind.buttonSave.alpha = 1.0f

        //
        bind.buttonSave.setOnClickListener {
            mHandler.removeCallbacks(mRunable)
            makeData()
            //
            bind.buttonSave.backgroundTintList = ColorStateList.valueOf(Color.DKGRAY)
            bind.buttonSave.isEnabled = false
        }
    }

    // 10 초를 기다리고 응답 없으면 닫는다
    private var mHandler:Handler = Handler(Looper.getMainLooper())
    private var mRunable:Runnable = object:Runnable {
        override fun run() {
            if(estimateSec==-1) {
                makeData()
            } else {
                setButtonSaveText()
                mHandler.postDelayed(this,1000)
            }
            estimateSec --
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setButtonSaveText() {
        bind.buttonSave.text = mContext?.getString(R.string.word_save) + " " + estimateSec.toString()
    }

    //
    private fun makeData() {

        val ropeCountsSave = ArrayList<JumpToday.RopeCount>()

        // 체크된 데이터만 복사
        for(i in 0 until bind.historyLayout.childCount) {
            val v = bind.historyLayout.getChildAt(i)
            val a = (v.findViewById(R.id.image_checkbox) as ImageView).alpha
            val t = v.tag as Int
            if(a==1.0f) ropeCountsSave.add(ropeCounts.get(t))
        }

        // 저장할 데이터가 있으면 SAVE
        if(ropeCountsSave.size>0) {

            //
            bind.progressbar.visibility = VISIBLE
            bind.titleBottom.visibility = GONE

            // 저장하는 동안 기다리기 //
            JumpToday.saveData(ropeCountsSave) {
                removeHistory { close() }
            }
        } else {
            // 바로 종료 //
            removeHistory { close() }
        }

    }

    //
    private fun removeHistory(onComplete:()->Unit) {
        // 줄넘기 디바이스에 히스토리 지운다
        if (bleConnection.TYPE==SmartRopeType.LED){
            bleConnection.write("HCLEAR!")
        } else if(bleConnection.TYPE==SmartRopeType.ROOKIE){
            bleConnection.write("HTR!")
        }
        onComplete()
    }


    //
    private fun close() {

        //
        statusbarColorChange(defaultStatusBarColor)

        // 창 닫았음 --> 메인 뷰업데이트 //
        bleConnection.smartropeInterface?.onCount(BleSmartRopePopupEvent.CLOSE) // 클로즈 이벤트
        //
        mHandler.removeCallbacks(mRunable)
        //
        animate().alpha(0.0f).translationY(500.0f).setDuration(300).setInterpolator(KAccelerateDecelerateInterpolator()).withEndAction {
            //(ViewPopHistory@this.parent as ViewGroup).removeView(ViewPopHistory@this)
            try {
                (parent as ViewGroup).removeView(this)
            } catch (e:Exception) {
                // 2020.02.19 TypeCastException ..?
            }
        }.start()

    }

    // 상단 스테이터스바 색상변경 에니메이션 //
    private fun statusbarColorChange(toColor:Int, duration:Long=120, delay:Long=0) {
        val statusBarAnim = ValueAnimator.ofArgb((context as Activity).window.statusBarColor,toColor)
        statusBarAnim.duration = duration
        statusBarAnim.startDelay = delay
        statusBarAnim.interpolator = AccelerateDecelerateInterpolator()
        statusBarAnim.addUpdateListener { valueAnimator ->
            (context as Activity).window.statusBarColor = valueAnimator.animatedValue as Int
        }
        statusBarAnim.start()
    }

    //
    @SuppressLint("SimpleDateFormat")
    private fun getDate(timeStamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("yyyy.MM.dd a hh:mm:ss")
            val netDate = Date(timeStamp)
            sdf.format(netDate)
        } catch (ex: Exception) {
            ""
        }
    }

}