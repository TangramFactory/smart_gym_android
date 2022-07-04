package kr.tangram.smartgym.data.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.local.AppDatabase
import kr.tangram.smartgym.data.remote.UserRestApi
import kr.tangram.smartgym.data.remote.WorkOutRestApi
import kr.tangram.smartgym.data.remote.model.BaseResult
import kr.tangram.smartgym.data.remote.model.JumpLoadDayResult
import kr.tangram.smartgym.data.remote.model.JumpSaveObject
import kr.tangram.smartgym.ui.workout.WorkOutViewModel
import org.koin.core.component.KoinComponent
import javax.inject.Singleton

@Singleton
class WorkOutRepository(
    private val workOutRestApi: WorkOutRestApi,
    private val db: AppDatabase,
):KoinComponent {
    fun saveJumpWorkOut(jumpSaveObject: JumpSaveObject):Observable<BaseResult> = workOutRestApi.jumpSave(jumpSaveObject)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun getTodayJump(uid:String): Observable<JumpLoadDayResult> = workOutRestApi.jumpLoadDay(uid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

}