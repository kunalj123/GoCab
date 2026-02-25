package com.example.gocab.di

import android.content.Context
import com.example.gocab.repositories.LocationRepository
import com.example.gocab.repositories.LocationRepositoryImpl
import com.example.gocab.repositories.MarkerRepository
import com.example.gocab.repositories.MarkerRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent ::class )
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore() : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideMarkerRepository(
        firestore : FirebaseFirestore
    ): MarkerRepository = MarkerRepositoryImpl(firestore)



}