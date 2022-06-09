package kr.tangram.smartgym.ui.certificate

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
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

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = getString(R.string.login_certification)
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