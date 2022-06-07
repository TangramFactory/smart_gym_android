package kr.tangram.smartgym.data.repository

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.domain.model.UserEmailCache
import kr.tangram.smartgym.data.local.AppDatabase
import kr.tangram.smartgym.data.remote.model.UserExistsResult
import kr.tangram.smartgym.data.remote.model.UserRegResult
import kr.tangram.smartgym.data.remote.RestApi
import org.koin.core.component.KoinComponent

class UserRepository constructor(
    private val restApi: RestApi,
    private val db: AppDatabase
) : KoinComponent {
    fun getUserExists(email: String): UserExistsResult? = restApi.getUserExists(email)
        .subscribeOn(Schedulers.io())
        .blockingFirst()

    fun getUserReg(uid :String, email : String) : UserRegResult? = restApi.getUserReg(uid, email)
        .subscribeOn(Schedulers.io())
        .blockingFirst()

    fun getUserCachedEmail() : Flowable<UserEmailCache> = db.userEmailCacheDAO().getEmail()

    fun cacheUserEmail(email: String) {
        db.userEmailCacheDAO()
            .deleteAllEmail()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                db.userEmailCacheDAO().saveEmail(UserEmailCache(email))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        Log.d("room", email.toString())
                    }
            }
    }





}