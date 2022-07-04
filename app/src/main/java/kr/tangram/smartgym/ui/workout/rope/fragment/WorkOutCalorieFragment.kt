package kr.tangram.smartgym.ui.workout.rope.fragment

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseFragment
import kr.tangram.smartgym.databinding.FragmentWorkOutCalorieBinding
import kr.tangram.smartgym.ui.workout.WorkOutViewModel
import kr.tangram.smartgym.util.scWidthPercent
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WorkOutCalorieFragment :
    BaseFragment<FragmentWorkOutCalorieBinding, WorkOutViewModel>(R.layout.fragment_work_out_calorie) {
    override val viewModel: WorkOutViewModel by sharedViewModel()

    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentWorkOutCalorieBinding.inflate(inflater).apply {
            textCountSwitcher.setFactory {
                val textViewCount : TextView= LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_pop_textcount, null) as TextView
                textViewCount.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    scWidthPercent(mContext, 24f))
                textViewCount.height = 800
                textViewCount
            }

            textCountSwitcher.inAnimation =
                AnimationUtils.loadAnimation(mContext, R.anim.count_fade_in)
            textCountSwitcher.outAnimation =
                AnimationUtils.loadAnimation(mContext, R.anim.count_fade_out)
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        val textLength = viewModel.jumpRopeWorkOut.value?.jump.toString().length // 문자열길이
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
            //
            binding.textCountSwitcher.inAnimation =
                AnimationUtils.loadAnimation(mContext, R.anim.count_jump_in)
            binding.textCountSwitcher.outAnimation =
                AnimationUtils.loadAnimation(mContext, R.anim.count_jump_out)

        }.interpolator = AccelerateInterpolator()

        binding.textCountSwitcher.animate().alpha(1.0f).setStartDelay(150)
            .setDuration(300).interpolator = DecelerateInterpolator()

    return binding.root
}


companion object {
    @JvmStatic
    fun newInstance() = WorkOutCalorieFragment()
}
}