package kr.tangram.smartgym.ui.main.map.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseFragment

import kr.tangram.smartgym.databinding.FragmentGymGroupListBinding
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A fragment representing a list of Items.
 */
class GymGroupFragment :  BaseFragment<FragmentGymGroupListBinding, GymViewModel>(R.layout.fragment_gym_group_list)  {
    override val viewModel: GymViewModel by sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGymGroupListBinding.inflate(inflater)
        val adapter = GymGroupRecyclerAdapter()
        binding.recyclerViewGymGroup.adapter = adapter
        val list = mutableListOf<String>()
        list.add("12309")
        list.add("12309")
        list.add("12309")
        list.add("12309")
        list.add("12309")
        list.add("12309")
        list.add("12309")

        adapter.setList(list)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = GymGroupFragment()
    }


}