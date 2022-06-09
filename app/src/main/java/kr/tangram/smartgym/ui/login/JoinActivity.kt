package kr.tangram.smartgym.ui.login

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityJoinBinding
import kr.tangram.smartgym.util.BackgroundRoundShape
import kr.tangram.smartgym.util.ReceiveEmail

class JoinActivity : BaseActivity<ActivityJoinBinding, LoginViewModel>(R.layout.activity_join) {
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityJoinBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnEmailSend.background = BackgroundRoundShape.fill("#3BA1FF")
        binding.btnRecertification.paintFlags = binding.btnRecertification.paintFlags or  Paint.UNDERLINE_TEXT_FLAG
        binding.tvJoinFailScript.compoundDrawablePadding = 10
        binding.btnEmailSend.setOnClickListener{
            if ( binding.edtJoinEmail.text.isNullOrEmpty()){
                binding.tvJoinFailScript.text = "이메일을 입력해주세요."
                binding.tvJoinFailScript.visibility = View.VISIBLE
                binding.tvJoinFailScript.setTextColor(Color.parseColor("#FF6681"))
                binding.tvJoinFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_fail_maker), null, null,null)
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher( binding.edtJoinEmail.text.toString()).matches()){
                //이메일 패턴 아님!
                binding.tvJoinFailScript.text = "이메일 형식이 아닙니다."
                binding.tvJoinFailScript.visibility = View.VISIBLE
                binding.tvJoinFailScript.setTextColor(Color.parseColor("#FF6681"))
                binding.tvJoinFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_fail_maker), null, null,null)
                return@setOnClickListener
            }

            binding.btnRecertification.visibility = View.VISIBLE
            binding.tvJoinFailScript.text = "인증메일 발송완료!"
            binding.tvJoinFailScript.visibility = View.VISIBLE
            binding.tvJoinFailScript.setTextColor(Color.parseColor("#3BA1FF"))
            binding.tvJoinFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_success_mark), null, null,null)
            viewModel.sendEmail( binding.edtJoinEmail.text.toString(), ReceiveEmail.JOIN)
            binding.btnEmailSend.isEnabled = false
        }

        binding.btnRecertification.setOnClickListener {
            binding.edtJoinEmail.text.clear()
            binding.btnEmailSend.isEnabled = true
            binding.tvJoinFailScript.visibility = View.INVISIBLE
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = getString(R.string.login_join)
        collapsingToolbar
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