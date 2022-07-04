package kr.tangram.smartgym.ui.login.join

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.databinding.ActivityJoinGuide14underBinding

class JoinGuide14UnderActivity : AppCompatActivity() {

    private lateinit var binding : ActivityJoinGuide14underBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinGuide14underBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);
        binding.header.collapsingToolbar.title = getString(R.string.login_over_14_years_old)
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