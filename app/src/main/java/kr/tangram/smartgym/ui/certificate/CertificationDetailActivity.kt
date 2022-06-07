package kr.tangram.smartgym.ui.certificate

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityCertificateDetailBinding
import kr.tangram.smartgym.ui.login.JoinActivity
import kr.tangram.smartgym.util.BackgroundRoundShape

class CertificationDetailActivity : BaseActivity<ActivityCertificateDetailBinding, CertificationViewModel>(
    R.layout.activity_certificate_detail) {
    override val viewModel: CertificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCertificationMobile.background = BackgroundRoundShape.fill("#3BA1FF")
        binding.btnCertificationMobile.setOnClickListener { startActivity(Intent(this, JoinActivity::class.java)) }
    }
}