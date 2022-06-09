package kr.tangram.smartgym.ui.login

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kr.tangram.smartgym.R
import kr.tangram.smartgym.databinding.ActivityLoginEmailSentBinding

class LoginEmailSentActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginEmailSentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginEmailSentBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

}