package kr.tangram.smartgym.data.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.data.domain.model.JumpData
import kr.tangram.smartgym.data.local.AppDatabase
import kr.tangram.smartgym.data.remote.WorkOutRestApi
import kr.tangram.smartgym.data.remote.response.JumpLoadDayResponse
import kr.tangram.smartgym.data.remote.request.JumpSaveObject
import kr.tangram.smartgym.data.remote.response.BaseResponse
import org.koin.core.component.KoinComponent
import javax.inject.Singleton

@Singleton
class WorkOutRepository(
    private val workOutRestApi: WorkOutRestApi,
    private val db: AppDatabase,
):KoinComponent {
    fun saveJumpWorkOut(jumpSaveObject: JumpSaveObject):Observable<BaseResponse> = workOutRestApi.jumpSave(jumpSaveObject)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun getTodayJump(uid:String): Observable<JumpLoadDayResponse> = workOutRestApi.jumpLoadDay(uid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun getJumpDataList(): Observable<List<JumpData>> = db.jumpDataDAO().getJumpDataList()

    fun insertJumpData(jumpData: JumpData)
    {
        db.jumpDataDAO().insertJumpData(jumpData)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteJumpData(wid: String)
    {
        db.jumpDataDAO().deleteJUmpData(wid)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun saveJumpWorkOutHistory(jumpSaveObject: JumpSaveObject):Observable<BaseResponse> = workOutRestApi.jumpSave(jumpSaveObject)
}
