package kr.tangram.smartgym.base


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import butterknife.ButterKnife
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import kr.tangram.smartgym.BR
import kr.tangram.smartgym.R
import kr.tangram.smartgym.UserViewModel
import kr.tangram.smartgym.base.BaseApplication.Companion.context
import kr.tangram.smartgym.ble.BluetoothService
import kr.tangram.smartgym.ble.SmartRopeManager
import kr.tangram.smartgym.databinding.LayoutCustomToastBinding
import kr.tangram.smartgym.ui.device.DeviceManagerActivity
import kr.tangram.smartgym.ui.login.LoginCompleteActivity
import kr.tangram.smartgym.ui.main.map.BottomDialogFragment
import kr.tangram.smartgym.ui.workout.RopeSyncActivity
import kr.tangram.smartgym.ui.workout.WorkOutViewModel
import kr.tangram.smartgym.ui.workout.rope.BasicCountFragment
import kr.tangram.smartgym.util.Define
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.component.KoinComponent
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layoutResId: Int,
) : AppCompatActivity(), KoinComponent {
    lateinit var binding: B

    abstract val viewModel: VM

    private val baseViewModel : BaseViewModel by lazy { getViewModel() }
    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val PERMISSIONS_S_ABOVE = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private  val REQUEST_ALL_PERMISSION = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.setVariable(BR.vm, viewModel)
        binding.lifecycleOwner = this

        ButterKnife.bind(this)

        viewModel.networkState.observe(this) {
            when (it) {
                is NetworkResult.FailToast -> showCustomToast(it.message, false)
                is NetworkResult.SuccessToast -> showCustomToast(it.message, false)
                is NetworkResult.UserCreateFail -> showCustomToast(it.message, false)
            }
        }
        viewModel.workOutState.observe(this) {
            if (it >=0)
            jumpStart()
        }

        viewModel.historySync.observe(this){
            val intent = Intent(this, RopeSyncActivity::class.java).apply {
                putExtra(Define.Extra.Identifier, it!!)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            }
            startActivity(intent)
        }

        init()
        initLiveData()
        initListener()
    }

    open fun init() {
//        val intent = Intent(getApplicationContext(), BluetoothService::class.java)
//        startService(intent)
    }

    open fun initLiveData() {}

    open fun initListener() {}

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showToast(message: String) =
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

    fun showCustomToast(message: String, showImgFlag: Boolean) {
        val binding = LayoutCustomToastBinding.inflate(layoutInflater)
        binding.tvToastMessage.text = message
        if (showImgFlag) {
            binding.tvToastMessage.compoundDrawablePadding = 10
            binding.tvToastMessage.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_fail_marker),
                null,
                null,
                null)
        }

        val toast = Toast(this).apply {
            setGravity(Gravity.TOP or Gravity.CENTER, 0, 200)
            duration = Toast.LENGTH_SHORT
            view = binding.root
        }

        toast.show()
    }


    fun jumpStart() {
        BasicCountFragment.newInstance().run {
            if (!isAdded) show(supportFragmentManager, BasicCountFragment::class.java.simpleName)
        }
    }
}

sealed class NetworkResult() {
    data class UserCreateFail(val message: String) : NetworkResult()
    data class FailToast(val message: String) : NetworkResult()
    data class SuccessToast(val message: String) : NetworkResult()
}