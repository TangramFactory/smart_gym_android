package kr.tangram.smartgym.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.hwangjr.rxbus.RxBus
import kr.tangram.smartgym.BR
import kr.tangram.smartgym.ble.BluetoothService
import org.koin.core.component.KoinComponent

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layoutResId: Int,
) : Fragment(), KoinComponent {
    lateinit var binding: B

    abstract val viewModel: VM


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        val view: View = binding.getRoot()
        ButterKnife.bind(this, view)
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


    open fun init() {
    }

    open fun initLiveData() {}

    open fun initListener() {}


    override fun onDestroy() {
        super.onDestroy()
    }
}