package kr.tangram.smartgym.ui.main.map.gym

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import kr.tangram.smartgym.ImageSize
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.data.domain.model.GymInfo
import java.io.ByteArrayOutputStream
import java.util.*


class GymViewModel: BaseViewModel(){

    private val _gymDataList = MutableLiveData<MutableList<GymInfo>>()
    val gymDataList : LiveData<MutableList<GymInfo>> = _gymDataList

    private val _privateGymFlag = MutableLiveData<Boolean>()
    val privateGymFlag : LiveData<Boolean> = _privateGymFlag

    private val _selectedActivity = MutableLiveData<String>()
    val selectedActivity : LiveData<String> = _selectedActivity

    private val _selectedType = MutableLiveData<String>()
    val selectedType : LiveData<String> = _selectedType

    private val _selectedLocation = MutableLiveData<LatLng>()
    val selectedLocation : LiveData<LatLng> = _selectedLocation

    private var uid : String

    init {
        uid = makeUID()
        _selectedLocation.value = LatLng(1.23, 32.2)
        _privateGymFlag.value=false
        _gymDataList.value = arrayListOf(GymInfo("상암고등학교", "마포구", 12300, 24f), GymInfo("상암고등학교", "마포구", 100, 24.123f))
    }

    private fun makeUID():String {
        val now = Calendar.getInstance(TimeZone.getTimeZone("GMT")).timeInMillis
        val str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var uid = ""
        for ( i in  0 until 6 ) {
            val r = ( str.length * Math.random() ).toInt()
            uid += str.substring(r,r+1)
        }
        return "${uid}_${now}"
    }



    fun gymPrivate(v : View){
        _privateGymFlag.value = true
    }
    fun setSelectedActivity(string: String){
        Log.d("클릭",string)
        _selectedActivity.value = string
    }
    fun setSelectedType(string: String){
        _selectedType.value = string
    }

    fun saveGymCreateData(gymName: String, gymIntro: String,  gymCreateInfo: String) {
        val data = GymCreateObject(uid ,gymName, gymIntro, selectedActivity.value.toString(), selectedType.value.toString(),
            selectedLocation.value.toString(), privateGymFlag.value.toString(), gymCreateInfo)
        Log.d("gymCreate", data.toString())
    }

    fun getProfileImagePath() = "https://storage.googleapis.com/smartgym-1204.appspot.com/smartgym/gym-image/"

    fun saveImage(resource: Bitmap, imageView: ImageView, imageSize: ImageSize, type: String) {
        val baos = ByteArrayOutputStream()
        resource.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()
        val mountiansURL = "/smartgym/gym-image/" + uid + "_${imageSize}_${type}"
        FirebaseStorage.getInstance().reference.child(mountiansURL).putBytes(data)
            .addOnSuccessListener {
                if(imageSize == ImageSize.Large)
                    imageView.setImageBitmap(resource)
            }
            .addOnFailureListener {
                Log.d("프로필이미지", "FAIL"+it.message)
            }
    }
}

data class GymCreateObject(
    val uid : String,
    val gymName: String,
    val gymIntro: String,
    val activity : String,
    val type : String,
    val location : String,
    val private : String,
    val gymCreateInfo: String
)



sealed class MapEvent{
    data class MapScaleChange(val scale : MapScale) : MapEvent()
    data class MapPositionChange(val latLng: LatLng) : MapEvent()
    object RefreshData : MapEvent()
}
//flow 사용시?
sealed class MapState{
    data class IsSuccess(val data: String) : MapState()
    data class IsFail(val data: String) : MapState()
    object IsLoading : MapState()
}

enum class MapScale {
    LARGE, MEDIUM , SMALL
}
