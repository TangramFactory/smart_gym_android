package kr.tangram.smartgym.ui.login.join

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.base.NetworkResult
import kr.tangram.smartgym.data.repository.UserRepository
import kr.tangram.smartgym.util.EmailReceiveType
import kr.tangram.smartgym.util.FireBaseEmailLogin
import org.koin.core.component.inject


class JoinViewModel : BaseViewModel() {

    private val userRepository: UserRepository by inject()
    private val fireBaseEmailLogin: FireBaseEmailLogin by inject()
    private val auth: FirebaseAuth = Firebase.auth

    private val btnStates = mutableListOf(false, false, false, false)

    private val _btnStateList = MutableLiveData<List<Boolean>>()
    val btnStateList : LiveData<List<Boolean>> = _btnStateList

    private val _nextBtnState = MutableLiveData<Boolean>()
    val nextBtnState : LiveData<Boolean> = _nextBtnState

    private val _allClickFlag = MutableLiveData<Boolean>()
     var allClickFlag : LiveData<Boolean> = _allClickFlag

    private val _isSaveUserFlag = MutableLiveData<Boolean>()
    var isSaveUserFlag: LiveData<Boolean> = _isSaveUserFlag


    init {
        _btnStateList.value = btnStates
        _nextBtnState.value = false
        _allClickFlag.value = false
    }

    fun savedUserCheck(string: String): Boolean = addDisposable(
        userRepository.getUserExists(string).subscribe({
            _isSaveUserFlag.value = it.resultList == 1
        }, {
            _networkState.postValue(NetworkResult.FailToast("인터넷 연결을 확인하여주세요"))
        })
    )

    fun sendEmail(email: String) {
        fireBaseEmailLogin.sendEmail(email, EmailReceiveType.Join)
        auth.signOut()
    }

    fun saveEmail(email: String?) {
        Hawk.put("email", email)
    }

    fun onClickBtn(v : View){
        var position = getViewPosition(v)

        btnStates[position] = !btnStates[position]
        _btnStateList.value = btnStates

        nextBtnEnableCheck()
    }

    private fun getViewPosition(v: View): Int  = when(v.id){
            R.id.imgCheckAge -> { 0 }
            R.id.imgPersonalData -> { 1 }
            R.id.imgTerms -> { 2 }
            R.id.imgLocationInfo -> { 3 }
            else -> { 0 }
        }



    private fun nextBtnEnableCheck() {
        for (flag in btnStates) {
            if (!flag) {
                _nextBtnState.value = false
                _allClickFlag.value = false
                return
            }
        }
        _nextBtnState.value = true
    }

    fun onClickAllBtn(v : View){
        if (!_allClickFlag.value!!) {
            _allClickFlag.value = true
            _nextBtnState.value = true

            for (i in btnStates.indices){
                btnStates[i]=true
            }
        } else {
           _allClickFlag.value = false
            _nextBtnState.value = false
            for (i in btnStates.indices){
                btnStates[i]=false
            }

        }
        _btnStateList.value = btnStates
    }

}