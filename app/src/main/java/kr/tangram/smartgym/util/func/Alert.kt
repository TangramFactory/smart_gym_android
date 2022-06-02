package kr.tangram.smartgym.util.func

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kr.tangram.smartgym.R


//
class Alert {

    companion object {

        private var snackbar: Snackbar? = null
        private const val tag = "Alert"

        var LENGTH:Int = Snackbar.LENGTH_INDEFINITE
        var DEFAULT_TEXT_COLOR:Int? = R.color.purple_200
        var DEFAULT_FONT_SIZE:Float? = 12.0f

        var ACTION_TEXT:String = "OK"
        var ACTION_TEXT_COLOR:Int? = R.color.purple_200
        var ACTION:()->Unit = { snackbar!!.dismiss()}

        var BACKGROUND_COLOR:Int? = Color.parseColor("#99272727") // DEFAULT BACKGROUND COLOR

//        private var context:Context? = null
//        private var message:String? = null

        //
        fun init(c:Context) {
            snackbar = null
            LENGTH = Snackbar.LENGTH_INDEFINITE
            DEFAULT_TEXT_COLOR = R.color.purple_200
            ACTION_TEXT = "text"
            DEFAULT_FONT_SIZE = 12.0f
            ACTION_TEXT_COLOR = R.color.purple_200
            BACKGROUND_COLOR = Color.parseColor("#ef202020")//R.color.background_transparent  //c.getResources().getColor(R.color.snackbar_background)
            ACTION = { snackbar!!.dismiss()}
        }

        //
        fun bgColor(c:String): Companion {
            BACKGROUND_COLOR = Color.parseColor(c)
            return this
        }
        fun fontColor(c:String): Companion {
            DEFAULT_TEXT_COLOR = Color.parseColor(c)
            return this
        }
        fun fontSize(i:Float): Companion {
            DEFAULT_FONT_SIZE = i
            return this
        }
        fun actionFontColor(c:String): Companion {
            ACTION_TEXT_COLOR = Color.parseColor(c)
            return this
        }
        fun length(l:Int): Companion {
            LENGTH = l
            return this
        }

        //
        fun show(c:Context,m:Int) {
            show(c,c.resources.getString(m))
        }
        fun show(c:Context,m:String) {
            show(c,m, ACTION_TEXT, ACTION)
        }
        fun show(c:Context,m:String,b:String) {
            show(c,m,b, ACTION)
        }
        fun show(c:Context,m:String,f:()->Unit){
            show(c,m, ACTION_TEXT,f)
        }
        fun show(c:Context,m:String,b:String,f:()->Unit) {

            //
            snackbar = Snackbar.make((c as Activity).window.decorView, m, LENGTH)

            //
            val snackbarText = snackbar!!.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            snackbarText.setTextColor(ContextCompat.getColor(c, DEFAULT_TEXT_COLOR!!))
            snackbarText.textSize = DEFAULT_FONT_SIZE!!
            snackbarText.isSingleLine = false
            snackbarText.isElegantTextHeight = true
            snackbarText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            snackbarText.isSingleLine = false

            //
            val snackbarAction = snackbar!!.view.findViewById(com.google.android.material.R.id.snackbar_action) as TextView
            snackbarAction.textSize = DEFAULT_FONT_SIZE!!

            //
            snackbar?.setAction(b) {
                f()
            }
            snackbar?.setActionTextColor(ContextCompat.getColor(c, ACTION_TEXT_COLOR!!))
            snackbar?.view?.setBackgroundColor(BACKGROUND_COLOR!!)

            // 소프트웨어 키를 사용하는 경우 키에 가려서 안보이는 경우가 있음 > 하단 여백을 강제로 주어 띄움
            val sbView: FrameLayout = snackbar!!.view as FrameLayout
            val sbParams:FrameLayout.LayoutParams = sbView.getChildAt(0).layoutParams as FrameLayout.LayoutParams
            val naviHeight = try {
                c.resources.getDimensionPixelSize(  c.resources.getIdentifier("navigation_bar_height", "dimen", "android") )
            } catch (e:NullPointerException) {
                0
            }
//            sbParams.setMargins(sbParams.leftMargin,sbParams.topMargin,sbParams.rightMargin,sbParams.bottomMargin+ScreenUtils.getNavigationBarHeight(c))
            val sbBottom = sbParams.bottomMargin + naviHeight + 30
            sbParams.setMargins(sbParams.leftMargin,sbParams.topMargin,sbParams.rightMargin,sbBottom)
            sbView.getChildAt(0).layoutParams = sbParams

            //
            snackbar!!.show()
        }

    }


//// 비트맵에 동그라미 마스크 씌워줌
//    fun getRoundedBitmap(bitmap:Bitmap):Bitmap {
//        val output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(output)
//        val color = Color.YELLOW
//        val paint = Paint()
//        val rect = Rect(0,0,bitmap.width,bitmap.height)
//        val rectF = RectF()
//        paint.isAntiAlias = true
//        canvas.drawARGB(0,0,0,0)
//        paint.color = color
//        canvas.drawOval(rectF,paint)
//        paint.setXfermode( PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
//        canvas.drawBitmap(bitmap,rect,rect,paint)
//        bitmap.recycle()
//        return output
//    }

}