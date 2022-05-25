package kr.tangram.smartgym.ui.login


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ui.main.MainActivity


class LoginActivity: AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnFbEmailLink = findViewById<Button>(R.id.btn_fb_email_link)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        val emailLink = intent.data.toString()

        viewModel.checkLogin(emailLink){ startActivity(Intent(this, MainActivity::class.java)) }

        btnFbEmailLink.setOnClickListener{
            if (edtEmail.text.isNullOrEmpty()){
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()){
                //이메일 패턴 아님!
                return@setOnClickListener
            }

            if (!viewModel.isCertifiedUser(edtEmail.text.toString())){
               //인증페이지로 넘어가야함
                return@setOnClickListener
            }

            viewModel.sendEmail(edtEmail.text.toString())
            btnFbEmailLink.isEnabled = false
        }

    }


}