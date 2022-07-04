package kr.tangram.smartgym.ui.login.junior

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import butterknife.ButterKnife
import butterknife.OnClick
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityJuniorJoinBinding
import kr.tangram.smartgym.ui.login.LoginEmailSentActivity
import kr.tangram.smartgym.ui.login.LoginViewModel
import kr.tangram.smartgym.util.BackgroundRoundShape
import kr.tangram.smartgym.util.EmailReceiveType
import java.text.SimpleDateFormat
import java.util.*

class JuniorJoinActivity : BaseActivity<ActivityJuniorJoinBinding, JuniorViewModel>(R.layout.activity_junior_join),
    View.OnClickListener {
    override val viewModel: JuniorViewModel by viewModels()
    private var gender = "0"
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJuniorJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButterKnife.bind(this)

        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow);
        binding.header.collapsingToolbar.title = "주니어 계정 만들기"
        binding.btnEmailLink.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))

        binding.btnEmailLink.setOnClickListener { dataCheck() }

        binding.edtEmail.setOnClickListener(this)
        binding.edtBirth.setOnClickListener(this)
        binding.edtName.setOnClickListener(this)

        viewModel.isSaveUserFlag.observe(this){ saveUserCallback(it) }
        onClickMale()
    }

    private fun dataCheck() {
         checkEmailPatterns(binding.edtEmail.text.trim().toString()).run {
             if (!this) return
         }

        checkNamePatterns(binding.edtName.text.trim().toString()).run {
            if (!this) return
        }

        checkBirthDayPatterns14Under(binding.edtBirth.text.trim().toString()).run {
            if (!this) return
        }

        viewModel.savedUserCheck(binding.edtEmail.text.trim().toString())


        binding.btnEmailLink.isEnabled = false


    }



    private fun checkBirthDayPatterns14Under(birth: String): Boolean {
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

    private fun checkEmailPatterns(email: String): Boolean {
        if ( email.isEmpty()){
            failAction("이메일을 입력하여주세요." , FailEditText.EMAIL)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            failAction("이메일 형식이 아닙니다.", FailEditText.EMAIL)
            return false
        }
        return true
    }

    private fun failAction(string: String, failEditText: FailEditText){
        showCustomToast(string, true)

        when(failEditText){
            FailEditText.EMAIL -> binding.edtEmail.background = getDrawable(R.drawable.edt_round_red)
            FailEditText.NAME -> binding.edtName.background = getDrawable(R.drawable.edt_round_red)
            FailEditText.BIRTH -> binding.edtBirth.background = getDrawable(R.drawable.edt_round_red)
        }
    }


    private fun isBirthDayPattern(string: String) : Boolean = string.length == 8 &&
            string.substring(4, 6).toInt()<=12 && string.substring(4, 6).toInt()>0 &&
            string.substring(7).toInt()<=31 && string.substring(7).toInt()>0

    //나이 계산.... //확인하기
    private fun is14Age(string: String) : Boolean {
        val age = 14
        if (string.length != 8) return false

        if ((SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toInt()-string.substring(0, 4).toInt())>age) return false

        if ((SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toInt()-string.substring(0, 4).toInt())==age &&
            (SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().time).toInt()>string.substring(4, 6).toInt())) return false

        if ((SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toInt()-string.substring(0, 4).toInt())==age&&
            (SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().time).toInt()==string.substring(4, 6).toInt()) &&
            (SimpleDateFormat("dd", Locale.getDefault()).format(Calendar.getInstance().time).toInt()>=string.substring(6).toInt())) return false

        return true
    }

    private fun saveUserCallback(isSaveUser : Boolean) {
        if (isSaveUser){
            failAction( "이미 가입되어있는 이메일입니다", FailEditText.EMAIL)
            binding.btnEmailLink.isEnabled = true
        }else{
            val email = binding.edtEmail.text.trim().toString()
            val name = binding.edtName.text.trim().toString()
            val birthDay = binding.edtBirth.text.trim().toString()

            binding.edtEmail.background = getDrawable(R.drawable.edt_round_black)
            viewModel.sendEmail(email, EmailReceiveType.Junior, name, birthDay, gender)
            viewModel.saveEmail(email)
            startActivity(Intent(this, LoginEmailSentActivity::class.java))
        }
    }


    @OnClick(R.id.vg_male)
    fun onClickMale()
    {
        binding.vgMale.isSelected = true
        binding.vgFemale.isSelected = false

        binding.viewMale.visibility = View.VISIBLE
        binding.viewFemal.visibility = View.GONE
        gender = "0"
    }


    @OnClick(R.id.vg_female)
    fun onClickFemale()
    {
        binding.vgMale.isSelected = false
        binding.vgFemale.isSelected = true

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
    }
}

enum class FailEditText{
    EMAIL, NAME, BIRTH
}