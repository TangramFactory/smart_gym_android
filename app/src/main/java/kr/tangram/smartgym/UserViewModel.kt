package kr.tangram.smartgym

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import com.orhanobut.hawk.Hawk
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.base.NetworkResult
import kr.tangram.smartgym.data.remote.model.UserInfo
import kr.tangram.smartgym.data.repository.UserRepository
import org.koin.core.component.inject
import java.io.ByteArrayOutputStream
import javax.inject.Singleton




@Singleton
class UserViewModel: BaseViewModel() {
    private val userRepository: UserRepository by inject()
    private val auth = FirebaseAuth.getInstance()

    private val _name = MutableLiveData<String>()
    val name : LiveData<String> get() = _name

    private val _info = MutableLiveData<UserInfo>()
    val info : LiveData<UserInfo> get() = _info

    private val _heightFt = MutableLiveData<String>()
    val heightFt : LiveData<String> get() = _heightFt

    private val _heightIn = MutableLiveData<String>()
    val heightIn : LiveData<String> get() = _heightIn

    private val _heightCm = MutableLiveData<String>()
    val heightCm : LiveData<String> get() = _heightCm

    private val _settingInfo = MutableLiveData<String>()
    val settingInfo : LiveData<String> get() = _settingInfo

    private val _deviceInfoList = MutableLiveData<List<String>>()
    val deviceInfoList : LiveData<List<String>> get() = _deviceInfoList


    init {
        refreshData()
    }


    fun setProfile(info: UserInfo){
        addDisposable(userRepository.updateUserProfile(auth.currentUser?.uid.toString(), info).subscribe({
            this._info.value = info
            Hawk.put("userInfoData", info)
            Log.d("User", "프로필 저장 $info")
            _networkState.postValue(NetworkResult.SuccessToast("저장되었습니다"))
        },{
            _networkState.postValue(NetworkResult.FailToast("프로필 저장오류"))
        }))
    }

    fun getProfileImagePath() = "https://storage.googleapis.com/smartgym-1204.appspot.com/smartgym/profile-image/${auth.currentUser?.email}-Large"

    fun saveImage(resource: Bitmap, imageView: CircularImageView, imageSize: ImageSize) {
        val baos = ByteArrayOutputStream()
        resource.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()
        val mountiansURL = "/smartgym/profile-image/" + auth.currentUser?.email + "-$imageSize"
        FirebaseStorage.getInstance().reference.child(mountiansURL).putBytes(data)
            .addOnSuccessListener {
                if(imageSize == ImageSize.Large)
                    imageView.setImageBitmap(resource)
            }
            .addOnFailureListener {
                Log.d("프로필이미지", "FAIL"+it.message)
            }

    }



    fun refreshData(){
        val info = Hawk.get<UserInfo>("userInfoData")
        if (info!=null){
            Log.d("info",  info.toString())
            _info.value = info
            _heightFt.value = info.getHeightFt()
            _heightIn.value = info.getHeightIn()
            _heightCm.value = info.getHeightCm()
        }
    }
}

enum class ImageSize {
    Small, Large
}