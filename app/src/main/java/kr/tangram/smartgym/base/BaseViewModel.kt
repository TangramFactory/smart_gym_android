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

    private val _workOutState = MutableLiveData<Int>()
    val workOutState: LiveData<Int> get() = _workOutState

    var smartRopeManager : SmartRopeManager

    init {
        smartRopeManager  = SmartRopeManager.getInstance().apply {
            onFound ={ scanResult ->  }
            onStopScan = {}
            onCountJump={
                Log.d("연결" ,it.toString())
                _workOutState.postValue(it)  }
        }
    }

    fun startScanBase()
    {
        smartRopeManager?.startScanAndConnect()
    }

    fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)


    fun getCompositeDisposable() : CompositeDisposable{
        return compositeDisposable
    }


    fun clearRelease()
    {
        smartRopeManager?.clearRelease()
    }

    override fun onCleared() {
        clearRelease()
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun showMessage(message: String)
    {
        Toast.makeText(BaseApplication.context, message, Toast.LENGTH_LONG).show()
    }
}