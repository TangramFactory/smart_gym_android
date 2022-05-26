package kr.tangram.smartgym.ui.login

import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.tangram.smartgym.util.FireBaseEmailLogin
import kr.tangram.smartgym.base.BaseViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LoginViewModel : BaseViewModel(), KoinComponent{

    private val fireBaseEmailLogin: FireBaseEmailLogin by inject()
    private val auth : FirebaseAuth = Firebase.auth
    private val tag = "Login"
    private var email = "printf2475@naver.com"

    init {
        getLastLoginEmail()
    }

    fun getLastLoginEmail(){

    }


    fun checkLogin(emailLink : String, onSuccess : () -> Unit){

        if(auth.currentUser !=null){
            // 로그인되어있을때
            onSuccess()
        }else if (auth.isSignInWithEmailLink(emailLink)) {
            //링크 타고 들어왔을때 로그인, 이메일은 로컬DB 사용해서 불러와야함...
            auth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "Successfully signed in with email link!")

                        onSuccess()
                    } else { Log.e(tag, "Error signing in with email link", task.exception) }
                }
        }
    }

    fun sendEmail(email: String) {
        fireBaseEmailLogin.sendEmail(email)
    }

    fun isCertifiedUser(toString: String): Boolean {
        //인증한 사람인가 확인
        return true
    }
}