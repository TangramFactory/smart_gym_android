package kr.tangram.smartgym.base

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.core.component.KoinComponent

open class BaseViewModel : ViewModel(), KoinComponent {

    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)


    fun getCompositeDisposable() : CompositeDisposable{
        return compositeDisposable
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun showMessage(message: String)
    {
        Toast.makeText(BaseApplication.context, message, Toast.LENGTH_LONG).show()
    }
}