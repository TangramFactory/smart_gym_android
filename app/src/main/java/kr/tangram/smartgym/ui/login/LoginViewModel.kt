package kr.tangram.smartgym.ui.login

import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kr.tangram.smartgym.util.FireBaseEmailLogin
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.data.remote.RestApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LoginViewModel constructor(
    private val restApi: RestApi
) : BaseViewModel(), KoinComponent{

    private val fireBaseEmailLogin: FireBaseEmailLogin by inject()
    private val auth : FirebaseAuth = Firebase.auth
    private val fireStore = Firebase.firestore
    private val tag = "Login"
    private var email = "printf2475@naver.com"

    init {
        getLastLoginUserInfo()
    }

    fun getLastLoginUserInfo(){

    }


    fun checkLogin(emailLink : String, savedUserCallback : () -> Unit, notSavedUserCallback : () -> Unit){

        if(auth.currentUser !=null){
            // 로그인되어있을때
            savedUserCallback()
        }else if (auth.isSignInWithEmailLink(emailLink)) {
            var savedUserFlag = false
            //링크 타고 들어왔을때 로그인, 이메일은 로컬DB 사용해서 불러와야함...
                if (isSavedUser(email)){ savedUserFlag = true }

            auth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "Successfully signed in with email link!")

                        if (!savedUserFlag){
                            savedUserCallback()
                        }else{
                            notSavedUserCallback()
                        }

                    } else { Log.e(tag, "Error signing in with email link", task.exception) }
                }

        }
    }

    fun isSavedUser(string: String): Boolean {
//        restApi.getUserExists()

        return true
    }

    fun sendEmail(email: String) {
        fireBaseEmailLogin.sendEmail(email)
    }


}