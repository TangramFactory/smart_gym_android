package kr.tangram.smartgym.ui.workout.rope.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import kr.tangram.smartgym.R
import java.util.*

@RequiresApi(Build.VERSION_CODES.M)
class ViewTimerCircle : View {
    private val tag: String = "ViewConnecting"
    var layout: ConstraintLayout? = null

    //

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    //
    private val paintFront = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val paintBack = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mRect = RectF()
    private val radiusInDPI = 39.0f
    private var radiusInPixels: Float = 0.toFloat()
    private val strokeWidthInDPI = 2.6f
    private var stokeWidthInPixels: Float = 0.toFloat()
    private var dpi: Float = 0.toFloat()
    private var heightByTwo: Int = 0
    private var widthByTwo: Int = 0


    //
    private var progress = 0.0f
    private var mHandler = Handler()
    private var mRunable = object:Runnable{
        override fun run() {

            val gapTime = Date().time - startTime
            progress = gapTime / targetMilliseconds.toFloat()

            if(progress<1) {
                postInvalidate()
                mHandler.postDelayed(this,10)
            } else {
                progress = 1.0f
                postInvalidate()
                onComplete()
            }
        }

    }

    //
    @RequiresApi(Build.VERSION_CODES.M)
    fun init() {

        val metrics = resources.displayMetrics
        dpi = metrics.density
        radiusInPixels = dpi * radiusInDPI
        stokeWidthInPixels = dpi * strokeWidthInDPI

//        //
//        paintBack.setStrokeWidth(stokeWidthInPixels)
//        paintBack.setStyle(Paint.Style.STROKE)
//        paintBack.setColor(Color.parseColor("#00000000"))

        //
        paintFront.strokeWidth = stokeWidthInPixels
        paintFront.style = Paint.Style.STROKE
//        paintFront.color = Color.parseColor("#202020") //****
        paintFront.color = context.getColor(R.color.color_both_2)
    }

    //
    private var startTime:Long = 0
    var targetMilliseconds = 5000
    var onComplete:()->Unit = {}

    //***
    fun start() {

        //
        startTime = Date().time

        //
        progress = 0f
        mHandler.post(mRunable)
    }

    //
    private var nowStop = false
    fun stop() {
        if(nowStop) return // 한번만 삭제 하자 //
        //
        nowStop = true
        mHandler.removeCallbacks(mRunable)
        animate().alpha(0.0f).setDuration(300).withEndAction {
            if(parent!=null)(parent as ConstraintLayout).removeView(this)
        }
    }

    //
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        heightByTwo = h / 2
        widthByTwo = w / 2
        mRect = RectF(w / 2 - radiusInPixels, h / 2 - radiusInPixels, w / 2 + radiusInPixels, h / 2 + radiusInPixels)
    }

    //
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

//        log(tag, "progress : " +  progress )
//         Background
//        if (paintBack != null) canvas?.drawArc(mRect, sweepAngle, 360.0f, false, paintBack)

        //
        val startAngle = -90.0f
        val finishAngle = 360.0f * progress
        canvas?.drawArc(mRect, startAngle, finishAngle, false, paintFront)
    }

}