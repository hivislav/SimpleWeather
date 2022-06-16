package ru.hivislav.simpleweather.view.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import ru.hivislav.simpleweather.R
import ru.hivislav.simpleweather.databinding.FragmentMainBinding
import ru.hivislav.simpleweather.model.entities.City
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.view.contacts.ContactsFragment
import ru.hivislav.simpleweather.view.details.DetailsFragment
import ru.hivislav.simpleweather.view.history.HistoryFragment
import ru.hivislav.simpleweather.viewmodel.AppStateMain
import ru.hivislav.simpleweather.viewmodel.MainViewModel

class MainFragment : Fragment(), OnItemClickListener, CoroutineScope by MainScope() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
    get() {
        return _binding!!
    }

    private var isRussian = true

    //Инициализируем ViewModel (провайдер возвращает уже имеющуюся, а если ее нет, то создает)
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val mainAdapter = MainFragmentAdapter(this)

    //получаем менеджера из сервиса и приводим его
    private val locationManager by lazy { requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        //Получаем LiveData и подписываемся на ее изменения
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer<AppStateMain>{renderData(it)})

        binding.mainFragmentFAB.setOnClickListener {
            sendRequestForChangeCitiesList()
        }

        binding.mainFragmentFABLocation.setOnClickListener {
            checkPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        _binding = null
    }

    private fun initView() {
        setHasOptionsMenu(true)
        binding.mainFragmentRecyclerView.adapter = mainAdapter
        loadChooseCitiesList()
    }

    private fun renderData(appStateMain: AppStateMain) {
        when (appStateMain) {
            is AppStateMain.Error -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                Snackbar.make(binding.root, "Error", Snackbar.LENGTH_LONG)
                    .setAction("Попробовать еще раз") {
                        sendRequestForChangeCitiesList()
                    }.show()
            }
            is AppStateMain.Loading -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is AppStateMain.Success -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                mainAdapter.setWeather(appStateMain.weatherData)
                Snackbar.make(binding.root, "Success", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendRequestForChangeCitiesList() {
        isRussian = !isRussian
        if (isRussian) {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        } else {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        }
        saveChooseCitiesList()
    }

    private fun loadChooseCitiesList() {
        activity?.let {
            isRussian = activity
                ?.getPreferences(Context.MODE_PRIVATE)
                ?.getBoolean(IS_RUSSIAN_CITIES_LIST, true)
                ?: true
        }
        if (isRussian) {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        } else {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        }
    }

    private fun saveChooseCitiesList() {
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)?.edit()
        sharedPreferences?.putBoolean(IS_RUSSIAN_CITIES_LIST, isRussian)
        sharedPreferences?.apply()
    }

    //по клику парсим погоду и показываем детальный экран
    override fun onItemClick(weather: Weather) {
        openDetailsFragment(weather)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_history -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.container, HistoryFragment.newInstance())
                    .addToBackStack("").commit()
                true
            }

            R.id.menu_contacts -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.container, ContactsFragment.newInstance())
                    .addToBackStack("").commit()
                true
            }
            else -> {false}
        }
    }

    //функция для проверки наличия нужных разрешений
    private fun checkPermission() {
        context?.let {
            when {
                //если доступ к контактам есть
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED -> {
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
    private val permissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getLocation()
        } else {
            Toast.makeText(context, "Нужно разрешение на доступ к геолокации", Toast.LENGTH_SHORT).show()
        }
    }

    //функция запроса для получения разрешения
    private fun myRequestPermission() {
        permissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @Suppress("DEPRECATION")
    private fun getLocation() {
        activity?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                //если выбранный провайдер включен вешаем слушатель
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val providerGPS = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    providerGPS?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0L,
                            0f,
                            locationListener
                        )
                    }
                } else {
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    lastLocation?.let {
                        getAddress(it)
                    }
                }
            }
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            getAddress(location)
            locationManager.removeUpdates(this)
        }
    }

    private fun getAddress(location: Location) {
        val geocoder = Geocoder(requireContext())
        launch {
            val job = async(Dispatchers.IO) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            }
            val addresses = job.await()
            if (isActive) {
                showAddressDialog(addresses[0].getAddressLine(0), location)
            }
        }
    }

    private fun showAddressDialog(address: String, location: Location) {
        AlertDialog.Builder(requireContext())
            .setTitle("Ваш адрес")
            .setMessage(address)
            .setPositiveButton("Узнать погоду") { _, _ ->
                openDetailsFragment(
                    Weather(City(address, location.latitude, location.longitude))
                )
            }
            .setNegativeButton("Закрыть") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к геолокации")
            .setMessage("Очень нужно, иначе дело плохо")
            .setPositiveButton("Предоставить доступ") { _, _ ->
                myRequestPermission()
            }
            .setNegativeButton("Не надо") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun openDetailsFragment(weather: Weather) {
        val bundle = Bundle()
        bundle.putParcelable(DetailsFragment.DETAIL_FRAGMENT_BUNDLE_KEY, weather)
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.container, DetailsFragment.newInstance(bundle))
            .addToBackStack("").commit()
    }

    companion object {
        const val IS_RUSSIAN_CITIES_LIST = "isRussianCitiesList"

        fun newInstance() = MainFragment()
    }
}
