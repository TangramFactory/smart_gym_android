package kr.tangram.smartgym.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.ble.BleSmartRopeConnect
import kr.tangram.smartgym.ble.BleSmartRopePopupEvent
import kr.tangram.smartgym.ble.BleSmartRopeState
import kr.tangram.smartgym.databinding.ActivityMainBinding
import kr.tangram.smartgym.ui.challenge.ChallengeFragment
import kr.tangram.smartgym.ui.login.LoginActivity
import kr.tangram.smartgym.ui.map.MapFragment
import kr.tangram.smartgym.util.bleConnection
import kr.tangram.smartgym.util.log
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main
), NavigationBarView.OnItemSelectedListener , BleSmartRopeConnect.SmartRopeInterface{
    override val viewModel: MainViewModel by lazy { getViewModel() }
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.setOnItemSelectedListener(this)






        //
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_LOCATION)
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))   requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED))   requestPermissions( arrayOf(Manifest.permission.BLUETOOTH_ADMIN), REQUEST_CODE_BLUETOOTH_ADMIN )
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED))   requestPermissions( arrayOf(Manifest.permission.BLUETOOTH), REQUEST_CODE_BLUETOOTH )
        if ( ( ContextCompat.checkSelfPermission( this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) && Build.VERSION.SDK_INT > 30 )   requestPermissions( arrayOf(Manifest.permission.BLUETOOTH_SCAN), REQUEST_CODE_BLUETOOTH_SCAN ) // 20220212 new grant
        if ( ( ContextCompat.checkSelfPermission( this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) && Build.VERSION.SDK_INT > 30 )    requestPermissions( arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_CODE_BLUETOOTH_CONNECT )// 20220212 new grant
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))  requestPermissions( arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE )
        if ( ( ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED))  requestPermissions( arrayOf(Manifest.permission.INTERNET), REQUEST_CODE_INTERNET )
        bleConnection.INFO.set(JSONObject())

        try {
                bleConnection.init(this)
            }catch (e:Exception) {}
            bleConnection.AUTOCONNECT = true

            bleConnection.setInterface(this)


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

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("onBackPressed", "~")
    }

    override fun onState(state: BleSmartRopeState, message: JSONObject?) {}
    override fun onCount(event: BleSmartRopePopupEvent) {}
    override fun onRead(data: String) {}
}