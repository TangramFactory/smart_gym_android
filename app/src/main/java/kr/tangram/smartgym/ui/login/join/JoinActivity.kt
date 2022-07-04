package kr.tangram.smartgym.ui.login.join

import android.graphics.Paint
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityJoinBinding
import kr.tangram.smartgym.ui.login.LoginViewModel
import kr.tangram.smartgym.util.BackgroundRoundShape
import kr.tangram.smartgym.util.EmailReceiveType

class JoinActivity : BaseActivity<ActivityJoinBinding, JoinViewModel>(R.layout.activity_join) {
    override val viewModel: JoinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityJoinBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);
        binding.header.collapsingToolbar.title = getString(R.string.login_join)

        binding.btnEmailSend.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))
        binding.btnRecertification.paintFlags = binding.btnRecertification.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.btnEmailSend.setOnClickListener{ emailCheck() }

        binding.edtJoinEmail.setOnClickListener {
            binding.edtJoinEmail.background = getDrawable(R.drawable.edt_round_black)
        }

        viewModel.isSaveUserFlag.observe(this){
            if (it) failAction( "이미 가입되어있는 이메일입니다") else successAction()
        }
    }

    private fun emailCheck() {
        checkEmailPatterns(binding.edtJoinEmail.text.trim().toString()).run {
            if (!this) return
        }
        viewModel.savedUserCheck(binding.edtJoinEmail.text.trim().toString())
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

    private fun successAction() {
        binding.btnRecertification.visibility = View.VISIBLE
        binding.edtJoinEmail.background = getDrawable(R.drawable.edt_round_black)
        binding.btnEmailSend.isEnabled = false

        viewModel.sendEmail( binding.edtJoinEmail.text.toString().trim())
        viewModel.saveEmail(binding.edtJoinEmail.text.toString().trim())
    }


    private fun failAction(string: String){
        showCustomToast(string, true)
        binding.edtJoinEmail.background = getDrawable(R.drawable.edt_round_red)
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