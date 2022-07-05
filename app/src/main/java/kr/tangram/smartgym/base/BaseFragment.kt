package kr.tangram.smartgym.base

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import butterknife.ButterKnife
import com.hwangjr.rxbus.RxBus
import kr.tangram.smartgym.BR
import kr.tangram.smartgym.ble.BluetoothService
import kr.tangram.smartgym.ble.SmartRopeManager
import kr.tangram.smartgym.ui.workout.rope.BasicCountFragment
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
        RxBus.get().register(this)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.workOutState.observe(viewLifecycleOwner) {
            jumpStart()
        }
    }


    open fun init() {
        val intent = Intent(activity, BluetoothService::class.java)
        activity?.startService(intent)
        viewModel.startScanBase()
    }


    open fun initLiveData() {}

    open fun initListener() {}

    private fun jumpStart() {
        if (isAdded) {
            binding.root
            BasicCountFragment.newInstance().run {
//                show(childFragmentManager, BasicCountFragment::class.java.simpleName)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearRelease()
        RxBus.get().unregister(this);
    }
}