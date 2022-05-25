package kr.tangram.smartgym.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng


class MapViewModel: ViewModel(){

    private val _gymDataList = MutableLiveData<String>()
    val gymDataList : LiveData<String> = _gymDataList

    private val _mapScale = MutableLiveData<MapScale>()
    val mapScale : LiveData<MapScale> = _mapScale

    private val _mapPosition = MutableLiveData<LatLng>()
    val mapPosition : LiveData<LatLng> = _mapPosition

    private val _mapState = MutableLiveData<LatLng>()
    val mapState : LiveData<LatLng> = _mapState

    fun onEvent(event: MapEvent){
        when(event){
            is MapEvent.MapPositionChange ->{
                _mapPosition.value = event.latLng
            }
            is MapEvent.MapScaleChange ->{
                _mapScale.value = event.scale
            }
            is MapEvent.RefreshData ->{
                //call data
            }
        }
    }
}



sealed class MapEvent{
    data class MapScaleChange(val scale : MapScale) : MapEvent()
    data class MapPositionChange(val latLng: LatLng) : MapEvent()
    object RefreshData : MapEvent()
}
//flow 사용시?
sealed class MapState{
    data class isSuccess(val data: String) : MapState()
    data class isFail(val data: String) : MapState()
    object isLoading : MapState()
}

enum class MapScale {
    LARGE, MEDIUM , SMALL
}
