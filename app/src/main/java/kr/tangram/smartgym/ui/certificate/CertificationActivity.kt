package kr.tangram.smartgym.ui.certificate

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityCertificateBinding
import kr.tangram.smartgym.util.BackgroundRoundShape



class CertificationActivity :
    BaseActivity<ActivityCertificateBinding, CertificationViewModel>(R.layout.activity_certificate) {
    override val viewModel: CertificationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.btnAge14.background = BackgroundRoundShape.fill("#3BA1FF")
        binding.btnNext.background = BackgroundRoundShape.fill("#3BA1FF")
        binding.btnNext.setOnClickListener { startActivity(Intent(this, CertificationDetailActivity::class.java)) }
        binding.btnAge14.setOnClickListener { startActivity(Intent(this, Certification14AgeActivity::class.java))}


        binding.btnChechAge.setOnClickListener { goWebViewActivity(CHECK_AGE) }
        binding.btnPersonalData.setOnClickListener { goWebViewActivity(PERSONAL_DATA) }
        binding.btnTerms.setOnClickListener { goWebViewActivity(TERMS) }
        binding.btnLocationInfo.setOnClickListener { goWebViewActivity(LOCATION_INFO) }


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = getString(R.string.login_join)
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