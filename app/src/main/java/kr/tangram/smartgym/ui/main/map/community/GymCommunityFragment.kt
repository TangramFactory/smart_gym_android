package kr.tangram.smartgym.ui.main.map.community

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseFragment
import kr.tangram.smartgym.databinding.FragmentGymCommunityListBinding
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GymCommunityFragment : BaseFragment<FragmentGymCommunityListBinding, GymViewModel>(R.layout.fragment_gym_community_list)  {
    override val viewModel: GymViewModel by sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGymCommunityListBinding.inflate(inflater)
        binding.layoutAllCommunity.setOnClickListener { startActivity(Intent(requireActivity(), CommunityListActivity::class.java)) }
        val adapter = GymCommunityRecyclerAdapter()
        binding.recyclerViewGymPost.adapter = adapter
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
        fun newInstance() = GymCommunityFragment()
    }
}