package kr.tangram.smartgym.ui.login

import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.tangram.smartgym.R
import kr.tangram.smartgym.databinding.ActivityLoginEmailSentBinding

class LoginEmailSentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailSentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginEmailSentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);
        binding.header.collapsingToolbar.title = getString(R.string.login_email_login)

        binding.btnRecertification.paintFlags = binding.btnRecertification.paintFlags or  Paint.UNDERLINE_TEXT_FLAG
        binding.btnRecertification.setOnClickListener {
            //팝업 버튼

            finish()
        }
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