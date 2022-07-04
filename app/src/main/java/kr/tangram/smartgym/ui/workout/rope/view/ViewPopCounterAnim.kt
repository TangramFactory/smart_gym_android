package kr.tangram.smartgym.ui.workout.rope.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import kr.tangram.smartgym.R
import kotlin.math.cos
import kotlin.math.sin

class ViewPopCounterAnim : View {


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
    private val  job = Job()
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
        invalidate()
        CoroutineScope(Dispatchers.Default+ job).launch {
            while (true){
                rpmQue -= 1.8 // 감소량 ---  부스팅 줄어드는 속도
                if(rpmQue<1) rpmQue = 1.0 // 최소량
                que += speed + (rpmQue/1600.0) // 돌아가는 속도
                withContext(Dispatchers.Main){
                    invalidate()
                }
                delay(20)
            }
        }
    }

    //

    fun animationStop() {
        job.cancel()
    }


    fun jumping() {
        rpmQue += (rpmQue + 90) / 4.0
        if(rpmQue>160) rpmQue = 160.0
    }

    //


    //
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val background = Bitmap.createBitmap(canvas?.width!!, canvas?.height!!, Bitmap.Config.ARGB_8888 )
        val backgroundCanvas = Canvas(background)
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
        paintActive.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        paintActive.strokeWidth = lienWidthThin.toFloat()


        //
        paintActive.color = Color.parseColor( colorArray[1] )
        val oval1 = RectF()
        val aX1 = sin(que2+pi2) *rotationDistance
        val aY1 = cos(que2+pi2) *rotationDistance
        oval1.set((cx+aX1).toFloat(), (cy+aY1).toFloat(), (cx2+aX1).toFloat(), (cy2+aY1).toFloat() )
        backgroundCanvas.drawArc(oval1, -90.0f, 360.0f, true, paintActive)

        paintActive.color = Color.parseColor( colorArray[2] )
        val oval2 = RectF()
        val aX2 = sin(que2+pi2*0.25) *rotationDistance
        val aY2 = cos(que2+pi2*0.25) *rotationDistance
        oval2.set((cx+aX2).toFloat(), (cy+aY2).toFloat(), (cx2+aX2).toFloat(), (cy2+aY2).toFloat() )
        backgroundCanvas.drawArc(oval2, -90.0f, 360.0f, true, paintActive)

        paintActive.color = Color.parseColor( colorArray[3] )
        val oval3 = RectF()
        val aX3 = sin(que2+pi2*0.50) *rotationDistance
        val aY3 = cos(que2+pi2*0.50) *rotationDistance
        oval3.set((cx+aX3).toFloat(), (cy+aY3).toFloat(), (cx2+aX3).toFloat(), (cy2+aY3).toFloat() )
        backgroundCanvas.drawArc(oval3, -90.0f, 360.0f, true, paintActive)


        paintActive.color = Color.parseColor( colorArray[0])
        val oval4 = RectF()
        val aX4 = sin(que2+pi2*0.75) *rotationDistance
        val aY4 = cos(que2+pi2*0.75) *rotationDistance
        oval4.set((cx+aX4).toFloat(), (cy+aY4).toFloat(), (cx2+aX4).toFloat(), (cy2+aY4).toFloat() )
        backgroundCanvas.drawArc(oval4, -90.0f, 360.0f, true, paintActive)


        paintActive.strokeWidth = lineWidth.toFloat()
        paintActive.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        paintActive.color = context.getColor(R.color.color_both_1)
        val oval = RectF()
        oval.set(cx, cy, cx2, cy2)
        backgroundCanvas.drawArc(oval, -90.0f, 360.0f, true, paintActive)

        canvas.drawBitmap(background, 0f, 0f, null)


    }

}