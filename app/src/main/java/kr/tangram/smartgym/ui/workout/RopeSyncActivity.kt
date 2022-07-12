package kr.tangram.smartgym.ui.workout

import android.os.Bundle
import android.util.Log
import com.r0adkll.slidr.Slidr
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityRopeSyncBinding
import kr.tangram.smartgym.util.Define
import org.koin.androidx.viewmodel.ext.android.viewModel


class RopeSyncActivity : BaseActivity<ActivityRopeSyncBinding, WorkOutViewModel>(R.layout.activity_rope_sync) {
    override val viewModel: WorkOutViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var identifer = intent.getStringExtra(Define.Extra.Identifier)
        binding = ActivityRopeSyncBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RopeSyncFragment.newInstance(identifer!!).show(supportFragmentManager, "")
    }


    override fun onBackPressed() {
        Log.e("T","T")
    }
}