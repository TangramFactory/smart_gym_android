package kr.tangram.smartgym.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kr.tangram.smartgym.base.BaseViewModel

class MainViewModel : BaseViewModel() {

    private val _name = MutableLiveData<String>()
    val name : LiveData<String>
        get() = _name

    init {
        viewModelScope.launch {

//            contributorRepository.getContributors("","").subscribe { contributors ->
//
//                if (!contributors.isNullOrEmpty())
//                    _name.postValue(
//                        contributors.map { contributor -> contributor.toString() }
//                            .joinToString { name -> "$name\n" }
//                            .replace(",", "")
//                    )
//            }
        }
    }
}