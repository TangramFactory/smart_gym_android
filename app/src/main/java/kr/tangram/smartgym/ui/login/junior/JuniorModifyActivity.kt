package kr.tangram.smartgym.ui.login.junior

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import butterknife.ButterKnife
import butterknife.OnClick
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.data.remote.model.UserInfo
import kr.tangram.smartgym.databinding.ActivityJuniorModifyBinding
import kr.tangram.smartgym.ui.login.LoginViewModel
import kr.tangram.smartgym.util.BackgroundRoundShape
import java.text.SimpleDateFormat
import java.util.*

class JuniorModifyActivity: BaseActivity<ActivityJuniorModifyBinding, JuniorViewModel>(R.layout.activity_junior_modify),
View.OnClickListener {
    override val viewModel: JuniorViewModel by viewModels()
    private var gender = "0"
    private lateinit var info : UserInfo
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJuniorModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButterKnife.bind(this)

        info = intent.getSerializableExtra("info") as UserInfo

        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);
        binding.header.collapsingToolbar.title = "주니어 계정 만들기"

        binding.tvUpdateFailScript.compoundDrawablePadding = 10
        binding.btnEmailLink.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))

        binding.tvUpdateEmail.text = info.userEmail
        binding.edtUpdateName.setText(info.userName)
        binding.edtUpdateBirth.setText(info.userBirthday)

        when(info.userGender){
            "1" -> onClickMale()
            "2" -> onClickFemale()
            else ->  onClickMale()
        }

        binding.btnEmailLink.setOnClickListener { saveData() }

        binding.edtUpdateBirth.setOnClickListener(this)
        binding.edtUpdateName.setOnClickListener(this)

    }

    private fun saveData() {
        checkNamePatterns(binding.edtUpdateName.text.trim().toString()).run {
            if (!this) return
        }

        checkBirthDayPatterns(binding.edtUpdateBirth.text.trim().toString()).run {
            if (!this) return
        }

        val name = binding.edtUpdateName.text.trim().toString()
        val birthDay = binding.edtUpdateBirth.text.trim().toString()

        viewModel.modifyJunior(info.userUid, name, gender.toInt(), birthDay)

        binding.btnEmailLink.isEnabled = false

    }

    private fun checkBirthDayPatterns(birth: String): Boolean {
        if (birth.isNullOrEmpty()){
            failAction("생년월일을 입력하여주세요", FailEditText.BIRTH)
            return false
        }

        if (!isBirthDayPattern(birth) ){
            failAction("생년월일을 확인하여주세요", FailEditText.BIRTH)
            return false
        }

        if (!isBirthDayPattern(birth)){
            failAction("생년월일을 확인하여주세요", FailEditText.BIRTH)
            return false
        }

        if (!is14Age(birth)){
            failAction("만13세 미만이 아닙니다 확인하여주세요", FailEditText.BIRTH)
            return false
        }

        return true
    }

    private fun checkNamePatterns(name: String): Boolean {
        if (name.isNullOrEmpty()){
            failAction("이름을 입력하여주세요", FailEditText.NAME)
            return false
        }
        return true
    }


    private fun failAction(string: String, failEditText: FailEditText){
        binding.tvUpdateFailScript.setText(string)
        binding.tvUpdateFailScript.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_fail_maker_red), null, null,null)
        binding.tvUpdateFailScript.visibility = View.VISIBLE

        when(failEditText){
            FailEditText.NAME -> binding.edtUpdateName.background = getDrawable(R.drawable.edt_round_red)
            FailEditText.BIRTH -> binding.edtUpdateBirth.background = getDrawable(R.drawable.edt_round_red)
        }
    }

    private fun isBirthDayPattern(string: String) : Boolean = string.length == 8 &&
            string.substring(4, 6).toInt()<=12 && string.substring(4, 6).toInt()>0 &&
            string.substring(7).toInt()<=31 && string.substring(7).toInt()>0


    private fun is14Age(string: String) : Boolean {
        if (string.length != 8) return false

        if ((SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toInt()-string.substring(0, 4).toInt())>14) return false

        if ((SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toInt()-string.substring(0, 4).toInt())==14 &&
            (SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().time).toInt()>string.substring(4, 6).toInt())) return false

        if ((SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toInt()-string.substring(0, 4).toInt())==14&&
            (SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().time).toInt()==string.substring(4, 6).toInt()) &&
            (SimpleDateFormat("dd", Locale.getDefault()).format(Calendar.getInstance().time).toInt()>=string.substring(6).toInt())) return false

        return true
    }

    @OnClick(R.id.vg_update_male)
    fun onClickMale()
    {
        binding.vgUpdateMale.isSelected = true
        binding.vgUpdateFemale.isSelected = false

        binding.viewMale.visibility = View.VISIBLE
        binding.viewFemal.visibility = View.GONE
        gender = "0"
    }


    @OnClick(R.id.vg_update_female)
    fun onClickFemale()
    {
        binding.vgUpdateMale.isSelected = false
        binding.vgUpdateFemale.isSelected = true

        binding.viewMale.visibility = View.GONE
        binding.viewFemal.visibility = View.VISIBLE
        gender = "1"
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

    override fun onClick(editText: View?) {
        editText?.background = getDrawable(R.drawable.edt_round_black)
        binding.tvUpdateFailScript.visibility = View.INVISIBLE
    }
}
