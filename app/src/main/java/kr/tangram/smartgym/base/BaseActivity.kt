package kr.tangram.smartgym.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kr.tangram.smartgym.BR

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layoutResId: Int
) : AppCompatActivity() {

    lateinit var binding: B

    abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.setVariable(BR.vm, viewModel)
        binding.lifecycleOwner = this

        init()
        initLiveData()
        initListener()
    }

    open fun init() {}

    open fun initLiveData() {}

    open fun initListener() {}

    fun showToast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}