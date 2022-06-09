package kr.tangram.smartgym.ui.certificate

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.databinding.ActivityCertification14ageBinding

class Certification14AgeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCertification14ageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertification14ageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = getString(R.string.login_email_login)
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