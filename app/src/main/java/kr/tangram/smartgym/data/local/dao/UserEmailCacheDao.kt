package kr.tangram.smartgym.data.local.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import kr.tangram.smartgym.data.domain.model.UserEmailCache

@Dao
interface UserEmailCacheDao {

    @Query("SELECT * FROM user_email_cache")
    fun getEmail(): Flowable<UserEmailCache>

    @Update
    fun updateEmail(email: UserEmailCache): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEmail(email: UserEmailCache): Completable

    @Query("DELETE FROM user_email_cache")
    fun deleteAllEmail() : Completable


}