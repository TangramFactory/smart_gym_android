package kr.tangram.smartgym.ui.leftMenu

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityProfileSettingBinding
import kr.tangram.smartgym.ImageSize
import kr.tangram.smartgym.UserViewModel
import kr.tangram.smartgym.util.BackgroundRoundShape
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ProfileSettingActivity :
    BaseActivity<ActivityProfileSettingBinding, UserViewModel>(R.layout.activity_profile_setting){
    override val viewModel: UserViewModel by lazy { getViewModel() }
    private var weightUnit = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow)
        supportActionBar?.title = "프로필"

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        ButterKnife.bind(this)
        binding.btnSave.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))

        binding.btnSave.setOnClickListener { saveData() }

        binding.imgBtnCamera.setOnClickListener { getProfileImageFromDevice() }

        initTextChangedListener()

        viewModel.info.observe(this) {
            when (it.userHwUnit) {
                "0" -> onClickCm()
                "1" -> onClickFt()
            }
        }

        Glide.with(this)
            .load(viewModel.getProfileImagePath())
            .error(R.drawable.ic_default_profile)
            .into(binding.imgBtnProfile)

    }



    private fun getProfileImageFromDevice() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent);
    }

    private fun saveData() {
        val name = binding.tvProfileName.text.toString()
        val gender = binding.tvProfileGender.text.toString()
        val birthDate = binding.tvBirthDate.text.toString()
        val nickName = binding.edtProfileNickName.text.toString()
        val intro = binding.tvProfileSelfIntroduction.text.toString()
        var height = "0"

        when (weightUnit) {
            "0" -> height = if (!binding.edtProfileHeight.text.isNullOrEmpty()) binding.edtProfileHeight.text.toString() else "0"

            "1" -> {
                val heightFt =
                    if (!binding.edtProfileHeightFt.text.isNullOrEmpty()) binding.edtProfileHeightFt.text.toString() else "0"
                var heightIn =
                    if (!binding.edtProfileHeightIn.text.isNullOrEmpty()) binding.edtProfileHeightIn.text.toString() else "0"
                if (heightIn[heightIn.lastIndex] == '.'){
                   heightIn = heightIn.substring(0, heightIn.length-1)
                }
                height = String.format("%s\'%s", heightFt, heightIn)
            }
        }
        var weight = 0
        var tempWeight =
            if (!binding.edtProfileWeight.text.isNullOrEmpty()) binding.edtProfileWeight.text.toString() else "0"

        if (tempWeight[tempWeight.lastIndex] == '.'){
            weight = tempWeight.substring(0, tempWeight.length-1).toInt()
        }else {
            weight= tempWeight.toInt()
        }
        val goal =
            if (!binding.edtProfileGoal.text.isNullOrEmpty()) binding.edtProfileGoal.text.toString().toInt() else 0
        val userInfo = viewModel.info.value!!
            .copy(userName = name, userGender = gender, userBirthday = birthDate, userNickname = nickName, userIntroduce = intro, userHwUnit = weightUnit, userHeight = height, userWeight = weight, userDailyGoal = goal)
        Log.d("userInfo", userInfo.toString())
        viewModel.setProfile(userInfo)

    }


    @OnClick(R.id.RBtnCmKg)
    fun onClickCm()
    {
        binding.RBtnCmKg.isSelected = true
        binding.RBtnFtIn.isSelected = false

        binding.layoutHeight.visibility = View.VISIBLE
        binding.layoutHeightFt.visibility = View.GONE

        binding.viewProfileCmKg.visibility = View.VISIBLE
        binding.viewProfileFtIn.visibility = View.GONE
        weightUnit = "0"
        binding.tvProfileWeighUnit.text="kg"

    }


    @OnClick(R.id.RBtnFtIn)
    fun onClickFt()
    {
        binding.RBtnCmKg.isSelected = false
        binding.RBtnFtIn.isSelected = true

        binding.layoutHeight.visibility = View.GONE
        binding.layoutHeightFt.visibility = View.VISIBLE

        binding.viewProfileCmKg.visibility = View.GONE
        binding.viewProfileFtIn.visibility = View.VISIBLE
        weightUnit = "1"
        binding.tvProfileWeighUnit.text="lbs"
    }


    private fun initTextChangedListener() {
        binding.edtProfileHeight.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(height: CharSequence?, p1: Int, p2: Int, p3: Int) {
                height.toString().replaceFirst("0", "")
                if ( !height.isNullOrEmpty() && height.toString().toFloat() >= MAX_HEIGHT+1) binding.edtProfileHeight.setText(MAX_HEIGHT.toString())
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.edtProfileWeight.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(weight: CharSequence?, p1: Int, p2: Int, p3: Int) {
                weight.toString().replaceFirst("0", "")
                if (!weight.isNullOrEmpty() && weight.toString().toFloat() >= MAX_WIGHT+1 ) binding.edtProfileWeight.setText(MAX_WIGHT.toString())
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.edtProfileGoal.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(goal: CharSequence?, p1: Int, p2: Int, p3: Int) {
                goal.toString().replaceFirst("0", "")
                if (!goal.isNullOrEmpty() && goal.toString().toFloat() >= MAX_GOAL+1) binding.edtProfileGoal.setText(MAX_GOAL.toString())
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.edtProfileHeightFt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(hightFt: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!hightFt.isNullOrEmpty() && hightFt.toString().toFloat() >= MAX_HEIGHT_FT+1 ) binding.edtProfileHeightFt.setText(MAX_HEIGHT_FT.toString())
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.edtProfileHeightIn.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(hightIn: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!hightIn.isNullOrEmpty() && hightIn.toString().toFloat() >= MAX_HEIGHT_IN+1 ) binding.edtProfileHeightIn.setText(MAX_HEIGHT_IN.toString())
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })

    }



    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data!!

                Glide.with(this)
                    .asBitmap()
                    .override(600, 600)
                    .load(intent.data)
                    .error(R.drawable.ic_default_profile)
                    .into(targetLargeImage)

                Glide.with(this)
                    .asBitmap()
                    .override(200, 200)
                    .load(intent.data)
                    .into(targetSmallImage)
            }
        }

    private val targetLargeImage = object : CustomTarget<Bitmap>() {
        override fun onResourceReady(
            resource: Bitmap,
            transition: Transition<in Bitmap>?,
        ) { // STORAGE UPLOAD
            viewModel.saveImage(resource, binding.imgBtnProfile, ImageSize.Large)
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    }

    private val targetSmallImage = object : CustomTarget<Bitmap>() {
        override fun onResourceReady(
            resource: Bitmap,
            transition: Transition<in Bitmap>?,
        ) { // STORAGE UPLOAD
            viewModel.saveImage(resource, binding.imgBtnProfile, ImageSize.Small)
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
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

    companion object{
        const val MAX_HEIGHT = 300
        const val MAX_WIGHT = 200
        const val MAX_GOAL = 1000000
        const val MAX_HEIGHT_FT = 6
        const val MAX_HEIGHT_IN = 9
    }
}