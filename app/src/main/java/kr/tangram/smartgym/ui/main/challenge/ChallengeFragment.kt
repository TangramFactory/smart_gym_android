package kr.tangram.smartgym.ui.main.challenge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseFragment
import kr.tangram.smartgym.databinding.FragmentChallengeBinding
import kr.tangram.smartgym.databinding.FragmentMapBinding
import kr.tangram.smartgym.ui.main.map.gym.GymListActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ChallengeFragment : BaseFragment<FragmentChallengeBinding, ChallengeViewModel>(
    R.layout.fragment_challenge
) {

    companion object {
        @JvmStatic
        fun newInstance() = ChallengeFragment()
    }

    override val viewModel: ChallengeViewModel by lazy { getViewModel() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View {
        binding = FragmentChallengeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun initLiveData() {

    }

    override fun initListener() {

    }

}