package kr.tangram.smartgym.ui.intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityDeviceManagerBinding
import kr.tangram.smartgym.ui.device.DeviceManagerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntroActivity : BaseActivity<ActivityDeviceManagerBinding, IntroViewModel>(R.layout.activity_sample) {
    override val viewModel: IntroViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

//        Thread.sleep(5000)
//        startActivity(Intent(this, WelComeActivity::class.java))
//        this.finish()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = "회원 가입"


        Handler().postDelayed({

            startActivity(Intent(this, DeviceManagerActivity::class.java))
            finish()

        }, 100)


        viewModel.offConnect()
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