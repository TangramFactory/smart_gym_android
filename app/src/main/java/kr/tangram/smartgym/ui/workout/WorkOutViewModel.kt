package kr.tangram.smartgym.ui.workout

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.*
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.base.NetworkResult
import kr.tangram.smartgym.ble.SmartRopeManager
import kr.tangram.smartgym.data.remote.model.JumpSaveObject
import kr.tangram.smartgym.data.remote.model.JumpToDay
import kr.tangram.smartgym.data.repository.WorkOutRepository
import javax.inject.Singleton

@Singleton
class WorkOutViewModel(
    private val workOutRepository: WorkOutRepository
) : BaseViewModel() {
    private val tag = javaClass.name

    private val _toDayJump = MutableLiveData<List<JumpToDay>>()
    val toDayJump: LiveData<List<JumpToDay>> get() = _toDayJump


    private val _jumpRopeWorkOut = MutableLiveData<JumpWorkOut>()
    val jumpRopeWorkOut: LiveData<JumpWorkOut> get() = _jumpRopeWorkOut

    private val _jumpMode = MutableLiveData<JumpMode>()
    val jumpMode: LiveData<JumpMode> get() = _jumpMode

    private val jumpSaveDataList = mutableListOf<JumpSaveObject.JumpSaveData>()

    private val auth = Firebase.auth

    init {
        SmartRopeManager.getInstance().apply {
            onCountJump = {
                Log.d("fragment", it.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    jumpCounting()
                }
            }
        }


        loadToDayJump()
        _jumpRopeWorkOut.value = JumpWorkOut()
        _jumpMode.value = JumpMode.JUMP
        jumpSaveDataList.clear()
    }

    fun jumpModeChange(v : View){
        when(jumpMode.value){
            JumpMode.JUMP -> _jumpMode.value = JumpMode.SPEED
            JumpMode.SPEED -> _jumpMode.value = JumpMode.CALORIE
            JumpMode.CALORIE -> _jumpMode.value = JumpMode.TIME
            JumpMode.TIME -> _jumpMode.value = JumpMode.JUMP
        }
    }

    private fun jumpCounting() {
        val jump = _jumpRopeWorkOut.value?.jump!! + 1
        val calorie = jump.toFloat()
        val speed = jump * 10
        val time = "2000"
        _jumpRopeWorkOut.postValue(JumpWorkOut(jump, calorie, speed, time))
    }

    fun saveJumpData() {
        jumpSaveDataList.add(JumpSaveObject.JumpSaveData((jumpSaveDataList.size+1).toString(), "0000", Hawk.get<String?>("deviceID").toString(),
            _jumpRopeWorkOut.value?.jump.toString(), "20", String.format("%.2f", _jumpRopeWorkOut.value?.calorie), _jumpRopeWorkOut.value?.time.toString()))

        addDisposable(workOutRepository.saveJumpWorkOut(JumpSaveObject(auth.currentUser?.uid.toString(), jumpSaveDataList)).subscribe({
               if(it.result.resultCode==0){
                    Log.d(tag ,"저장 성공")
               }else{
                   Log.d(tag ,"저장 실패")
               }
        },{
           _networkState.postValue(NetworkResult.FailToast("인터넷연결을 확인하여주세요"))
        }))
    }

    fun loadToDayJump(){
        addDisposable(workOutRepository.getTodayJump(auth.currentUser?.uid.toString()).subscribe({
            if(it.result.resultCode==0){
                Log.d(tag ,"로드 성공")
                _toDayJump.postValue(it.result.resultList)
            }else{
                Log.d(tag ,"로드 실패")
            }
        },{
            _networkState.postValue(NetworkResult.FailToast("인터넷연결을 확인하여주세요"))
        }))
    }




    data class JumpWorkOut(val jump: Int = 0, val calorie: Float = 0F, val speed: Int = 0, val time: String = "00:00:00")

    enum class JumpMode{
        JUMP, SPEED, CALORIE, TIME
    }
}
