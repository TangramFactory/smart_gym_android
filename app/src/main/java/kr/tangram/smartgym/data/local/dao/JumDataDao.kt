package kr.tangram.smartgym.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.data.domain.model.JumpData

@Dao
interface JumDataDao {

    @Query("SELECT * FROM jump_data")
    fun getJumpDataList(): Observable<List<JumpData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJumpData(vararg device: JumpData): Completable

    @Query("DELETE from jump_data WHERE wid = :wid")
    fun deleteJUmpData(wid: String) : Completable
}