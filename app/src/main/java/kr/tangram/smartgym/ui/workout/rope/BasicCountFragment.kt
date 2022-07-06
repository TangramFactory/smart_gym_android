package kr.tangram.smartgym.ui.workout.rope

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.tangram.smartgym.R

import kr.tangram.smartgym.databinding.ActivityBasicCountBinding
import kr.tangram.smartgym.ui.workout.WorkOutViewModel

import kr.tangram.smartgym.ui.workout.rope.view.ViewTimerCircle
import kr.tangram.smartgym.util.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.component.KoinComponent
import java.util.*
import kotlin.concurrent.timerTask

class BasicCountFragment : BottomSheetDialogFragment(), KoinComponent {
    val viewModel: WorkOutViewModel by lazy { getViewModel() }
    private var timer: Timer = Timer()
    private var timerCircle: ViewTimerCircle? = null
    private lateinit var binding: ActivityBasicCountBinding

    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        Log.d("오픈", "~")
        binding = ActivityBasicCountBinding.inflate(layoutInflater).apply {
            textCountSwitcher.setFactory {
                val textViewCount = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_textcount, null) as TextView
                textViewCount.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    scWidthPercent(requireContext(), 24f))
                textViewCount.height = 800
                textViewCount
            }

            textCountSwitcher.inAnimation =
                AnimationUtils.loadAnimation(mContext, R.anim.count_fade_in)
            textCountSwitcher.outAnimation =
                AnimationUtils.loadAnimation(mContext, R.anim.count_fade_out)
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        val textLength = viewModel.jumpRopeWorkOut.value?.jump.toString().length // 문자열길이
        //
        binding.textCountSwitcher.animate().alpha(0.0f).setDuration(100).withEndAction {

            val fontSize = when {
                textLength <= 3 -> 24f
                textLength == 4 -> 22f
                textLength == 5 -> 19f
                else -> 17f
            }
            if (isAdded) {
                (binding.textCountSwitcher.getChildAt(1) as TextView).setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    scWidthPercent(mContext, fontSize)
                )
                (binding.textCountSwitcher.getChildAt(0) as TextView).setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    scWidthPercent(mContext, fontSize)
                )
            }

            binding.textCountSwitcher.inAnimation =
                AnimationUtils.loadAnimation(mContext, R.anim.count_jump_in)
            binding.textCountSwitcher.outAnimation =
                AnimationUtils.loadAnimation(mContext, R.anim.count_jump_out)

        }.interpolator = AccelerateInterpolator()

        binding.textCountSwitcher.animate().alpha(1.0f).setStartDelay(150)
            .setDuration(300).interpolator = DecelerateInterpolator()

        binding.buttonClose.setOnClickListener {
            timer.cancel()
            timerCircle?.stop()
            binding.buttonClose.isEnabled = false
            close()
        }

        viewModel.jumpMode.observe(this) { modeChangeCallBack(it) }
        viewModel.jumpRopeWorkOut.observe(this) {
            dataChangedCallBack()
        }

        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.let { it ->
                    setupFullHeight(it)
                    BottomSheetBehavior.from(it).apply {
                        state = BottomSheetBehavior.STATE_EXPANDED
                        isDraggable = false
                    }
                }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun dataChangedCallBack() {
        // 5초 동안 운동하지 않으면 //

        timer.cancel()
        timer = Timer()
        timer.schedule(timerTask {
            Handler(Looper.getMainLooper()).post {
                if (!isDetached) {
                    startTimeOut(mContext)
                }
            }
        }, 5000)

        timerCircle?.stop()
        binding.popCounterAnim.jumping()
    }

    //버튼 애니메이션 동작
    @RequiresApi(Build.VERSION_CODES.M)
    private fun startTimeOut(context: Context) {

        timerCircle = ViewTimerCircle(context)
        binding.layoutButton.addView(timerCircle)
        timerCircle?.targetMilliseconds = 7000 // 10초의 시간을 더 주겠다 !!
        timerCircle?.onComplete = {
            Log.d("timer", "COMPLETE ")
            timerCircle?.stop()
            //
//            SoundEffect.play.pop(AppSetting.sound_jump / 100f)
            close()
        }
        timerCircle?.start()

    }


    private fun modeChangeCallBack(jumpMode: WorkOutViewModel.JumpMode) {
        var img = mContext.getDrawable(R.drawable.ic_home_basiccount_jump)
        var indicatorImg = R.drawable.ic_basic_count_indicator_01
        var modeText = "JUMP"
        var leftLabel = "Speed"
        var rightLabel = "Calories"
        when (jumpMode) {
            WorkOutViewModel.JumpMode.JUMP ->{
                img = mContext.getDrawable(R.drawable.ic_home_basiccount_jump)
                indicatorImg = R.drawable.ic_basic_count_indicator_01
                modeText = "JUMP"
                leftLabel = "Speed"
                rightLabel = "Calories"
            }
            WorkOutViewModel.JumpMode.SPEED -> {
                img = mContext.getDrawable(R.drawable.ic_speed)
                indicatorImg = R.drawable.ic_basic_count_indicator_02
                modeText = "SPEED"
                leftLabel = "Jump"
                rightLabel = "Calories"
            }
            WorkOutViewModel.JumpMode.CALORIE -> {
                img = mContext.getDrawable(R.drawable.ic_home_basiccount_calories)
                indicatorImg = R.drawable.ic_basic_count_indicator_03
                modeText = "CALORIE"
                leftLabel = "Jump"
                rightLabel = "Time"
            }
            WorkOutViewModel.JumpMode.TIME -> {
                img = mContext.getDrawable(R.drawable.ic_home_basiccount_duration)
                indicatorImg = R.drawable.ic_basic_count_indicator_04
                modeText = "TIME"
                leftLabel = "Jump"
                rightLabel = "Calories"
            }
        }
        binding.modeIcon.setImageDrawable(img)
        binding.textMode.text = modeText
        binding.textLeftLabel.text=leftLabel
        binding.textRightLabel.text=rightLabel
        binding.imageIndicator.setImageResource(indicatorImg)
    }

    fun close() {
        timer.cancel()
        timerCircle?.onComplete = {}
        timerCircle = null
        binding.popCounterAnim.animationStop()
        viewModel.saveJumpData()
        viewModel.clearJump()
        viewModel.smartRopeManagerInit()
        if (isAdded) {
            val fragmentManager: FragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().remove(this@BasicCountFragment).commitAllowingStateLoss()
        }
    }

    fun scWidthPercent(c: Context, p: Float): Float {
        return c.resources.displayMetrics.widthPixels * p / 100f
    }


    override fun show(manager: FragmentManager, tag: String?) {
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    override fun onStop() {
        close()
        super.onStop()
    }

    override fun onDestroy() {
        close()
        super.onDestroy()
    }


    companion object {
        @JvmStatic
        fun newInstance() = BasicCountFragment()

        @BindingAdapter("switcher_workOut_bind", "switcher_mode_bind")
        @JvmStatic
        fun switcherTextSetting(textView: TextSwitcher, workOut: LiveData<WorkOutViewModel.JumpWorkOut>, mode : LiveData<WorkOutViewModel.JumpMode>) {
            when(mode.value){
                WorkOutViewModel.JumpMode.JUMP ->{
                    textView.setText(workOut.value?.jump.toString())
                }
                WorkOutViewModel.JumpMode.SPEED ->{
                    textView.setText(workOut.value?.speed.toString())
                }
                WorkOutViewModel.JumpMode.CALORIE ->{
                    textView.setText(workOut.value?.calorie.toString())
                }
                WorkOutViewModel.JumpMode.TIME ->{
                    textView.setText(workOut.value?.time.toString())
                }
            }
        }


        @BindingAdapter("text_view_work_out_bind", "text_view_mode_bind")
        @JvmStatic
        fun textViewLabelSetting(textView: TextView, workOut: LiveData<WorkOutViewModel.JumpWorkOut>, mode : LiveData<WorkOutViewModel.JumpMode>) {
            when(mode.value){
                WorkOutViewModel.JumpMode.JUMP ->{
                    if (textView.id == R.id.text_left_value){
                        textView.text = workOut.value?.speed.toString()
                    }else if (textView.id == R.id.text_right_value ){
                        textView.text = workOut.value?.calorie.toString()
                    }
                }
                WorkOutViewModel.JumpMode.SPEED ->{
                    if (textView.id == R.id.text_left_value){
                        textView.text = workOut.value?.jump.toString()
                    }else if (textView.id == R.id.text_right_value ){
                        textView.text = workOut.value?.calorie.toString()
                    }
                }
                WorkOutViewModel.JumpMode.CALORIE ->{
                    if (textView.id == R.id.text_left_value){
                        textView.text = workOut.value?.jump.toString()
                    }else if (textView.id == R.id.text_right_value ){
                        textView.text = workOut.value?.time.toString()
                    }
                }
                WorkOutViewModel.JumpMode.TIME ->{
                    if (textView.id == R.id.text_left_value){
                        textView.text = workOut.value?.jump.toString()
                    }else if (textView.id == R.id.text_right_value ){
                        textView.text = workOut.value?.calorie.toString()
                    }
                }
            }
        }
    }

}