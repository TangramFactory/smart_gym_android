package kr.tangram.smartgym.ui.certificate

import androidx.activity.viewModels
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityCeritilcateBinding

class CertificationActivity : BaseActivity<ActivityCeritilcateBinding, CertificationViewModel>(R.layout.activity_ceritilcate) {
    override val viewModel: CertificationViewModel by viewModels()
}