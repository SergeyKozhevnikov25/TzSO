package space.game.android.tzsantehnikaonline.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.*
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import space.game.android.tzsantehnikaonline.R
import space.game.android.tzsantehnikaonline.databinding.FragmentMapBinding
import space.game.android.tzsantehnikaonline.presentation.info.InfoFragment
import space.game.android.tzsantehnikaonline.presentation.utils.UtilLocation
import space.game.android.tzsantehnikaonline.presentation.utils.getBitmapFromVectorDrawable

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentMapBinding = null")

    private lateinit var mapView: MapView
    private lateinit var mapKit: MapKit
    private lateinit var viewModel: MapViewModel

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.map { it.value }.none { !it }) {
            if (viewModel.placeMark.value == null) {
                getLastLocation()
            }
        } else {
            viewModel.setLocation(Point(RANDOM_LATITUDE, RANDOM_LONGITUDE))
            Toast.makeText(
                requireContext(),
                getString(R.string.location_request_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private var locationManager: LocationManager? = null
    private var myLocationListener: LocationListener? = null

    private val inputListener: InputListener = object : InputListener {
        override fun onMapTap(p0: Map, p1: Point) {
            viewModel.setPlaceMark(p1)
        }

        override fun onMapLongTap(p0: Map, p1: Point) {}
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        mapKit = MapKitFactory.getInstance()
        mapView = binding.mapView
        val lat = arguments?.getDouble(LATITUDE_ARG)
        val long = arguments?.getDouble(LONGITUDE_ARG)
        if (long != null && lat != null) {
            viewModel.setLocation(Point(lat, long))
            viewModel.setPlaceMark(Point(lat, long))
        } else {
            startLocationPermissionRequest()
        }
        setupMap()
        setupViewModel()

        binding.btnAddPoint.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    InfoFragment.newInstance(viewModel.placeMark.value)
                )
                .commit()
        }
    }

    private fun setupViewModel() {
        viewModel.location.observe(viewLifecycleOwner) {
            setLocation(it)
        }

        viewModel.placeMark.observe(viewLifecycleOwner) {
            setPlaceMark(it)
        }
    }

    private fun setupMap() {
        locationManager = MapKitFactory.getInstance().createLocationManager()
        myLocationListener = object : LocationListener {
            override fun onLocationUpdated(location: Location) {
                if (viewModel.placeMark.value == null) {
                    viewModel.setLocation(location.position)
                }
            }

            override fun onLocationStatusUpdated(locationStatus: LocationStatus) {}
        }
        mapView.map.isFastTapEnabled = true
        mapView.map.addInputListener(inputListener)
    }

    private fun setLocation(point: Point) {
        mapView.map.move(CameraPosition(point, COMFORTABLE_ZOOM_LEVEL, 0.0f, 0.0f))
//        setPlaceMark(point)
    }

    private fun setPlaceMark(point: Point) {
        mapView.map.mapObjects.clear()
        mapView.map.mapObjects.addPlacemark(
            point,
            ImageProvider.fromBitmap(requireContext().getBitmapFromVectorDrawable(R.drawable.ic_map_pin))
        )
    }

    private fun subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager!!.subscribeForLocationUpdates(
                DESIRED_ACCURACY,
                MINIMAL_TIME,
                MINIMAL_DISTANCE,
                USE_IN_BACKGROUND,
                FilteringMode.OFF,
                myLocationListener!!
            )
        }
    }

    private fun startLocationPermissionRequest() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            if (viewModel.placeMark.value == null) {
                getLastLocation()
            }
        }
    }

    private fun getLastLocation() {
        val location = UtilLocation.getLastKnownLocation(requireContext())
        if (location != null) {
            viewModel.setLocation(Point(location.latitude, location.longitude))
        } else {
            viewModel.setLocation(Point(RANDOM_LATITUDE, RANDOM_LONGITUDE))
            Toast.makeText(
                requireContext(),
                getString(R.string.location_search_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onStart() {
        mapView.onStart()
        mapKit.onStart()
        subscribeToLocationUpdate()
        super.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        mapKit.onStop()
        myLocationListener?.let { locationManager?.unsubscribe(it) }
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val RANDOM_LATITUDE = 55.7522
        private const val RANDOM_LONGITUDE = 37.6156
        private const val COMFORTABLE_ZOOM_LEVEL = 14f
        private const val DESIRED_ACCURACY = 0.0
        private const val MINIMAL_TIME: Long = 1000
        private const val MINIMAL_DISTANCE = 1.0
        private const val USE_IN_BACKGROUND = false
        private const val LATITUDE_ARG = "LATITUDE_ARG"
        private const val LONGITUDE_ARG = "LONGITUDE_ARG"

        fun newInstance(location: Point?): MapFragment {
            return if (location == null) {
                MapFragment()
            } else {
                MapFragment().apply {
                    arguments = bundleOf(
                        LATITUDE_ARG to location.latitude,
                        LONGITUDE_ARG to location.longitude
                    )
                }
            }
        }
    }
}