package kr.tangram.smartgym.ui.intro

import kr.tangram.smartgym.base.BaseViewModel
import org.koin.core.component.inject
import DeviceRegisterRepository
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.domain.model.DeviceRegister

class IntroViewModel(
): BaseViewModel() {

    private val deviceRegisterRepository: DeviceRegisterRepository by inject()

    init {
    }


    fun offConnect()
    {
        addDisposable(
            deviceRegisterRepository.getDeviceList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<DeviceRegister>>() {
                    override fun onNext(response: List<DeviceRegister>) {

                        getCompositeDisposable().clear()

                        for(i in 0 until response.size)
                        {
                            deviceRegisterRepository.updateDeviceConnect(false, response[i].identifier!!)
                        }
                    }

                    override fun onError(e: Throwable) {
                    }
                    override fun onComplete() {
                    }
                })
        )
    }

}