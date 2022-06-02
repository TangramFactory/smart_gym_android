package kr.tangram.smartgym.ui.challenge

import android.os.Bundle
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseFragment
import kr.tangram.smartgym.databinding.FragmentChallengeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChallengeFragment : BaseFragment<FragmentChallengeBinding, ChallengeViewModel>(
    R.layout.fragment_challenge
) {

    companion object {
        @JvmStatic
        fun newInstance() = ChallengeFragment()
    }

    override val viewModel: ChallengeViewModel by viewModel()

    override fun initLiveData() {

    }

    override fun initListener() {

    }

}