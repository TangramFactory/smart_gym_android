package kr.tangram.smartgym.ui.login.join

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.databinding.BindingAdapter
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityJoinGuideBinding
import kr.tangram.smartgym.ui.login.LoginViewModel
import kr.tangram.smartgym.util.BackgroundRoundShape



class JoinGuideActivity :
    BaseActivity<ActivityJoinGuideBinding, JoinViewModel>(R.layout.activity_join_guide) {
    override val viewModel: JoinViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.btnAge14.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))
        binding.btnNext.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))
        binding.btnNext.setOnClickListener { startActivity(Intent(this, JoinSelfAuthActivity::class.java)) }
        binding.btnAge14.setOnClickListener { startActivity(Intent(this, JoinGuide14UnderActivity::class.java))}


        binding.btnChechAge.setOnClickListener { goWebViewActivity(CHECK_AGE) }
        binding.btnPersonalData.setOnClickListener { goWebViewActivity(PERSONAL_DATA) }
        binding.btnTerms.setOnClickListener { goWebViewActivity(TERMS) }
        binding.btnLocationInfo.setOnClickListener { goWebViewActivity(LOCATION_INFO) }

        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow)
        binding.header.collapsingToolbar.title = getString(R.string.login_join)
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

    private fun goWebViewActivity(str: String) {
        startActivity(Intent(this, CertificationWebViewActivity::class.java).putExtra(PAGE, str))
    }

    companion object {
        const val PAGE = "page"
        const val CHECK_AGE = "checkAge"
        const val PERSONAL_DATA = "Terms"
        const val TERMS = "PersonalData"
        const val LOCATION_INFO = "LocationInfo"

        @JvmStatic
        @BindingAdapter("app:srcCompat")
        fun setImageSrc(view : ImageView, src: Drawable){
            view.setImageDrawable(src)
        }
    }




}