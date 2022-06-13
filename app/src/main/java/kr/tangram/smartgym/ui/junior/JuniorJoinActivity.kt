package kr.tangram.smartgym.ui.junior

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityJuniorJoinBinding
import kr.tangram.smartgym.util.BackgroundRoundShape

class JuniorJoinActivity : BaseActivity<ActivityJuniorJoinBinding, JuniorViewModel>(R.layout.activity_junior_join) {
    override val viewModel: JuniorViewModel  by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJuniorJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ButterKnife.bind(this)


        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);
        binding.header.collapsingToolbar.title = "주니어 계정 만들기"

        binding.btnEmailLink.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))

        onClickMale()
    }


    @OnClick(R.id.vg_male)
    fun onClickMale()
    {
        binding.vgMale.isSelected = true
        binding.vgFemale.isSelected = false

        binding.viewMale.visibility = View.VISIBLE
        binding.viewFemal.visibility = View.GONE
    }


    @OnClick(R.id.vg_female)
    fun onClickFemale()
    {
        binding.vgMale.isSelected = false
        binding.vgFemale.isSelected = true

        binding.viewMale.visibility = View.GONE
        binding.viewFemal.visibility = View.VISIBLE
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