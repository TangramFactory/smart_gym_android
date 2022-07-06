package kr.tangram.smartgym.ui.login.junior

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.data.remote.model.UserInfo
import kr.tangram.smartgym.databinding.ActivityJuniorBinding

class JuniorActivity : BaseActivity<ActivityJuniorBinding, JuniorViewModel>(R.layout.activity_junior) {
    override val viewModel: JuniorViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJuniorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);
        binding.header.collapsingToolbar.title = "주니어 계정"

        binding.layoutPlus.setOnClickListener { startActivity(Intent(this, JuniorJoinActivity::class.java)) }

        viewModel.getJuniorList()
        viewModel.juniorList.observe(this){
            binding.layoutJuniorList.removeAllViews()
            it.forEach { junior->
                binding.layoutJuniorList.addView(addRow(junior))
            }
        }
    }

    private fun addRow(juniorInfo : UserInfo): View
    {
        val view: View = ConstraintLayout.inflate(this, R.layout.row_account, null)
        val tvJuniorItemEmail = view.findViewById<TextView>(R.id.tvJuniorItemEmail)
        val ivJuniorProfileImage = view.findViewById<CircularImageView>(R.id.ivJuniorProfileImage)

        tvJuniorItemEmail.text = juniorInfo.userEmail
        Glide.with(this)
            .load(getString(R.string.base_url_image_path)+"${juniorInfo.userEmail}-Small")
            .error(R.drawable.ic_default_profile)
            .into(ivJuniorProfileImage)
        view.setOnClickListener {
           val intent =  Intent(this, JuniorModifyActivity::class.java)
            intent.putExtra("info", juniorInfo)
            startActivity(intent)
        }

        return view
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

    override fun onRestart() {
        super.onRestart()
        viewModel.getJuniorList()
    }
}