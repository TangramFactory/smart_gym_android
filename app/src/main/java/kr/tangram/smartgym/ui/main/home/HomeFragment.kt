package kr.tangram.smartgym.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.tangram.smartgym.UserViewModel
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseFragment
import kr.tangram.smartgym.databinding.FragmentHomeBinding
import kr.tangram.smartgym.ui.workout.WorkOutViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, UserViewModel>(R.layout.fragment_home) {

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override val viewModel: UserViewModel by lazy { getViewModel() }
    private val userViewModel : WorkOutViewModel by sharedViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        val auth = Firebase.auth
        binding.tvMain.text = auth.currentUser?.email.toString()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        viewModel.loadToDayJump()
    }

    override fun initLiveData() {

    }

    override fun initListener() {

    }



}