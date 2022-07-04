package kr.tangram.smartgym.ui.login.join

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityJoinSalfAuthBinding
import kr.tangram.smartgym.ui.login.LoginViewModel
import kr.tangram.smartgym.util.BackgroundRoundShape

class JoinSelfAuthActivity : BaseActivity<ActivityJoinSalfAuthBinding, LoginViewModel>(
    R.layout.activity_join_salf_auth) {
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinSalfAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow)
        binding.header.collapsingToolbar.title = getString(R.string.login_certification)

        binding.btnCertificationMobile.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))
        binding.btnCertificationMobile.setOnClickListener { startActivity(Intent(this, JoinActivity::class.java)) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}