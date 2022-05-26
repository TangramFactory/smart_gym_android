package kr.tangram.smartgym.ui.login


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityLoginBinding
import kr.tangram.smartgym.ui.certificate.CertificationActivity
import kr.tangram.smartgym.ui.main.MainActivity
import kr.tangram.smartgym.util.BackgroundRoundShape


class LoginActivity: BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val emailLink = intent.data.toString()
        binding.btnSignIn.background = BackgroundRoundShape.fill("#3BA1FF")
        binding.btnEmailLink.background = BackgroundRoundShape.fillStroke(Color.WHITE.toString(), "#000000", 1f)
        viewModel.checkLogin(emailLink){ startActivity(Intent(this, MainActivity::class.java)) }
        binding.btnSignIn.setOnClickListener { startActivity(Intent(this, CertificationActivity::class.java)) }

        binding.btnEmailLink.setOnClickListener{
            if ( binding.edtEmail.text.isNullOrEmpty()){
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher( binding.edtEmail.text.toString()).matches()){
                //이메일 패턴 아님!
                    binding.tvLoginFailScript.text = "이메일 형식이 아닙니다."
                return@setOnClickListener
            }

            if (!viewModel.isCertifiedUser(binding.edtEmail.text.toString())){
               //인증페이지로 넘어가야함
                binding.tvLoginFailScript.text = "회원이 아닙니다. 회원가입을 해주세요"
                return@setOnClickListener
            }

            viewModel.sendEmail( binding.edtEmail.text.toString())
            binding.btnEmailLink.isEnabled = false
        }



    }


}