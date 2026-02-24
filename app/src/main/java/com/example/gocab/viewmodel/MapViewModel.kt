package com.example.gocab.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gocab.domainlayer.RouteResult
import com.example.gocab.module.CarType
import com.example.gocab.module.RideState
import com.example.gocab.repositories.DirectionRepository
import com.example.gocab.repositories.LocationRepository
import com.example.gocab.repositories.MarkerRepository
import com.example.gocab.utilities.DestinationMarker
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class MapViewModel @Inject constructor(
    private val markerRepository: MarkerRepository,
    private val locationRepository: LocationRepository,
    private val directionRepository: DirectionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    companion object {
        private const val KEY_RIDE_PRICE = "ride_price"
        private const val KEY_DISTANCE_KM = "distance_km"
        private const val KEY_DURATION_MIN = "duration_min"
        private const val KEY_SELECTED_CAR = "selected_car"
    }

    private val _price = MutableStateFlow(savedStateHandle.get<Double?>(KEY_RIDE_PRICE))
    val price: StateFlow<Double?> = _price.asStateFlow()

    private val _distanceKm = MutableStateFlow(savedStateHandle.get<Double?>(KEY_DISTANCE_KM))
    val distanceKm: StateFlow<Double?> = _distanceKm.asStateFlow()

    private val _durationMin = MutableStateFlow(savedStateHandle.get<Int?>(KEY_DURATION_MIN))
    val durationMin: StateFlow<Int?> = _durationMin.asStateFlow()

    val carTypes = listOf(
        CarType("Mini", 12.0, 60.0),
        CarType("Sedan", 18.0, 100.0),
        CarType("SUV", 25.0, 150.0)
    )

    private val initialCarName = savedStateHandle.get<String?>(KEY_SELECTED_CAR)
    private val _selectedCar = MutableStateFlow(carTypes.firstOrNull { it.name == initialCarName })
    val selectedCar: StateFlow<CarType?> = _selectedCar.asStateFlow()



    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    val userLocation = locationRepository.userLocation

    private val _markers = MutableStateFlow<List<DestinationMarker>>(emptyList())
    val markersInFirebase: StateFlow<List<DestinationMarker>> = _markers.asStateFlow()

    private val _rideState = MutableStateFlow(RideState.IDLE)
    val rideState: StateFlow<RideState> = _rideState.asStateFlow()




    init {
        loadMarkerFromFirebase()
        viewModelScope.launch {
            hasLocationPermission.collect { granted ->
                if (granted) locationRepository.startLocationUpdates() else locationRepository.stopLocationUpdates()
            }
        }
    }



    private fun loadMarkerFromFirebase() {
        viewModelScope.launch {

            try {
                markerRepository.getAllMarkersFromFirestore().collectLatest { newMarkers ->
                    _markers.value = newMarkers
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }


    fun updatePermission(granted: Boolean) {
        _hasLocationPermission.value = granted
    }


    override fun onCleared() {
        locationRepository.stopLocationUpdates()
    }



    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation : StateFlow<LatLng?> = _selectedLocation.asStateFlow()

    fun setSelectedLocation(location : LatLng){
        _selectedLocation.value = location
    }


    fun fetchRoute(
        pickup: LatLng?,
        destination: LatLng,
        waypoints: List<LatLng>
    ) {
        viewModelScope.launch {

            val origin = pickup ?: userLocation.value
            if (origin == null) {
                Log.d("ROUTE_DEBUG", "Origin is not available yet")
                return@launch
            }

            val result = if (waypoints.isEmpty()) {
                directionRepository.getRoute(origin = origin, destination = destination)
            } else {
                directionRepository.getRouteForMultipleMarkers(
                    origin = origin,
                    waypoints = waypoints.take(3),
                    destination = destination
                )
            }
            applyRouteResult(result)
        }

    }

    private fun applyRouteResult(result: RouteResult?) {
        if (result == null) {
            Log.d("ROUTE_DEBUG", "Route is null")
            return
        }
        _routePoints.value = result.points
        _distanceKm.value = result.distanceMeters / 1000.0
        _durationMin.value = result.durationSeconds / 60
        savedStateHandle[KEY_DISTANCE_KM] = _distanceKm.value
        savedStateHandle[KEY_DURATION_MIN] = _durationMin.value
        _selectedCar.value?.let { calculatePriceForCar(it) }
    }







    fun selectCar(car: CarType) {
        _selectedCar.value = car
        savedStateHandle[KEY_SELECTED_CAR] = car.name
        calculatePriceForCar(car)
    }


    private fun calculatePriceForCar(car: CarType) {

        val km = _distanceKm.value ?: return

        val etaMultiplier = ((_durationMin.value ?: 0) * 0.6)
        var total = (km * car.ratePerKm) + etaMultiplier
        if (total < car.minimumFare) total = car.minimumFare

        _price.value = total
        savedStateHandle["ride_price"] = total
    }



    fun clearRoute() {
        _routePoints.value = emptyList()
        _distanceKm.value = null
        _durationMin.value = null
        _price.value = null
        _selectedCar.value = null
        savedStateHandle[KEY_RIDE_PRICE] = null
        savedStateHandle[KEY_DISTANCE_KM] = null
        savedStateHandle[KEY_DURATION_MIN] = null
        savedStateHandle[KEY_SELECTED_CAR] = null
    }


    fun startRideSimulation() {
        viewModelScope.launch {
            _rideState.value = RideState.REQUESTED
            delay(2000)
            _rideState.value = RideState.IN_PROGRESS
            delay(5000)
            _rideState.value = RideState.COMPLETED
        }
    }

    fun resetRide() {
        _rideState.value = RideState.IDLE
    }



}