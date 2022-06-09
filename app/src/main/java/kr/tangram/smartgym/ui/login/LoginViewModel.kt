package kr.tangram.smartgym.ui.login

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.util.FireBaseEmailLogin
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.data.repository.UserRepository
import kr.tangram.smartgym.util.ReceiveEmail
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LoginViewModel : BaseViewModel(), KoinComponent {
    private val userRepository: UserRepository by inject()
    private val fireBaseEmailLogin: FireBaseEmailLogin by inject()
    private val auth: FirebaseAuth = Firebase.auth
    private val tag = "Login"


    private val _emailLinkSuccessData = MutableLiveData<ReceiveEmail>()
    val emailLinkSuccessData: LiveData<ReceiveEmail> = _emailLinkSuccessData

    fun checkLogin(emailLink: String) {
        // 로그인되어있을때
        Log.d("링크 파라미터", emailLink.toUri().getQueryParameter("receiveEmail").toString())

        if (auth.currentUser != null) {
            _emailLinkSuccessData.postValue(ReceiveEmail.LOGIN)
            return
        }

        if (auth.isSignInWithEmailLink(emailLink)) {
            //링크 타고 들어왔을때 로그인, 이메일은 로컬DB 사용해서 불러와야함...

            userRepository.getUserCachedEmail().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.email.isNullOrEmpty()) {
                        return@subscribe
                    }
                    emailLinkLogin(it.email!!, emailLink)
                }
        }
    }

    private fun emailLinkLogin(email: String, emailLink: String) {
        auth.signOut()

        auth.signInWithEmailLink(email, emailLink)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val receiveEmailParam =
                        emailLink.toUri().getQueryParameter("receiveEmail").toString()
                    when (receiveEmailParam) {
                        "LOGIN" -> _emailLinkSuccessData.postValue(ReceiveEmail.LOGIN)

                        "JOIN" -> {
                            saveUser(task.result.user?.uid.toString(), email)
                            _emailLinkSuccessData.postValue(ReceiveEmail.JOIN)
                        }

                        "JUNIOR" -> {
                            _emailLinkSuccessData.postValue(ReceiveEmail.JUNIOR)
                        }
                    }
                } else {
                    Log.e(tag, "Error signing in with email link", task.exception)
                }
            }
    }


    fun isSavedUser(string: String): Boolean =
        userRepository.getUserExists(string)?.result?.userCnt == 1

    private fun saveUser(uid: String, email: String) = userRepository.getUserReg(uid, email)


    fun sendEmail(email: String, receiveEmail: ReceiveEmail) {
        fireBaseEmailLogin.sendEmail(email, receiveEmail)
    }

    fun saveEmail(email: String?) {
        userRepository.cacheUserEmail(email!!)
    }
}