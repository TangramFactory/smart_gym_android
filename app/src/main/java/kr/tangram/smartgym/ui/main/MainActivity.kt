package kr.tangram.smartgym.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationBarView

import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityMainBinding
import kr.tangram.smartgym.ui.challenge.ChallengeFragment
import kr.tangram.smartgym.ui.map.MapFragment

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main
), NavigationBarView.OnItemSelectedListener {
    override val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.bottomNavigationView.setOnItemSelectedListener(this)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayout.id, MainFragment.newInstance())
            .commitAllowingStateLoss()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.page_home -> {
                supportFragmentManager.beginTransaction().replace(binding.frameLayout.id,
                    MainFragment.newInstance()).commit()
                return true
            }
            R.id.page_map ->{
                supportFragmentManager.beginTransaction().replace(binding.frameLayout.id, MapFragment.newInstance()).commit()
                return true
            }

            R.id.page_challenge ->{
                supportFragmentManager.beginTransaction().replace(binding.frameLayout.id, ChallengeFragment.newInstance()).commit()
                return true
            }
        }
        return false
    }


    override fun initLiveData()
    {
        viewModel.name.observe(this) {
            binding.apply {
            }
        }
    }
}