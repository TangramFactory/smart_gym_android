import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.local.dao.TableNameDao
import kr.tangram.smartgym.data.remote.RestApi
import org.koin.core.component.KoinComponent

class TableNameRepository(
    private val restApi: RestApi
    , private val tableNameDao: TableNameDao
) : KoinComponent {

    fun getTableNames(id: String): Single<List<String>> {

        return restApi.getObContributors("", "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
            }

//            contributorDao.deleteAll()
//        .subscribeOn(Schedulers.io())
//        .subscribe()
//
//        contributorDao
//            .insertContributors(*it.toTypedArray())
//            .subscribeOn(Schedulers.io())
//            .subscribe()
    }

}