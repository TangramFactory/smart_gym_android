package kr.tangram.smartgym.ui.workout.rope

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.tangram.smartgym.databinding.ActivityBasicCountBinding
import kr.tangram.smartgym.ui.workout.WorkOutViewModel
import kr.tangram.smartgym.ui.workout.rope.fragment.WorkOutCalorieFragment
import kr.tangram.smartgym.ui.workout.rope.fragment.WorkOutJumpFragment
import kr.tangram.smartgym.ui.workout.rope.fragment.WorkOutSpeedFragment
import kr.tangram.smartgym.ui.workout.rope.fragment.WorkOutTimeFragment
import kr.tangram.smartgym.ui.workout.rope.view.ViewTimerCircle
import kr.tangram.smartgym.util.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent
import java.util.*
import kotlin.concurrent.timerTask

class BasicCountFragment : BottomSheetDialogFragment(), KoinComponent {
    val viewModel: WorkOutViewModel by lazy { getViewModel() }
    private var timer: Timer = Timer()
    private var timerCircle: ViewTimerCircle? = null
    private lateinit var binding: ActivityBasicCountBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = ActivityBasicCountBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.buttonClose.setOnClickListener {
            timer.cancel()
            timerCircle?.stop()
            binding.buttonClose.isEnabled = false
            close()
        }

        viewModel.jumpMode.observe(this) { modeChangeCallBack(it) }

        viewModel.jumpRopeWorkOut.observe(this) { dataChengedCallBack() }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { it ->
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
    private fun dataChengedCallBack() {
        // 5초 동안 운동하지 않으면 //
        timer.cancel()
        timer = Timer()
        timer.schedule(timerTask {
            Handler(Looper.getMainLooper()).post {
                startTimeOut(requireActivity())
            }
        }, 5000)

        timerCircle?.stop()
        binding.popCounterAnim.jumping()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun startTimeOut(context: Context) {
        //
        timerCircle = ViewTimerCircle(context)
        binding.layoutButton.addView(timerCircle)
        timerCircle?.targetMilliseconds = 7000 // 10초의 시간을 더 주겠다 !!
        timerCircle?.onComplete = {
            log("timer", "COMPLETE ")
            timerCircle?.stop()
            //
//            SoundEffect.play.pop(AppSetting.sound_jump / 100f)
            SoundEffect.play.ready(AppSetting.sound_jump / 100f)
            close()
        }
        timerCircle?.start()

        //
        if (AppSetting.sound_jump > 0 && AppSetting.sound_voicecount > 0) {
//            SoundEffect.play.pop(AppSetting.sound_jump / 100f)
            SoundEffect.play.ready(AppSetting.sound_jump / 100f)
//            vc?.countComplete(ropeCount.jump, "autoComplete") {}
        }
    }



    private fun modeChangeCallBack(jumpMode: WorkOutViewModel.JumpMode) {
        when (jumpMode) {
            WorkOutViewModel.JumpMode.JUMP -> childFragmentManager.beginTransaction()
                .replace(binding.frameBasicCount.id, WorkOutJumpFragment.newInstance()).commit()
            WorkOutViewModel.JumpMode.SPEED -> childFragmentManager.beginTransaction()
                .replace(binding.frameBasicCount.id, WorkOutSpeedFragment.newInstance()).commit()
            WorkOutViewModel.JumpMode.CALORIE -> childFragmentManager.beginTransaction()
                .replace(binding.frameBasicCount.id, WorkOutCalorieFragment.newInstance()).commit()
            WorkOutViewModel.JumpMode.TIME -> childFragmentManager.beginTransaction()
                .replace(binding.frameBasicCount.id, WorkOutTimeFragment.newInstance()).commit()
        }
    }

    fun close(){
        if (isAdded){
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().remove(this@BasicCountFragment).commit()
            fragmentManager.popBackStack()
        }
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
        binding.popCounterAnim.animationStop()
        viewModel.saveJumpData()
    }



    companion object {
        @JvmStatic
        fun newInstance() = BasicCountFragment()
    }

}