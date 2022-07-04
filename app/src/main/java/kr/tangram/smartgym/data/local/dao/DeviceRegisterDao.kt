package kr.tangram.smartgym.data.local.dao

import android.net.MacAddress
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.data.domain.model.UserEmailCache
import retrofit2.http.DELETE

@Dao
interface DeviceRegisterDao {

//    @Query("SELECT * FROM device_register")
//    fun getDeviceList(): Flowable<List<DeviceRegister>>

    @Query("SELECT * FROM device_register")
    fun getDeviceList(): Observable<List<DeviceRegister>>

    @Query("SELECT * FROM device_register WHERE identifier = :identifier")
    fun getDeviceList(identifier: String): Single<List<DeviceRegister>>

    @Query("SELECT * FROM device_register WHERE identifier = :identifier")
    fun getDevice(identifier: String): Flowable<DeviceRegister>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevice(vararg device: DeviceRegister): Completable

    @Update
    fun updateDevice(vararg device: DeviceRegister) : Completable


    @Query("UPDATE device_register set connect = :connect, date_connect = :date_connect WHERE identifier = :identifier")
    fun updateDeviceConnect(connect:Boolean, date_connect: String, identifier: String) : Completable

    @Query("UPDATE device_register set battery = :battery WHERE identifier = :identifier")
    fun updateDeviceBattery(battery: Int, identifier: String) : Completable

    @Query("UPDATE device_register set option = :option WHERE identifier = :identifier")
    fun updateDeviceVersion(option: Int, identifier: String) : Completable

    @Query("UPDATE device_register set auto = :auto WHERE identifier = :identifier")
    fun updateDeviceAuto(auto: Boolean, identifier: String) : Completable

    @Query("DELETE from device_register WHERE identifier = :identifier")
    fun deleteDevice(identifier: String) : Completable

    @Query("DELETE FROM device_register")
    fun deleteAll(): Completable
}