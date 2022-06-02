package kr.tangram.smartgym.ui.certificate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityCertificateDetailBinding
import kr.tangram.smartgym.ui.login.SignUpActivity
import kr.tangram.smartgym.util.BackgroundRoundShape

class CertificationDetailActivity : BaseActivity<ActivityCertificateDetailBinding, CertificationViewModel>(
    R.layout.activity_certificate_detail) {
    override val viewModel: CertificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCertificationKakao.background = BackgroundRoundShape.fill("#FAE300")
        binding.btnCertificationMobile.background = BackgroundRoundShape.fill("#444444")

        binding.btnCertificationKakao.setOnClickListener { Log.d("클릭", "~") }
        binding.btnCertificationMobile.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }
    }
}