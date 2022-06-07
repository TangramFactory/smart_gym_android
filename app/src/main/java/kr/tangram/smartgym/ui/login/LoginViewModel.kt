package kr.tangram.smartgym.ui.login

import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.util.FireBaseEmailLogin
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.data.repository.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LoginViewModel : BaseViewModel(), KoinComponent{
    private val userRepository: UserRepository by inject()
    private val fireBaseEmailLogin: FireBaseEmailLogin by inject()
    private val auth : FirebaseAuth = Firebase.auth
    private val tag = "Login"


    fun checkLogin(emailLink : String, savedUserCallback : () -> Unit, notSavedUserCallback : () -> Unit){
        // 로그인되어있을때
        if(auth.currentUser !=null){
            savedUserCallback()
            return
        }

        if (auth.isSignInWithEmailLink(emailLink)) {
            var savedUserFlag = false
            //링크 타고 들어왔을때 로그인, 이메일은 로컬DB 사용해서 불러와야함...
            userRepository.getUserCachedEmail().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.email.isNullOrEmpty()){ return@subscribe }

                    if (isSavedUser(it.email!!)){ savedUserFlag = true }

                    auth.signInWithEmailLink(it.email!!, emailLink)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(tag, "Successfully signed in with email link!")
                                if (savedUserFlag){
                                    savedUserCallback()
                                }else{
                                    saveUser(task.result.user?.uid.toString(), it.email!!)
                                    notSavedUserCallback()
                                }
                            } else { Log.e(tag, "Error signing in with email link", task.exception) }
                        }
                }
        }
    }


    fun isSavedUser(string: String): Boolean = userRepository.getUserExists(string)?.result?.userCnt == 1

    private fun saveUser(uid : String, email : String) = userRepository.getUserReg(uid, email)


    fun sendEmail(email: String) {
        fireBaseEmailLogin.sendEmail(email)
    }

    fun saveEmail(email: String?) {
        userRepository.cacheUserEmail(email!!)
    }


}