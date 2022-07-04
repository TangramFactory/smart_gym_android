package kr.tangram.smartgym.ui.login

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.databinding.ActivityLoginInvalidBinding
import kr.tangram.smartgym.util.BackgroundRoundShape

class LoginInvalid : AppCompatActivity() {
    private lateinit var binding : ActivityLoginInvalidBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginInvalidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);
        binding.header.collapsingToolbar.title = getString(R.string.login_join)
        binding.btnConfirm.background =BackgroundRoundShape.fill(getString(R.string.buttonColor))
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