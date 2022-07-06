package kr.tangram.smartgym.data.repository

import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.local.AppDatabase
import kr.tangram.smartgym.data.remote.UserRestApi
import kr.tangram.smartgym.data.remote.model.UserInfo
import kr.tangram.smartgym.data.remote.response.*
import org.koin.core.component.KoinComponent

class UserRepository constructor(
    private val userRestApi: UserRestApi,
    private val db: AppDatabase,
) : KoinComponent {
    fun getUserExists(email: String): Observable<UserExistsResponse> = userRestApi.getUserExists(email)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())


    fun getUserReg(
        uid: String,
        email: String,
        name: String,
        gender: Int,
        birthday: String,
        juniorYn: String,
        parentsUid: String,
    ): Observable<BaseResponse> =
        userRestApi.getUserReg(uid, email, name, gender, birthday, juniorYn, parentsUid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun updateUserLogin(jsonObject: JsonObject): Observable<UserLoginResponse> =
        userRestApi.updateUserLogin(jsonObject).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun updateUserProfile(
        uuid: String,
        info: UserInfo,
    ): Observable<BaseResponse> =
        userRestApi.updateUserProfile(uuid,
            info.userNickname,
            info.userIntroduce,
            info.userHwUnit,
            info.userHeight,
            info.userWeight.toString(),
            info.userDailyGoal.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun getJuniorList(uid: String): Observable<JuniorResponse> =
        userRestApi.getJuniorList(uid).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun modifyJuniorProfile(
        uid: String,
        name: String,
        gender: Int,
        birthday: String,
    ): Observable<BaseResponse> =
        userRestApi.modifyJuniorProfile(uid, name, gender, birthday).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}