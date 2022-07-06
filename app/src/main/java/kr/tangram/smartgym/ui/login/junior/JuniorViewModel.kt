package kr.tangram.smartgym.ui.login.junior

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.data.repository.UserRepository
import kr.tangram.smartgym.util.FireBaseEmailLogin
import kr.tangram.smartgym.util.EmailReceiveType
import kr.tangram.smartgym.base.NetworkResult
import kr.tangram.smartgym.data.remote.model.UserInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.inject.Singleton


@Singleton
class JuniorViewModel : BaseViewModel(), KoinComponent {
    private val userRepository: UserRepository by inject()

    private val fireBaseEmailLogin: FireBaseEmailLogin by inject()

    private val auth: FirebaseAuth = Firebase.auth

    private val _juniorList = MutableLiveData<List<UserInfo>>()
    val juniorList: LiveData<List<UserInfo>> get() = _juniorList

    private val _isSaveUserFlag = MutableLiveData<Boolean>()
    var isSaveUserFlag: LiveData<Boolean> = _isSaveUserFlag


    fun modifyJunior(uid: String, name: String, gender: Int, birthday: String) {
        addDisposable(userRepository.modifyJuniorProfile(uid, name, gender, birthday).subscribe({
            if (it.result?.resultCode == 0) Log.d("junior", "주니어 정보 수정 ${it.result?.resultMsg}")
            else _networkState.postValue(NetworkResult.FailToast("주니어 업데이트 오류"))

        }, {
            _networkState.postValue(NetworkResult.FailToast("인터넷 연결을 확인하여주세요"))
        }))
    }


    fun savedUserCheck(string: String): Boolean = addDisposable(
        userRepository.getUserExists(string).subscribe({
            _isSaveUserFlag.value = it.resultList == 1
        }, {
            _networkState.postValue(NetworkResult.FailToast("인터넷 연결을 확인하여주세요"))
        })
    )


    fun sendEmail(email: String, emailReceiveType: EmailReceiveType, name: String = "", birthDay: String = "", gender: String = "0", ) {
        fireBaseEmailLogin.sendEmail(email, emailReceiveType, name, birthDay, gender, auth.currentUser?.uid)
        auth.signOut()
    }

    fun getJuniorList() = addDisposable(userRepository.getJuniorList(auth.currentUser?.uid!!).subscribe({
            if (it.result?.resultCode == 0) {
                Log.d("junior", "주니어 리스트 ${it.resultList}")
                _juniorList.postValue(it.resultList)
            } else {
                _networkState.postValue(NetworkResult.FailToast("주니어 목록 가져오기 실패"))
            }
        }, {
            _networkState.postValue(NetworkResult.FailToast("인터넷 연결을 확인하여주세요"))
        }))


    fun saveEmail(email: String?) {
        Hawk.put("email", email)
    }
}

