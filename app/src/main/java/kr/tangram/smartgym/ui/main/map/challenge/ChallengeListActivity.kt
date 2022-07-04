package kr.tangram.smartgym.ui.main.map.challenge

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityChallengeListBinding
import kr.tangram.smartgym.databinding.ActivityCommunityListBinding
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ChallengeListActivity : BaseActivity<ActivityChallengeListBinding, GymViewModel>(R.layout.activity_community_list){
    override val viewModel: GymViewModel by lazy { getViewModel() }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow)
        supportActionBar?.title = "짐 첼린지"
        val adapter = GymChallengeRecyclerViewAdapter()
        binding.recyclerViewChallengeList.adapter = adapter
        val list= mutableListOf<String>()
        list.add("222")
        list.add("222")
        list.add("222")
        list.add("222")
        list.add("222")
        adapter.setList(list)

        binding.toggleBtnProgress.setTextColor(getColorStateList(R.color.color_selector))
        binding.toggleBtnBefor.setTextColor(getColorStateList(R.color.color_selector))
        binding.toggleBtnAfter.setTextColor(getColorStateList(R.color.color_selector))
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



//    override fun onCheckedChanged(group: SingleSelectToggleGroup?, checkedId: Int) {
//        when(checkedId){
//            R.id.toggle_btn_after ->{
//                binding.toggleBtnAfter.setTextColor(R.color.white)
//                binding.toggleBtnProgress.setTextColor(R.color.color_both_1)
//                binding.toggleBtnBefor.setTextColor(R.color.color_both_1)
//            }
//            R.id.toggle_btn_progress ->{
//                binding.toggleBtnAfter.setTextColor(R.color.color_both_1)
//                binding.toggleBtnProgress.setTextColor(R.color.white)
//                binding.toggleBtnBefor.setTextColor(R.color.color_both_1)
//            }
//            R.id.toggle_btn_befor ->{
//                binding.toggleBtnAfter.setTextColor(R.color.color_both_1)
//                binding.toggleBtnProgress.setTextColor(R.color.color_both_1)
//                binding.toggleBtnBefor.setTextColor(R.color.white)
//            }
//
//        }
//    }
}