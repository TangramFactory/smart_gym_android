package kr.tangram.smartgym.ui.workout

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.base.NetworkResult
import kr.tangram.smartgym.ble.SmartRopeManager
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.data.domain.model.JumpData
import kr.tangram.smartgym.data.remote.model.JumpToDay
import kr.tangram.smartgym.data.remote.request.JumpSaveObject
import kr.tangram.smartgym.data.repository.DeviceRegisterRepository
import kr.tangram.smartgym.data.repository.WorkOutRepository
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Singleton
class WorkOutViewModel(
    private val workOutRepository: WorkOutRepository
) : BaseViewModel() {
    private val tag = javaClass.name

    private val deviceRegisterRepository: DeviceRegisterRepository by inject()

    private val _toDayJump = MutableLiveData<List<JumpToDay>>()
    val toDayJump: LiveData<List<JumpToDay>> get() = _toDayJump


    private val _jumpRopeWorkOut1 = MutableLiveData<JumpWorkOut>()
    val jumpRopeWorkOut1: LiveData<JumpWorkOut> get() = _jumpRopeWorkOut1

    private val _jumpRopeWorkOut = MutableLiveData<JumpWorkOut>()
    val jumpRopeWorkOut: LiveData<JumpWorkOut> get() = _jumpRopeWorkOut

    private val _jumpMode = MutableLiveData<JumpMode>()
    val jumpMode: LiveData<JumpMode> get() = _jumpMode

    private val _deviceRegister = MutableLiveData<DeviceRegister>()
    var deviceRegister : LiveData<DeviceRegister> = _deviceRegister

    private val _localJumpData = MutableLiveData<List<JumpData>>()
    var localJumpData : LiveData<List<JumpData>> = _localJumpData

    private val jumpSaveDataList = mutableListOf<JumpSaveObject.JumpSaveData>()

    private val auth = Firebase.auth

    private var startTime : Long

    init {
        smartRopeManager = SmartRopeManager.getInstance().apply {
            onFound ={}
            onStopScan = {}
            onCountJump = { jumpCount: Int, timeGap: Long ->
                CoroutineScope(Dispatchers.Default).launch {
                    jumpCounting(timeGap)
                }
            }
        }
        startTime = getCurrentTime()
        clearJump()
        loadToDayJump()
        _jumpRopeWorkOut.value = JumpWorkOut()
        _jumpMode.value = JumpMode.JUMP
        jumpSaveDataList.clear()
    }

    fun getCurrentTime() = Date().time + TimeZone.getDefault().rawOffset

    fun jumpModeChange(v : View){
        when(jumpMode.value){
            JumpMode.JUMP -> _jumpMode.value = JumpMode.SPEED
            JumpMode.SPEED -> _jumpMode.value = JumpMode.CALORIE
            JumpMode.CALORIE -> _jumpMode.value = JumpMode.TIME
            JumpMode.TIME -> _jumpMode.value = JumpMode.JUMP
        }
    }

    private fun jumpCounting( timeGap : Long) {
        val jump =  _jumpRopeWorkOut.value?.jump!!+1
        val calorie = String.format("%.2f", _jumpRopeWorkOut.value?.calorie!! + calCalorie(90f, timeGap)).toFloat()
        val speed = (60000.0f / timeGap).toInt()
        val time = SimpleDateFormat("HH:mm:ss").format(getCurrentTime() - startTime)

        _jumpRopeWorkOut.postValue(JumpWorkOut(jump, calorie, speed, time))
    }

    fun calCalorie( weight_kg: Float, time_gap: Long): Float {
        var oneCalorie = 0.0f
        if (time_gap in 1..3000) {
            val minRPM = 60000.0f / time_gap

            val avrCalorie = when {
                minRPM < 70 -> {
                    0.074f
                }
                minRPM < 90 -> {
                    0.075f
                }
                minRPM < 110 -> {
                    0.077f
                }
                minRPM < 125 -> {
                    0.080f
                }
                else -> {
                    0.085f
                }
            }

            // LBS로 계산되기 때문에 KG기준 > LBS기준으로 바꿈
            oneCalorie = (weight_kg / 0.45359237f) * avrCalorie / 60.0f / 1000.0f * time_gap
        }
        return oneCalorie
    }

    fun saveJumpData() {
        jumpSaveDataList.add(JumpSaveObject.JumpSaveData((jumpSaveDataList.size+1).toString(),"", "", "", "0000", Hawk.get<String?>("deviceID").toString(),
            _jumpRopeWorkOut.value?.jump.toString(), "20", String.format("%.2f", _jumpRopeWorkOut.value?.calorie), _jumpRopeWorkOut.value?.time.toString()))

        addDisposable(workOutRepository.saveJumpWorkOut(JumpSaveObject(auth.currentUser?.uid.toString(), jumpSaveDataList)).subscribe({
               if(it.result?.resultCode==0){
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
            if(it.result?.resultCode==0){
                Log.d(tag ,"로드 성공")
                _toDayJump.postValue(it.resultList)
            }else{
                Log.d(tag ,"로드 실패")
            }
        },{
            _networkState.postValue(NetworkResult.FailToast("인터넷연결을 확인하여주세요"))
        }))
    }

    fun clearJump(){
        _jumpRopeWorkOut.postValue(JumpWorkOut(0, 0f, 0, "00:00:00"))
    }


    override fun onCleared() {
        super.onCleared()
        smartRopeManager.stopScan()
    }

    data class JumpWorkOut(val jump: Int = 0, val calorie: Float = 0F, val speed: Int = 0, val time: String = "00:00:00")

    enum class JumpMode{
        JUMP, SPEED, CALORIE, TIME
    }


    fun getDeviceRegister(identifier: String)
    {
        deviceRegisterRepository.getDeviceList(identifier).doOnSuccess {
            if(!it.isNullOrEmpty()){
                _deviceRegister.postValue(it[0])
            }
        }.subscribe()
    }

    fun deleteHistory()
    {
        smartRopeManager.deleteHistory()
    }

    fun saveLocalJumpData(list: List<JumpSaveObject.JumpSaveData>)
    {
        for(i in 0 until list.size)
        {
            var jumpData = JumpData(list[i].wid, list[i].did, list[i].mid, list[i].jump,
                list[i].avg, list[i].calorie, list[i].duration, list[i].finish)

            workOutRepository.insertJumpData(jumpData)
        }
    }


}
