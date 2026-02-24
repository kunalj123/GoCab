package com.example.gocab.repositories

import com.example.gocab.utilities.DestinationMarker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MarkerRepositoryImpl @Inject constructor(
    private val firebase: FirebaseFirestore
) : MarkerRepository {

    override suspend fun addMarker(marker: LatLng, name: String) {


        try {
            val data = hashMapOf(
                "latitude" to marker.latitude,
                "longitude" to marker.longitude,
                "name" to name
            )

            firebase.collection("markers")
                .document(name)
                .set(data)
                .await()
        } catch (e: Exception) {
        }
    }

    override  fun getAllMarkersFromFirestore(): Flow<List<DestinationMarker>> = callbackFlow{

       val listener = firebase.collection("markers")
           .addSnapshotListener{snapshot , error ->



               if(error != null){
                   error.printStackTrace()
                   close(error)
                   return@addSnapshotListener
               }

               if(snapshot != null ){

                   val markers = snapshot.documents.map { marker ->
                       DestinationMarker(
                           id = marker.id,
                           latitude =  marker.getDouble("latitude")?: 0.0,
                           longitude =  marker.getDouble("longitude")?: 0.0,
                           name =  marker.getString("name")?: ""

                       )
                   }

                   trySend(markers).isSuccess
               }else{
                   trySend(emptyList())
               }
           }

        awaitClose { listener.remove() }

    }
}