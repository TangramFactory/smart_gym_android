package kr.tangram.smartgym.ui.login

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivitySignupBinding
import kr.tangram.smartgym.util.BackgroundRoundShape

class SignUpActivity : BaseActivity<ActivitySignupBinding, LoginViewModel>(R.layout.activity_signup) {
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignupBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnEmailSend.background = BackgroundRoundShape.fill("#3BA1FF")
        binding.btnEmailSend.setOnClickListener{
            if ( binding.edtSignUpEmail.text.isNullOrEmpty()){
                binding.tvSignUpFailScript.text = "이메일을 입력해주세요."
                binding.tvSignUpFailScript.visibility = View.VISIBLE
                binding.tvSignUpFailScript.setTextColor(Color.parseColor("#FF6681"))
                binding.tvSignUpFailScript.setCompoundDrawables(null, null, null,null)
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher( binding.edtSignUpEmail.text.toString()).matches()){
                //이메일 패턴 아님!
                binding.tvSignUpFailScript.text = "이메일 형식이 아닙니다."
                binding.tvSignUpFailScript.visibility = View.VISIBLE
                binding.tvSignUpFailScript.setTextColor(Color.parseColor("#FF6681"))
                binding.tvSignUpFailScript.setCompoundDrawables(null, null, null,null)
                return@setOnClickListener
            }

            binding.tvSignUpFailScript.text = "전송이 완료되었습니다. 이메일을 확인하여주세요."
            binding.tvSignUpFailScript.visibility = View.VISIBLE
            binding.tvSignUpFailScript.setTextColor(Color.parseColor("#3BA1FF"))
            binding.tvSignUpFailScript.setCompoundDrawables(getDrawable(R.drawable.ic_checkbox_history_checked), null, null,null)
            viewModel.sendEmail( binding.edtSignUpEmail.text.toString())
            binding.btnEmailSend.isEnabled = false
        }

    }
}