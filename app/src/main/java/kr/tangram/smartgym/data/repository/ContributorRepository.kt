import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.remote.RestApi
import org.koin.core.component.KoinComponent

class ContributorRepository(
    private val restApi: RestApi
) : KoinComponent {

    fun getContributors(owner: String, repo: String): Single<List<String>> {

        return restApi.getObContributors(owner, repo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
            }
    }

}