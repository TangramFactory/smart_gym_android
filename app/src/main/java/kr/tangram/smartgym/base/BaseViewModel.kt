package kr.tangram.smartgym.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hwangjr.rxbus.RxBus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.tangram.smartgym.ble.SmartRopeManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.inject.Singleton

@Singleton
open class BaseViewModel : ViewModel(), KoinComponent {

    private val compositeDisposable = CompositeDisposable()

    internal val _networkState = MutableLiveData<NetworkResult>()
    val networkState: LiveData<NetworkResult> get() = _networkState

    //모든곳에서 같은 객체를 바라보게 하기위해 의존성 주입
    private val _workOutState : MutableLiveData<Int> by inject()
    val workOutState: LiveData<Int> get() = _workOutState

    private val _historySync = MutableLiveData<String>()
    var historySync : LiveData<String> = _historySync


    lateinit var smartRopeManager : SmartRopeManager

    init {
        _workOutState.postValue(-1)
        smartRopeManagerInit()
    }


    fun smartRopeManagerInit(){
        _workOutState.postValue(-1)
        smartRopeManager  = SmartRopeManager.getInstance().apply {
            onFound ={ }
            onStopScan = {}
            onHistory = {
                _historySync.postValue(it)
            }
            onCountJump={ jumpCount: Int, _: Long ->
                _workOutState.postValue(jumpCount)
            }
        }
    }


    fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)


    fun getCompositeDisposable() : CompositeDisposable{
        return compositeDisposable
    }


    fun showMessage(message: String)
    {
        Toast.makeText(BaseApplication.context, message, Toast.LENGTH_LONG).show()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}