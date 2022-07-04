package kr.tangram.smartgym.ui.main.map.challenge

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseFragment
import kr.tangram.smartgym.databinding.FragmentGymChallengeListBinding
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * A fragment representing a list of Items.
 */
class GymChallengeFragment : BaseFragment<FragmentGymChallengeListBinding, GymViewModel>(R.layout.fragment_gym_challenge_list) {
    override val viewModel: GymViewModel by  sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGymChallengeListBinding.inflate(inflater)
        binding.layoutJoinedChallenge.setOnClickListener { startActivity(Intent(requireActivity(), ChallengeListActivity::class.java)) }
        val adapter = GymChallengeRecyclerViewAdapter()
        binding.recyclerViewGymChallenge.adapter = adapter

        val list= mutableListOf<String>()
        list.add("222")
        list.add("222")
        list.add("222")
        list.add("222")
        list.add("222")
        adapter.setList(list)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = GymChallengeFragment()
    }


}