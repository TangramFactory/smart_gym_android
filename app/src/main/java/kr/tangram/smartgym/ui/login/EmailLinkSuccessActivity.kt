package kr.tangram.smartgym.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ui.main.MainActivity
import kr.tangram.smartgym.util.ReceiveEmail
import org.koin.androidx.viewmodel.ext.android.getViewModel

class EmailLinkSuccessActivity : AppCompatActivity() {
    val viewModel: LoginViewModel by lazy { getViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_link_success)
        val emailLink = intent.data.toString()
        viewModel.checkLogin(emailLink)

        viewModel.emailLinkSuccessData.observe(this){
            when(it){
                ReceiveEmail.LOGIN, ReceiveEmail.JOIN ->{
                    val intent = Intent(this, LoginSuccessActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    this.finish()
                }
                ReceiveEmail.JUNIOR ->{


                }
            }
        }
    }
}