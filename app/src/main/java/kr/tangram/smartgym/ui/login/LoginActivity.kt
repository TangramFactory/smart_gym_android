package kr.tangram.smartgym.ui.login


import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityLoginBinding
import kr.tangram.smartgym.ui.certificate.CertificationActivity
import kr.tangram.smartgym.ui.main.MainActivity
import kr.tangram.smartgym.util.BackgroundRoundShape
import org.koin.androidx.viewmodel.ext.android.getViewModel


class LoginActivity: BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailLink = intent.data.toString()
        binding.btnJoin.background = BackgroundRoundShape.fill("#444444")
        binding.btnEmailLink.background = BackgroundRoundShape.fill("#3BA1FF")
        viewModel.checkLogin(emailLink,
            savedUserCallback = {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                this.finish()
            },
            notSavedUserCallback = {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                this.finish()
            })


        binding.btnJoin.setOnClickListener { startActivity(Intent(this, CertificationActivity::class.java)) }

        binding.btnEmailLink.setOnClickListener{
            if ( binding.edtEmail.text.isNullOrEmpty()){
                Log.d("click", "~")
                binding.tvLoginFailScript.setText("이메일을 입력하여주세요.")
                binding.tvLoginFailScript.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher( binding.edtEmail.text.toString()).matches()){
                //이메일 패턴 아님!
                binding.tvLoginFailScript.text = "이메일 형식이 아닙니다."
                binding.tvLoginFailScript.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!viewModel.isSavedUser(binding.edtEmail.text.toString())){
               //인증페이지로 넘어가야함
                binding.tvLoginFailScript.text = "회원이 아닙니다. 회원가입을 해주세요"
                binding.tvLoginFailScript.visibility = View.VISIBLE
                return@setOnClickListener
            }

            viewModel.sendEmail(binding.edtEmail.text.toString())
            viewModel.saveEmail(binding.edtEmail.text.toString())

            binding.btnEmailLink.isEnabled = false
        }
    }
}