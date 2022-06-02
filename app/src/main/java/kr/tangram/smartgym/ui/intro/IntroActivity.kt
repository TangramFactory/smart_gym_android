package kr.tangram.smartgym.ui.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ui.login.LoginActivity

class IntroActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        Thread.sleep(5000)
        startActivity(Intent(this, LoginActivity::class.java))
        this.finish()

    }

}