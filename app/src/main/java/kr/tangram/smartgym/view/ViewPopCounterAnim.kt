package com.tangramfactory.smartrope.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import kr.tangram.smartgym.view.ViewPopCounter
import kotlin.math.cos
import kotlin.math.sin

class ViewPopCounterAnim : View {

    //
    private var tag:String = "ViewPopCounterAnim"

    //
    private var mHandler: Handler
    private lateinit var mRunnableStart:Runnable

    private var circlePadding = 0.0f
    private var circleRadius = 0.0f
    private var centerPoint: Point = Point()

    private var paintActive:Paint = Paint()

    private var lineWidth = 0.0
    private var lienWidthThin = 0.0

    //
    private var que:Double = 0.0
    private var wav:Double = 0.0

    // RED / ORANGE / BLUE / GREEN /
    private var colorArray = arrayOf("#FF6161", "#FF8532", "#4096FF", "#3DE463" )

    //
    var defaultColor = 3
    private var speed = 0.04
    private var rpmQue = 0.0

    //
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        lineWidth = context.resources.displayMetrics.widthPixels * 0.008 //  0.012
        lienWidthThin = context.resources.displayMetrics.widthPixels * 0.002 // 0.0035

        //
        paintActive = Paint()
        paintActive.isAntiAlias = true
        paintActive.isDither = true
        paintActive.color = Color.WHITE
        paintActive.strokeWidth = 4.0f
        paintActive.strokeCap = Paint.Cap.ROUND
        paintActive.style = Paint.Style.STROKE

        circlePadding = context.resources.displayMetrics.widthPixels*0.11f
        circleRadius = (context.resources.displayMetrics.widthPixels-circlePadding)*0.5f
        centerPoint= Point((context.resources.displayMetrics.widthPixels*0.5f).toInt(), (context.resources.displayMetrics.widthPixels*0.5f).toInt())

        //
        mHandler = Handler()
        mRunnableStart = Runnable {

            //if(turnSpeed>0)turnSpeed -= 0.0018 //0.0008
            //
            rpmQue -= 1.8 // 감소량 ---  부스팅 줄어드는 속도
            if(rpmQue<1) rpmQue = 1.0 // 최소량
            que += speed + (rpmQue/1600.0) // 돌아가는 속도

            //
            invalidate()
            mHandler.postDelayed(mRunnableStart,10)
        }
        mHandler.post(mRunnableStart)

        //
        invalidate()

        //
        this.viewTreeObserver.addOnWindowAttachListener( object : ViewTreeObserver.OnWindowAttachListener {
                override fun onWindowDetached() {
                    mHandler.removeCallbacks(mRunnableStart)
                    viewTreeObserver.removeOnWindowAttachListener(this)
                }
                override fun onWindowAttached() { }
            }
        )
    }

    //
    fun animationStart() {
        mHandler.removeCallbacks(mRunnableStart)
        mHandler.post(mRunnableStart)
    }
    fun animationStop() {
        mHandler.removeCallbacks(mRunnableStart)
    }

    //
    fun setMode(mode: ViewPopCounter.JUMP_MODE) {
        defaultColor = when(mode) {
            ViewPopCounter.JUMP_MODE.JUMP -> 0
            ViewPopCounter.JUMP_MODE.CALORIE -> 1
            ViewPopCounter.JUMP_MODE.TIME -> 2
            ViewPopCounter.JUMP_MODE.GOAL -> 3
        }
    }

    fun jumping() {
        rpmQue += (rpmQue + 90) / 4.0
        if(rpmQue>160) rpmQue = 160.0
    }

    //
    private fun getColor(i:Int):Int {
        return (i+ defaultColor)% colorArray.size
    }

    //
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // CENTER
        val cx = centerPoint.x - circleRadius
        val cy = centerPoint.y - circleRadius
        val cx2 = centerPoint.x + circleRadius
        val cy2 = centerPoint.y + circleRadius

        val range = context.resources.displayMetrics.widthPixels*0.012 + (rpmQue*0.3) //+ (turnSpeed*200.0)//0.012 돌아가는 크기
        val rotationDistance = sin(wav) + range //+ shakeParam
        val pi2 = Math.PI * 2

        val que2 = que

        //
        paintActive.strokeWidth = lienWidthThin.toFloat()
        paintActive.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)

        //
        paintActive.color = Color.parseColor( colorArray[getColor(1)] )
        val oval1 = RectF()
        val aX1 = sin(que2+pi2) *rotationDistance
        val aY1 = cos(que2+pi2) *rotationDistance
        oval1.set((cx+aX1).toFloat(), (cy+aY1).toFloat(), (cx2+aX1).toFloat(), (cy2+aY1).toFloat() )
        canvas?.drawArc(oval1, -90.0f, 360.0f, true, paintActive)

        paintActive.color = Color.parseColor( colorArray[getColor(2)] )
        val oval2 = RectF()
        val aX2 = sin(que2+pi2*0.33) *rotationDistance
        val aY2 = cos(que2+pi2*0.33) *rotationDistance
        oval2.set((cx+aX2).toFloat(), (cy+aY2).toFloat(), (cx2+aX2).toFloat(), (cy2+aY2).toFloat() )
        canvas?.drawArc(oval2, -90.0f, 360.0f, true, paintActive)

        paintActive.color = Color.parseColor( colorArray[getColor(3)] )
        val oval3 = RectF()
        val aX3 = sin(que2+pi2*0.66) *rotationDistance
        val aY3 = cos(que2+pi2*0.66) *rotationDistance
        oval3.set((cx+aX3).toFloat(), (cy+aY3).toFloat(), (cx2+aX3).toFloat(), (cy2+aY3).toFloat() )
        canvas?.drawArc(oval3, -90.0f, 360.0f, true, paintActive)

        //
        paintActive.strokeWidth = lineWidth.toFloat()
        paintActive.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        paintActive.color = Color.parseColor( colorArray[getColor(0)] )

        val oval4 = RectF()
        val aX4 = 0//(Math.sin(que+pi2)*rotationDistance).toFloat()
        val aY4 = 0//(Math.cos(que+pi2)*rotationDistance).toFloat()
        oval4.set((cx+aX4), (cy+aY4), (cx2+aX4), (cy2+aY4))
        canvas?.drawArc(oval4, -90.0f, 360.0f, true, paintActive)

    }

}