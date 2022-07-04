package kr.tangram.smartgym.ui.main

import DeviceRegisterRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kr.tangram.smartgym.base.BaseViewModel
import org.koin.core.component.inject


class MainViewModel: BaseViewModel() {
    private val contributorRepository: DeviceRegisterRepository by inject()
    private val _name = MutableLiveData<String>()
    val name : LiveData<String>
        get() = _name

    init {
//        viewModelScope.launch {
//
//            contributorRepository.getContributors("","").subscribe { contributors ->
//
//                if (!contributors.isNullOrEmpty())
//                    _name.postValue(
//                        contributors.map { contributor -> contributor.toString() }
//                            .joinToString { name -> "$name\n" }
//                            .replace(",", "")
//                    )
//            }
//        }
    }
}