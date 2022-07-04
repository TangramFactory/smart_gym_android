import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.remote.UserRestApi
import org.koin.core.component.KoinComponent

class ContributorRepository(
    private val userRestApi: UserRestApi
) : KoinComponent {

    fun getContributors(owner: String, repo: String): Single<List<String>> {

        return userRestApi.getObContributors(owner, repo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
            }
    }

}