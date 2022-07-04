package kr.tangram.smartgym.ui.main.map.community

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.delay
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityCommunityListBinding
import kr.tangram.smartgym.ui.main.map.BottomDialogFragment
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class CommunityListActivity : BaseActivity<ActivityCommunityListBinding, GymViewModel>(R.layout.activity_community_list) {
    override val viewModel: GymViewModel by lazy { getViewModel() }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow)
        supportActionBar?.title = "커뮤니티"

        val adapter= GymCommunityRecyclerAdapter()
        binding.recyclerViewCommunityList.adapter = adapter
        val list= arrayListOf<String>()
        list.add("222")
        list.add("222")
        list.add("222")
        list.add("222")
        list.add("222")
        adapter.setList(list)
        binding.toggleBtnAll.setTextColor(getColorStateList(R.color.color_selector))
        binding.toggleBtnGroup.setTextColor(getColorStateList(R.color.color_selector))
        binding.toggleBtnGroup.setOnClickListener {
            BottomDialogFragment.newInstance(list).run {
                setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
                show(supportFragmentManager, BottomDialogFragment::class.java.simpleName)
            }
        }
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
}