package kr.tangram.smartgym.ui.challenge

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.tangram.smartgym.R

class ChallengeFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ChallengeFragment()
    }

    private lateinit var viewModel: ChallengeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(this)[ChallengeViewModel::class.java]

        return inflater.inflate(R.layout.fragment_challenge, container, false)
    }

}