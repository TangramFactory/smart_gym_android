package kr.tangram.smartgym.ui.main.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseFragment
import kr.tangram.smartgym.databinding.FragmentMapBinding
import kr.tangram.smartgym.ui.main.map.gym.GymListActivity
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel


// TODO: Rename parameter arguments, choose names that match

class MapFragment : BaseFragment<FragmentMapBinding, GymViewModel>(R.layout.fragment_map), OnMapReadyCallback {

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }

    override val viewModel: GymViewModel by lazy { getViewModel() }

    private var mLocationPermissionGranted = false
    private var mapFragment: SupportMapFragment? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View {
        binding = FragmentMapBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.floatingButton.setOnClickListener{ startActivity(Intent(requireActivity(), GymListActivity::class.java))}

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        getLocationPermission()
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true


        val sydney = LatLng(37.58, 126.88)
        googleMap.addMarker(MarkerOptions().position(sydney))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18F))
    }
    private fun getLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }
    override fun initLiveData() {

    }

    override fun initListener() {

    }

}