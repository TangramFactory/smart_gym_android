package kr.tangram.smartgym.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.tangram.smartgym.UserViewModel

import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.ble.BleSmartRopeConnect
import kr.tangram.smartgym.ble.BleSmartRopePopupEvent
import kr.tangram.smartgym.ble.BleSmartRopeState
import kr.tangram.smartgym.ble.BluetoothService
import kr.tangram.smartgym.databinding.LayoutDrawerLeftBinding
import kr.tangram.smartgym.ui.device.DeviceManagerActivity
import kr.tangram.smartgym.ui.leftMenu.GymCreateActivity
import kr.tangram.smartgym.ui.login.LoginActivity
import kr.tangram.smartgym.ui.login.junior.JuniorActivity
import kr.tangram.smartgym.ui.main.challenge.ChallengeFragment
import kr.tangram.smartgym.ui.main.home.HomeFragment
import kr.tangram.smartgym.ui.main.map.MapFragment
import kr.tangram.smartgym.ui.leftMenu.ProfileSettingActivity
import kr.tangram.smartgym.ui.workout.WorkOutViewModel
import kr.tangram.smartgym.util.bleConnection
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.component.inject

class MainActivity : BaseActivity<LayoutDrawerLeftBinding, UserViewModel>(
    R.layout.layout_drawer_left
), NavigationBarView.OnItemSelectedListener , BleSmartRopeConnect.SmartRopeInterface,
    View.OnClickListener {
    override val viewModel: UserViewModel by lazy { getViewModel() }
    private val workOutViewModel : WorkOutViewModel by inject()

    val tag =  javaClass.name
    //
    private val REQUEST_CODE_LOCATION = 101
    private val REQUEST_CODE_FINE_LOCATION = 106
    private val REQUEST_CODE_BLUETOOTH_ADMIN = 102
    private val REQUEST_CODE_BLUETOOTH = 103
    private val REQUEST_CODE_STORAGE = 104
    private val REQUEST_CODE_INTERNET = 105
    private val REQUEST_CODE_BLUETOOTH_SCAN = 107 // 20220212
    private val REQUEST_CODE_BLUETOOTH_CONNECT = 108 // 20220212


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDrawerLeftBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel

        setSupportActionBar(binding.activityMain.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        binding.activityMain.bottomNavigationView.setOnItemSelectedListener(this)

        initDrawerLayout()
        initPermission()


        supportFragmentManager.beginTransaction()
            .replace(binding.activityMain.frameLayout.id, HomeFragment.newInstance())
            .commitAllowingStateLoss()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initPermission() {
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_LOCATION)
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))   requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED))   requestPermissions( arrayOf(Manifest.permission.BLUETOOTH_ADMIN), REQUEST_CODE_BLUETOOTH_ADMIN )
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED))   requestPermissions( arrayOf(Manifest.permission.BLUETOOTH), REQUEST_CODE_BLUETOOTH )
        if ( ( ContextCompat.checkSelfPermission( this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) && Build.VERSION.SDK_INT > 30 )   requestPermissions( arrayOf(Manifest.permission.BLUETOOTH_SCAN), REQUEST_CODE_BLUETOOTH_SCAN ) // 20220212 new grant
        if ( ( ContextCompat.checkSelfPermission( this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) && Build.VERSION.SDK_INT > 30 )    requestPermissions( arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_CODE_BLUETOOTH_CONNECT )// 20220212 new grant
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))  requestPermissions( arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE )
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED))  requestPermissions( arrayOf(Manifest.permission.INTERNET), REQUEST_CODE_INTERNET )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initDrawerLayout() {
        binding.menuUserAccount.setOnClickListener(this)
        binding.menuUserProfile.setOnClickListener(this)
        binding.menuUserBadge.setOnClickListener(this)
        binding.menuDeviceSetting.setOnClickListener(this)
        binding.menuAppSettings.setOnClickListener(this)
        binding.menuNotice.setOnClickListener(this)
        binding.menuContactUs.setOnClickListener(this)
        binding.menuLogout.setOnClickListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.page_home -> {
                supportFragmentManager.beginTransaction().replace(binding.activityMain.frameLayout.id,
                    HomeFragment.newInstance()).commit()
                return true
            }
            R.id.page_map ->{
                supportFragmentManager.beginTransaction().replace(binding.activityMain.frameLayout.id, MapFragment.newInstance()).commit()
                return true
            }

            R.id.page_challenge ->{
                supportFragmentManager.beginTransaction().replace(binding.activityMain.frameLayout.id, ChallengeFragment.newInstance()).commit()
                return true
            }
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> binding.mainDrawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }



    override fun initLiveData()
    {
        viewModel.name.observe(this) {
            binding.apply {
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onState(state: BleSmartRopeState, message: JSONObject?) {}
    override fun onCount(event: BleSmartRopePopupEvent) {}
    override fun onRead(data: String) {}


    override fun onClick(view: View?) {
        when(view?.id){
            R.id.menu_user_profile -> startActivity(Intent(this, ProfileSettingActivity::class.java))
            R.id.menu_user_account -> startActivity(Intent(this, JuniorActivity::class.java))
            R.id.menu_logout-> {
                Log.d("눌림", "~")
                Firebase.auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.menu_user_badge -> return
            R.id.menu_device_setting-> startActivity(Intent(this, DeviceManagerActivity::class.java))
            R.id.menu_app_settings->return
            R.id.menu_notice-> return
            R.id.menu_contact_us-> startActivity(Intent(this, GymCreateActivity::class.java))
        }
    }
}