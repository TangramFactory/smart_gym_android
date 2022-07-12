package kr.tangram.smartgym.ui.login


import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.base.BaseApplication
import kr.tangram.smartgym.databinding.ActivityLoginBinding
import kr.tangram.smartgym.ui.login.join.JoinGuideActivity
import kr.tangram.smartgym.ui.main.MainActivity
import kr.tangram.smartgym.ui.device.DeviceInfoActivity
import kr.tangram.smartgym.ui.device.DeviceManagerActivity
import kr.tangram.smartgym.ui.device.DeviceNameActivity
import kr.tangram.smartgym.ui.device.DeviceScanActivity
import kr.tangram.smartgym.ui.workout.RopeSyncActivity
import kr.tangram.smartgym.ui.workout.RopeSyncFragment
import kr.tangram.smartgym.util.BackgroundRoundShape
import kr.tangram.smartgym.util.EmailReceiveType
import org.koin.androidx.viewmodel.ext.android.getViewModel


class LoginActivity: BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.header.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);
        binding.header.collapsingToolbar.title = getString(R.string.login_email_login)


        startActivity(Intent(this, DeviceManagerActivity::class.java))


            //자동 로그인
        viewModel.checkLogin(){
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnEmailLink.isEnabled=true
        binding.btnJoin.background = BackgroundRoundShape.fill("#444444")
        binding.btnEmailLink.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))

        binding.btnJoin.setOnClickListener { startActivity(Intent(this, JoinGuideActivity::class.java)) }
        binding.btnEmailLink.setOnClickListener{ emailCheck() }


        binding.btnJoin.setOnLongClickListener {
            goMain()
            true
        }

        binding.edtEmail.setOnClickListener {
            binding.edtEmail.background = getDrawable(R.drawable.edt_round_black)
        }

        viewModel.isSaveUserFlag.observe(this){
            Log.d("이메일 체크", it.toString())
            saveUserCallback(it) }
    }

    private fun goMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun emailCheck() {
        checkEmailPatterns(binding.edtEmail.text.trim().toString()).run {
            if (!this) return
        }

        viewModel.savedUserCheck(binding.edtEmail.text.trim().toString())

        binding.btnEmailLink.isEnabled = false
    }


    private fun checkEmailPatterns(email: String): Boolean {
        if ( email.isEmpty()){
            failAction("이메일을 입력하여주세요.")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            failAction("이메일 형식이 아닙니다.")
            return false
        }
        return true
    }

    private fun failAction(string: String){
        showCustomToast(string, true)
        binding.edtEmail.background = getDrawable(R.drawable.edt_round_red)
    }


    private fun saveUserCallback(isSaveUser: Boolean) {
        if (isSaveUser) {
            viewModel.sendEmail(binding.edtEmail.text.toString().trim(), EmailReceiveType.Login)
            viewModel.saveEmail(binding.edtEmail.text.toString().trim())
            binding.edtEmail.background = getDrawable(R.drawable.edt_round_black)
            startActivity(Intent(this, LoginEmailSentActivity::class.java))
        }else{
            binding.btnEmailLink.isEnabled = true
            failAction( "가입되지 않은 이메일입니다")
        }
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