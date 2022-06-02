package kr.tangram.smartgym.ui.certificate

import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseViewModel



class CertificationViewModel : BaseViewModel() {
    private val btnStates = mutableListOf(false, false, false, false)

    private val _btnStateList = MutableLiveData<List<Boolean>>()
    val btnStateList : LiveData<List<Boolean>> = _btnStateList

    private val _nextBtnState = MutableLiveData<Boolean>()
    val nextBtnState : LiveData<Boolean> = _nextBtnState

    private val _allClickFlag = MutableLiveData<Boolean>()
     var allClickFlag : LiveData<Boolean> = _allClickFlag

    init {
        _btnStateList.value = btnStates
        _nextBtnState.value = false
        _allClickFlag.value = false
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