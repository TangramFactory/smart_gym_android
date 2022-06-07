package kr.tangram.smartgym.ui.login

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityJoinBinding
import kr.tangram.smartgym.util.BackgroundRoundShape

class JoinActivity : BaseActivity<ActivityJoinBinding, LoginViewModel>(R.layout.activity_join) {
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityJoinBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnEmailSend.background = BackgroundRoundShape.fill("#3BA1FF")
        binding.btnRecertification.paintFlags = binding.btnRecertification.paintFlags or  Paint.UNDERLINE_TEXT_FLAG
        binding.btnEmailSend.setOnClickListener{
            if ( binding.edtJoinEmail.text.isNullOrEmpty()){
                binding.tvSignUpFailScript.text = "이메일을 입력해주세요."
                binding.tvSignUpFailScript.visibility = View.VISIBLE
                binding.tvSignUpFailScript.setTextColor(Color.parseColor("#FF6681"))
                binding.tvSignUpFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_fail_maker), null, null,null)
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher( binding.edtJoinEmail.text.toString()).matches()){
                //이메일 패턴 아님!
                binding.tvSignUpFailScript.text = "이메일 형식이 아닙니다."
                binding.tvSignUpFailScript.visibility = View.VISIBLE
                binding.tvSignUpFailScript.setTextColor(Color.parseColor("#FF6681"))
                binding.tvSignUpFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_fail_maker), null, null,null)
                return@setOnClickListener
            }
            binding.btnRecertification.visibility = View.VISIBLE
            binding.tvSignUpFailScript.text = "전송이 완료되었습니다. 이메일을 확인하여주세요."
            binding.tvSignUpFailScript.visibility = View.VISIBLE
            binding.tvSignUpFailScript.setTextColor(Color.parseColor("#3BA1FF"))
            binding.tvSignUpFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_success_mark), null, null,null)
            viewModel.sendEmail( binding.edtJoinEmail.text.toString())
            binding.btnEmailSend.isEnabled = false
        }

        binding.btnRecertification.setOnClickListener {
            binding.edtJoinEmail.text.clear()
            binding.btnEmailSend.isEnabled = true
        }

    }
}