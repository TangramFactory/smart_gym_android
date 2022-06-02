package kr.tangram.smartgym.util.func

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Spanned
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.util.htmlCompact


class Frank {

    //
    companion object {

        private const val tag = "Frank"
        @SuppressLint("StaticFieldLeak")
        private var context:Context? = null

        //
        fun init(c: Context) {
            context = c
        }

        //
        fun show(t:String) {
            try {
                // kotlin.TypeCastException: null cannot be cast to non-null type android.app.Activity
                show(htmlCompact(t))
            } catch (e:TypeCastException) {
                show("")
            }
        }

        //
        @SuppressLint("InflateParams")
        fun show(t:Spanned) {

            val layout = (context as Activity).layoutInflater.inflate(R.layout.layout_frank,null) as ConstraintLayout?

            val text = layout!!.findViewById(R.id.text) as TextView
            text.text = t

//            val toast = Toast(getApplicationContext())
//            toast.setGravity(Gravity.BOTTOM, 0, toPx(context as Activity,120f))
//            toast.duration = Toast.LENGTH_LONG

            //
//            toast.view = layout
//            (toast.view as ConstraintLayout).translationY = 100.0f
//            (toast.view as ConstraintLayout).animate().translationY(0.0f).setDuration(300).setInterpolator(OvershootInterpolator()).start()
//
//            toast.show()

        }
    }
}