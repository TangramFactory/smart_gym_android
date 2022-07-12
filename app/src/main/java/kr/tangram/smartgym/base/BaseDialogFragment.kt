package kr.tangram.smartgym.base

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hwangjr.rxbus.RxBus
import kr.tangram.smartgym.BR
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ble.BluetoothService
import org.koin.core.component.KoinComponent

abstract class BaseDialogFragment<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layoutResId: Int,
) : DialogFragment() , KoinComponent {
    lateinit var binding: B
    abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        val view: View = binding.getRoot()

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            setVariable(BR.vm, viewModel)
            lifecycleOwner = viewLifecycleOwner
        }


        init()
        initLiveData()
        initListener()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.fragment_dialog_back_transparen)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val layoutParams = bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.height = CoordinatorLayout.LayoutParams.MATCH_PARENT
                bottomSheet.layoutParams = layoutParams

                BottomSheetBehavior.from(bottomSheet).run {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                    isHideable = true
                }

            }
        }
        return bottomSheetDialog
    }

    open fun init() {}
    open fun initLiveData() {}
    open fun initListener() {}
}