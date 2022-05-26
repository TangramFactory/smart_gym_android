package kr.tangram.smartgym.ui.certificate

import androidx.activity.viewModels
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityCeritilcateDetailBinding

class CertificationDetailActivity : BaseActivity<ActivityCeritilcateDetailBinding, CertificationViewModel>(
    R.layout.activity_ceritilcate_detail) {
    override val viewModel: CertificationViewModel by viewModels()
}