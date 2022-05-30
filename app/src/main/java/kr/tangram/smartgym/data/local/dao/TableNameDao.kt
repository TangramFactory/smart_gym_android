package kr.tangram.smartgym.data.local.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import kr.tangram.smartgym.data.domain.model.TableName

@Dao
interface TableNameDao {

    @Query("SELECT * FROM table_name")
    fun getList(): Flowable<List<TableName>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertField(vararg name: TableName): Completable

    @Update
    fun updateContributor(name: TableName): Completable

    @Query("DELETE FROM table_name")
    fun deleteAll(): Completable
}