package kr.tangram.smartgym.ui.login

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.data.repository.UserRepository
import kr.tangram.smartgym.util.FireBaseEmailLogin
import kr.tangram.smartgym.util.EmailReceiveType
import com.google.gson.JsonObject;
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.NetworkResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException
import javax.inject.Singleton


@Singleton
class LoginViewModel : BaseViewModel(), KoinComponent {
    private val context : Context by inject()
    private val userRepository: UserRepository by inject()

    private val fireBaseEmailLogin: FireBaseEmailLogin by inject()

    private val auth: FirebaseAuth = Firebase.auth

    private val tag = "User"

    private val _emailLinkSuccessData = MutableLiveData<EmailReceiveType>()
    val emailReceiveTypeLinkSuccessData: LiveData<EmailReceiveType> = _emailLinkSuccessData

    private val _isSaveUserFlag = MutableLiveData<Boolean>()
    var isSaveUserFlag: LiveData<Boolean> = _isSaveUserFlag


    init {
//        getAid{  Aid ->
//            if(!Aid.isNullOrEmpty()){
//                Hawk.put("deviceID", Aid)
//            }else{
//                Hawk.put("deviceID", "1")
//            }
//        }
    }



    //deepLink로 앱 들어왔을떄
    fun checkLogin(emailLink: String) {
        juniorAccountCheck(emailLink)

        loginCheck().let { if (it) return }

        emailLinkCheck(emailLink)
    }

    private fun juniorAccountCheck(emailLink: String){
        //주니어가입시 부모계정 로그아웃
        if (emailLink.toUri().getQueryParameter("receiveEmail").toString() == EmailReceiveType.Junior.toString()) {
            auth.signOut()
        }
    }

    private fun loginCheck():Boolean {
        // 로그인되어있을때
        if (auth.currentUser != null) {
            _emailLinkSuccessData.postValue(EmailReceiveType.Login)
            return true
        }
        return false
    }

    private fun emailLinkCheck(emailLink: String) {
        if (!auth.isSignInWithEmailLink(emailLink)) {
            _emailLinkSuccessData.postValue(EmailReceiveType.Fail("잘못된 링크입니다 이메일을 다시 확인하여주세요"))
            return
        }

        val email: String? =  emailLink.toUri().getQueryParameter("email").toString()
        if (email.isNullOrEmpty()) {
            return
        }

        emailLinkLogin(email, emailLink)
    }





    private fun emailLinkLogin(email: String, emailLink: String, name: String = "이재원", birthday: String = "19921223", gender: Int = 0, ) {
        val receiveEmailParam = emailLink.toUri().getQueryParameter("receiveEmail").toString()

        auth.signInWithEmailLink(email, emailLink)
            .addOnFailureListener { _emailLinkSuccessData.postValue(EmailReceiveType.Fail("Login Fail")) }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val jsonProfile = makeJsonProfile(task.result.user?.uid, email)

                    when (receiveEmailParam) {
                        "Login" -> login(jsonProfile)
                        "Join" -> join(task.result.user?.uid.toString(), jsonProfile, email, name, birthday, gender)
                        "Junior" -> juniorJoin(task.result.user?.uid.toString(), jsonProfile, emailLink)

                        else -> {
                            auth.signOut()
                            _emailLinkSuccessData.postValue(EmailReceiveType.Fail("잘못된 링크입니다 이메일을 다시 확인하여주세요 param error"))
                        }
                    }
                } else {
                    _emailLinkSuccessData.postValue(EmailReceiveType.Fail("로그인 오류"))
                }
            }
    }

    private fun makeJsonProfile(uid: String?, email: String): JsonObject {
        val jsonProfile = JsonObject()
        val jsonAuth = JsonObject()
        val jsonFirebase = JsonObject()
        jsonFirebase.addProperty("uid", uid)
        jsonFirebase.addProperty("email", email)
        jsonAuth.add("firebase", jsonFirebase)
        jsonProfile.add("auth", jsonAuth)
        return jsonProfile
    }

    private fun login(jsonProfile: JsonObject) {
        updateUser(jsonProfile, EmailReceiveType.Login)
    }

    private fun join(uid: String, jsonProfile: JsonObject, email: String, name: String, birthday: String, gender: Int, ) {
        createUser(uid, email, name, gender, birthday) {
            updateUser(jsonProfile, EmailReceiveType.Join)
        }
    }

    //딥링크에있는 파라미터로 주니어 계정 생성
    private fun juniorJoin(uid: String, jsonProfile: JsonObject, emailLink: String) {
        val juniorName = emailLink.toUri().getQueryParameter("name").toString()
        val juniorBirthDay = emailLink.toUri().getQueryParameter("birthDay").toString()
        val juniorEmail = emailLink.toUri().getQueryParameter("email").toString()
        val juniorGender = emailLink.toUri().getQueryParameter("gender").toString()
        val juniorParentUid = emailLink.toUri().getQueryParameter("parentUid").toString()

        createUser(uid, juniorEmail, juniorName, juniorGender.toInt(), juniorBirthDay, "Y", juniorParentUid) {
            updateUser(jsonProfile, EmailReceiveType.Junior)
        }
    }


    private fun createUser(uid: String, email: String, name: String, gender: Int, birthday: String, juniorYn: String = "N", parentsUid: String = "", onSuccess: (() -> Unit)? = null) {
        addDisposable(userRepository.getUserReg(uid, email, name, gender, birthday, juniorYn, parentsUid).subscribe(
            {
                if (it.result.resultCode == 0 && onSuccess != null) {
                    Log.d(tag, "유저 생성")
                    onSuccess()
                } else if (it.result.resultCode != 0 || it == null) {
                    _emailLinkSuccessData.postValue(EmailReceiveType.Fail("유저 생성 오류"))

                    auth.currentUser?.delete()
                    auth.signOut()
                }

            }, {
                _emailLinkSuccessData.postValue(EmailReceiveType.Fail("유저 생성 오류"))

                auth.currentUser?.delete()
                auth.signOut()
            }
        ))
    }

    private fun updateUser(jsonProfile: JsonObject, emailReceiveType: EmailReceiveType) {
        addDisposable(userRepository.updateUserLogin(jsonProfile).subscribe({
            if (it.result.resultCode==0){
                Hawk.put("userInfoData", it.result.userInfoList[0])
                _emailLinkSuccessData.postValue(emailReceiveType)
            }else{
                _emailLinkSuccessData.postValue(EmailReceiveType.Fail("로그인오류"))
                _networkState.postValue(NetworkResult.FailToast("유저 업데이트 오류"))
            }
        }, {
            _networkState.postValue(NetworkResult.FailToast("인터넷 연결을 확인하여주세요"))
        }))
    }




    fun savedUserCheck(string: String): Boolean = addDisposable(
        userRepository.getUserExists(string).subscribe({
            _isSaveUserFlag.value = it.result.userCnt == 1
        }, {
            _networkState.postValue(NetworkResult.FailToast("인터넷 연결을 확인하여주세요"))
        })
    )


    fun sendEmail(email: String, emailReceiveType: EmailReceiveType, name: String = "", birthDay: String = "", gender: String = "0", ) {
        fireBaseEmailLogin.sendEmail(email, emailReceiveType, name, birthDay, gender, auth.currentUser?.uid)
        auth.signOut()
    }


    fun saveEmail(email: String?) {
        Hawk.put("email", email)
    }

    //자동 로그인
    fun checkLogin(isLogin: () -> Unit) {
        if (auth.currentUser != null) {
            Log.d("로그인", auth.currentUser?.email.toString())
            isLogin()
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getAid(onComplete: (aid:String?) -> Unit) {
        val thread = object : Thread() {
            override fun run() {
                try {
                    val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                    val advertisingId = adInfo.id
                    onComplete(advertisingId)
                } catch (exception: IOException) {
                    exception.printStackTrace()
                    onComplete(null)
                } catch (exception: GooglePlayServicesRepairableException) {
                    exception.printStackTrace()
                    onComplete(null)
                } catch (exception: GooglePlayServicesNotAvailableException) {
                    exception.printStackTrace()
                    onComplete(null)
                }
            }
        }
        // call thread start for background process
        thread.start()
    }

}

