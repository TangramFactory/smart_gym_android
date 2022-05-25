package kr.tangram.smartgym.ui.main

import ContributorRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kr.tangram.smartgym.base.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel(
//    private val contributorRepository: ContributorRepository
) : BaseViewModel() {

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