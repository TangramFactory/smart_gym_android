package kr.tangram.smartgym.data.repository

import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.local.AppDatabase
import kr.tangram.smartgym.data.remote.UserRestApi
import kr.tangram.smartgym.data.remote.model.*
import org.koin.core.component.KoinComponent

class UserRepository constructor(
    private val userRestApi: UserRestApi,
    private val db: AppDatabase,
) : KoinComponent {
    fun getUserExists(email: String): Observable<UserExistsResult> = userRestApi.getUserExists(email)
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
    ): Observable<BaseResult> =
        userRestApi.getUserReg(uid, email, name, gender, birthday, juniorYn, parentsUid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun updateUserLogin(jsonObject: JsonObject): Observable<UserLoginResult> =
        userRestApi.updateUserLogin(jsonObject).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun updateUserProfile(
        uuid: String,
        info: UserInfo,
    ): Observable<BaseResult> =
        userRestApi.updateUserProfile(uuid,
            info.userNickname,
            info.userIntroduce,
            info.userHwUnit,
            info.userHeight,
            info.userWeight.toString(),
            info.userDailyGoal.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun getJuniorList(uid: String): Observable<JuniorResult> =
        userRestApi.getJuniorList(uid).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun modifyJuniorProfile(
        uid: String,
        name: String,
        gender: Int,
        birthday: String,
    ): Observable<BaseResult> =
        userRestApi.modifyJuniorProfile(uid, name, gender, birthday).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}