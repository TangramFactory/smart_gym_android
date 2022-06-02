package kr.tangram.smartgym.view


import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import kr.tangram.smartgym.R
import kr.tangram.smartgym.databinding.LayoutPopDialogBinding
import kr.tangram.smartgym.util.KAccelerateDecelerateInterpolator
import kr.tangram.smartgym.util.htmlCompact
import kr.tangram.smartgym.util.touchAlphaAction

class ViewDialogPop:ConstraintLayout {

    //
    private val tag: String = "ViewDialogPop"

    //
    var buttonRight:Button
    var buttonLeft:Button

    //
    var leftAction:()->Unit = {}
    var rightAction:()->Unit = {}
    lateinit var  bind : LayoutPopDialogBinding

    //
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        bind = LayoutPopDialogBinding.inflate(LayoutInflater.from(context))
        // 레이아웃 붙여 넣기
//        val inflater = LayoutInflater.from(context)
//        val layout = inflater.inflate(R.layout.layout_pop_dialog,this,false) as ConstraintLayout
        addView(bind.root, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))


        //
        bind.layoutBackground.setBackgroundColor(Color.argb(60,0,0,0))
        bind.layoutBackground.alpha = 0.0f
        bind.layoutBackground.animate().alpha(1.0f).setDuration(200).interpolator = KAccelerateDecelerateInterpolator()

        //
        bind.layoutPop.scaleX = 0.7f
        bind.layoutPop.scaleY = 0.7f
        bind.layoutPop.alpha = 0.0f
        bind.layoutPop.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setStartDelay(100).setDuration(160).interpolator = KAccelerateDecelerateInterpolator()

        buttonRight = bind.buttonRight
        buttonLeft = bind.buttonLeft

        //
        touchAlphaAction(bind.buttonRight,0.2f){
            dismiss(rightAction)
        }

        touchAlphaAction(bind.buttonLeft,0.2f){
            dismiss(leftAction)
        }

        touchAlphaAction(bind.layoutBackground,1.0f){}
    }

    //
    fun setTouchBackgroundCloseLeftAction(){
        touchAlphaAction(bind.layoutBackground,1.0f) {
            dismiss(leftAction)
        }
    }
    fun setTouchBackgroundCloseRightAction(){
        touchAlphaAction(bind.layoutBackground,1.0f) {
            dismiss(rightAction)
        }
    }

    //
    fun backgroundBlurDisable() {
        bind.layoutBackground.setBackgroundColor(Color.argb(220,0,0,0))
        bind.layoutBackground.getViewById(R.id.realtimeBlurView).visibility = View.GONE
    }

    //
    fun setNoTitle() {
        bind.textTitle.visibility = View.GONE
//        divide.visibility = View.GONE
    }

    //
    fun dismiss(u:()->Unit) {
        bind.layoutBackground.animate().alpha(0.0f).setStartDelay(100).setDuration(160).interpolator = KAccelerateDecelerateInterpolator()
        bind.layoutPop.animate().scaleX(0.7f).scaleY(0.7f).alpha(0.0f).setDuration(160).setInterpolator(
            KAccelerateDecelerateInterpolator()).withEndAction {
            u()
            (this.parent as ViewGroup).removeView(this)
        }
    }


    // 제목
    var title:String = "title"
        get() = if (field.isNotEmpty()) field else "title"
        set (value) {
            if (value.isNotEmpty()) field = value else ""
            bind.textTitle.text = value
        }
    //
    var HtmlTitle:String = "description"
        get() = if (field.isNotEmpty()) field else "title"
        set (value) {
            if (value.isNotEmpty()) field = value else ""
            bind.textTitle.text = HtmlCompat.fromHtml(value,HtmlCompat.FROM_HTML_MODE_COMPACT)
                //Html.fromHtml(value)
            // htmlCompact(value)//
        }

    //
    var description:String = "description"
        get() = if (field.isNotEmpty()) field else "title"
        set (value) {
            if (value.isNotEmpty()) field = value else ""
            bind.textDescription.text = value
        }

    //
    var HtmlDescription:String = "description"
        get() = if (field.isNotEmpty()) field else "title"
        set (value) {
            if (value.isNotEmpty()) field = value else ""
            bind.textDescription.text = htmlCompact(value)//Html.fromHtml(value)
        }

    //
    var left:String = "cancel"
        get() = if (field.isNotEmpty()) field else "title"
        set (value) {
            if (value.isNotEmpty()) field = value else ""
            bind.buttonLeft.text = value
        }

    //
    var right:String = "cancel"
        get() = if (field.isNotEmpty()) field else "title"
        set (value) {
            if (value.isNotEmpty()) field = value else ""
            bind.buttonRight.text = value
        }

    //
    fun leftDisable(b:Boolean) {
        if(b) {
            bind.buttonLeft.visibility = View.GONE
        } else {
            bind.buttonLeft.visibility = View.VISIBLE
        }
    }
    fun leftDisable() {
        bind.buttonLeft.visibility = View.GONE
    }
    fun leftEnable() {
        bind.buttonLeft.visibility = View.VISIBLE
    }

    fun rightDisable(b:Boolean) {
        if(b) {
            bind.buttonRight.visibility = View.GONE
        } else {
            bind.buttonRight.visibility = View.VISIBLE
        }
    }
    fun rightDisable() {
        bind.buttonRight.visibility = View.GONE
    }
    fun rightEable() {
        bind.buttonRight.visibility = View.VISIBLE
    }

    fun titleDisable(){
        bind.textTitle.visibility = View.GONE
    }
    fun titleEnable(){
        bind.textTitle.visibility = View.VISIBLE
    }
    fun descriptionDisable() {
        bind.textDescription.visibility = View.GONE
    }
    fun descriptionEnable() {
        bind.textDescription.visibility = View.VISIBLE
    }
}