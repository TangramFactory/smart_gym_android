package kr.tangram.smartgym.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kr.tangram.smartgym.R
import kr.tangram.smartgym.util.EmailReceiveType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EmailLinkSuccessActivity : AppCompatActivity(), KoinComponent {
    val viewModel : LoginViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_link_success)
        val emailLink = intent.data.toString()

        viewModel.checkLogin(emailLink)

        viewModel.emailReceiveTypeLinkSuccessData.observe(this){
            Log.d("loginSuccess", it.toString())
            when(it){
                EmailReceiveType.Login, EmailReceiveType.Join ,EmailReceiveType.Junior->{
                    val intent = Intent(this, LoginCompleteActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    this.finish()
                }

                is EmailReceiveType.Fail ->{
                    Log.d("loginFail", it.data)
                    viewModel.logout()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    Toast.makeText(this, it.data, Toast.LENGTH_LONG).show()
                    this.finish()
                }
            }
        }
    }
}