package kr.tangram.smartgym.ui.login


import android.content.Intent
import android.graphics.Color

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityLoginBinding
import kr.tangram.smartgym.ui.certificate.CertificationActivity
import kr.tangram.smartgym.ui.device.DeviceInfoActivity
import kr.tangram.smartgym.ui.device.DeviceManagerActivity
import kr.tangram.smartgym.ui.device.DeviceNameActivity
import kr.tangram.smartgym.ui.device.DeviceScanActivity
import kr.tangram.smartgym.util.BackgroundRoundShape
import kr.tangram.smartgym.util.ReceiveEmail
import org.koin.androidx.viewmodel.ext.android.getViewModel


class LoginActivity: BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnJoin.background = BackgroundRoundShape.fill("#444444")
        binding.btnEmailLink.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))

        binding.btnJoin.setOnClickListener { startActivity(Intent(this, CertificationActivity::class.java)) }
        binding.tvLoginFailScript.compoundDrawablePadding = 10
        binding.btnEmailLink.setOnClickListener{
            if ( binding.edtEmail.text.isNullOrEmpty()){
                binding.tvLoginFailScript.setText("이메일을 입력하여주세요.")
                binding.tvLoginFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_fail_maker), null, null,null)
                binding.tvLoginFailScript.visibility = View.VISIBLE

                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher( binding.edtEmail.text.toString()).matches()){
                //이메일 패턴 아님!
                binding.tvLoginFailScript.text = "이메일 형식이 아닙니다."
                binding.tvLoginFailScript.visibility = View.VISIBLE
                binding.tvLoginFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_fail_maker), null, null,null)
                return@setOnClickListener
            }

            if (!viewModel.isSavedUser(binding.edtEmail.text.toString())){
               //인증페이지로 넘어가야함
                binding.tvLoginFailScript.text = "회원이 아닙니다. 회원가입을 해주세요"
                binding.tvLoginFailScript.visibility = View.VISIBLE
                binding.tvLoginFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_fail_maker), null, null,null)
                return@setOnClickListener
            }

            viewModel.sendEmail(binding.edtEmail.text.toString(), ReceiveEmail.LOGIN)
            viewModel.saveEmail(binding.edtEmail.text.toString())
            binding.btnEmailLink.isEnabled = false

            startActivity(Intent(this, LoginEmailSentActivity::class.java))
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = getString(R.string.login_email_login)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}