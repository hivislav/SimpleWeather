package ru.hivislav.simpleweather.view.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import ru.hivislav.simpleweather.R
import ru.hivislav.simpleweather.databinding.FragmentGoogleMapsMainBinding

class MapsFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentGoogleMapsMainBinding? = null
    private val binding: FragmentGoogleMapsMainBinding
        get() {
            return _binding!!
        }

    private lateinit var map: GoogleMap
    //получаем менеджера из сервиса и приводим его
    private val locationManager by lazy { requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        googleMap.setOnMapLongClickListener {
            getAddress(it)
        }

        checkPermission()

        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoogleMapsMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.buttonSearch.setOnClickListener {
            search()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        _binding = null
    }

    private fun checkPermission() {
        context?.let {
            when {
                //если доступ к геолокации есть
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    map.isMyLocationEnabled = true
                    getLocation()
                }
                //если доступа нет или он был отменен -> запрашиваем рационализацию
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    showRationaleDialog()
                }
                //запрашиваем разрешение
                else -> {
                    myRequestPermission()
                }
            }
        }
    }

    //результат запроса на разрешение доступа
    @SuppressLint("MissingPermission")
    private val permissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            map.isMyLocationEnabled = true
        } else {
            Toast.makeText(context, R.string.message_dialog_rationale_maps, Toast.LENGTH_SHORT).show()
        }
    }

    //функция запроса для получения разрешения
    private fun myRequestPermission() {
        permissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.title_dialog_rationale_maps)
            .setMessage(R.string.message_dialog_rationale_maps)
            .setPositiveButton(R.string.positive_button_rationale_dialog) { _, _ ->
                myRequestPermission()
            }
            .setNegativeButton(R.string.negative_button_rationale_dialog) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun getAddress(location: LatLng) {
        launch {
            val geocoder = Geocoder(requireContext())
            val job = async(Dispatchers.IO) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                }
            val listAddress = job.await()
            if (isActive) {
                binding.textAddress.text = listAddress[0].getAddressLine(0)
            }
        }
    }

    private fun search() {
        launch {
            val geocoder = Geocoder(requireContext())
            val job = async(Dispatchers.IO) {
                geocoder.getFromLocationName(binding.searchAddress.text.toString(), 1)
            }
            val listAddress = job.await()
            if (isActive) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        listAddress[0].latitude,
                        listAddress[0].longitude),
                    15f
                ))
                map.addMarker(MarkerOptions().position(LatLng(listAddress[0].latitude, listAddress[0].longitude)))
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getLocation() {
        activity?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                //если выбранный провайдер включен вешаем слушатель
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    val providerGPS = locationManager.getProvider(LocationManager.NETWORK_PROVIDER)
                    providerGPS?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0L,
                            0f,
                            locationListener
                        )
                    }

                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val providerGPS = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    providerGPS?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0L,
                            0f,
                            locationListener
                        )
                    }
                }

                else {
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    lastLocation?.let { location ->
                        getAddress(LatLng(location.latitude, location.longitude))
                    }
                }
            }
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            getAddress(LatLng(location.latitude, location.longitude))
            map.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
            locationManager.removeUpdates(this)
        }
    }

    companion object {
        fun newInstance() = MapsFragment()
    }
}