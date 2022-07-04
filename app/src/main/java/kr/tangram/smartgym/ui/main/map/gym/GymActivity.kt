package kr.tangram.smartgym.ui.main.map.gym

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.data.domain.model.GymInfo
import kr.tangram.smartgym.databinding.ActivityGymBinding
import kr.tangram.smartgym.ui.main.challenge.ChallengeFragment
import kr.tangram.smartgym.ui.main.map.challenge.GymChallengeFragment
import kr.tangram.smartgym.ui.main.map.group.GymGroupFragment
import kr.tangram.smartgym.ui.main.map.community.GymCommunityFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class GymActivity : BaseActivity<ActivityGymBinding, GymViewModel>(R.layout.activity_gym) {
    private lateinit var gymInfo: GymInfo
    private lateinit var navController: NavController
    private val tabNameArray = arrayListOf("챌린지", "커뮤니티", "그룹")
    override val viewModel: GymViewModel by lazy { getViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGymBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow)
        supportActionBar?.title = "짐 정보"
        gymInfo = intent.getSerializableExtra("gymInfo") as GymInfo
        binding.model = gymInfo
        val fragmentAdapter = FragmentAdapter(this)
        binding.gymViewPager.adapter = fragmentAdapter
        val leaderBoardListAdapter = LeaderBoardListAdapter()
//        binding.recyclerViewGymReaderBoard.adapter = leaderBoardListAdapter

        TabLayoutMediator(binding.gymTabLayout, binding.gymViewPager) { tab, position ->
            tab.text = tabNameArray[position]
        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class FragmentAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> GymChallengeFragment.newInstance()
                1 -> GymCommunityFragment.newInstance()
                2 -> GymGroupFragment.newInstance()
                else -> ChallengeFragment.newInstance()
            }
        }
    }
}