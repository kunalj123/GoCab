package com.example.gocab.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlin.let


@HiltViewModel
class MapViewModel @Inject constructor(
    private val markerRepository : MarkerRepository,
    private val locationRepository : LocationRepository,
    private val directionRepository : DirectionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){


    fun addMarker(
        marker : LatLng,
        name : String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        viewModelScope.launch {
            try{
                markerRepository.addMarker(marker, name)
                onSuccess()
            }catch(e:Exception){
                onFailure(e)
            }
        }
    }

    init {
        loadMarkerFromFirebase()
    }



    private val _markers = MutableStateFlow<List<DestinationMarker>>(emptyList())
    val markersInFirebase : StateFlow<List<DestinationMarker>> = _markers.asStateFlow()




    fun loadMarkerFromFirebase(){

        viewModelScope.launch {

            try{

                markerRepository.getAllMarkersFromFirestore()
                    .collectLatest{ newMarkers ->
                    _markers.value = newMarkers
                }


            }catch(e : Exception){
                e.printStackTrace()
            }
        }
    }



    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission : StateFlow<Boolean> = _hasLocationPermission.asStateFlow()



    val userLocation = locationRepository.userLocation


    init {




        viewModelScope.launch {

            hasLocationPermission.collect { granted ->
                if(granted){
                    locationRepository.startLocationUpdates()
                }else{
                    locationRepository.stopLocationUpdates()
                }

            }
        }
    }


    fun updatePermission(granted : Boolean)  {
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


    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints : StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private val _distanceKm = MutableStateFlow<Double?>(null)
    val distanceKm : StateFlow<Double?> = _distanceKm.asStateFlow()


    fun fetchRouteFromCurrentPositionToMarker(marker : LatLng){


        viewModelScope.launch {

            val currentLocation = locationRepository.userLocation.value

            if (currentLocation == null) {
                Log.d("ROUTE_DEBUG", "Location not ready yet!")
                return@launch
            }

            Log.d("ROUTE_DEBUG", "Fetching route...")

            val result = directionRepository.getRoute(
                origin = currentLocation,
                destination = marker
            )

            Log.d("ROUTE_DEBUG", "Route size = ${result?.points}")

           if(result == null){
               Log.d("ROUTE_DEBUG", "Route is null")
               return@launch
           }

            _routePoints.value = result.points

            val km = result.distanceMeters / 1000.0
            _distanceKm.value = km

            _selectedCar.value?.let {
                calculatePriceForCar(it)
            }
        }

    }

    fun fetchRouteForMultipleMarkers(
        clientOrigin : LatLng,
        waypoints : List<LatLng>,
        clientDestination : LatLng
    ){

        viewModelScope.launch {
           _routePoints.value =  directionRepository.getRouteForMultipleMarkers(
                origin = clientOrigin,
                waypoints = waypoints,
                destination = clientDestination
            ) ?: emptyList()
        }
    }






    private val _price = MutableStateFlow<Double?>(null)
    val price : StateFlow<Double?>  = _price.asStateFlow()

    val carTypes = listOf(
        CarType("Mini", 12.0, 60.0),
        CarType("Prime", 18.0, 100.0),
        CarType("SUV", 25.0, 150.0)
    )

    private val _selectedCar = MutableStateFlow<CarType?>(null)
    val selectedCar: StateFlow<CarType?> = _selectedCar

    fun selectCar(car: CarType) {
        _selectedCar.value = car
        calculatePriceForCar(car)
    }

    init {
        val savedPrice: Double? = savedStateHandle["ride_price"]
        if (savedPrice != null) {
            _price.value = savedPrice
        }
    }



    private fun calculatePriceForCar(car: CarType) {

        val km = _distanceKm.value ?: return

        if (km == null) {
            Log.d("PRICE_DEBUG", "Distance not ready yet")
            return
        }

        var total = km * car.ratePerKm

        if (total < car.minimumFare) {
            total = car.minimumFare
        }

        Log.d("PRICE_DEBUG", "Distance = ${_distanceKm.value}")
        Log.d("PRICE_DEBUG", "Selected car = $car")
        Log.d("PRICE_DEBUG", "Price calculated = $total")

        _price.value = total
        savedStateHandle["ride_price"] = total
    }



    fun clearRoute(){
        _routePoints.value = emptyList()
        _distanceKm.value = null
        _price.value = null
        _selectedCar.value = null
    }


    private val _rideState = MutableStateFlow(RideState.IDLE)
    val rideState : StateFlow<RideState> = _rideState


    fun startRideSimulation(){

        viewModelScope.launch{
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