package com.example.gocab.di

import com.example.gocab.module.AppApiKey
import com.example.gocab.repositories.DirectionRepository
import com.example.gocab.repositories.DirectionRepositoryImpl
import com.example.gocab.retrofit.DirectionServices
import com.example.gocab.retrofit.RetrofitProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DirectionModule {



    @Provides
    @Singleton
    fun provideDirectionsService(): DirectionServices{
        return RetrofitProvider.createDirectionService(
            baseUrl = "https://maps.googleapis.com/maps/api/"
        )
    }

    @Provides
    @Named("mapApiKey")
    fun provideApikey() : String{
        return AppApiKey.KEY_ID
    }

    @Provides
    @Singleton
    fun provideDirectionServices(directionService : DirectionServices,
                                 @Named("mapApiKey")apikey : String): DirectionRepository {
       return DirectionRepositoryImpl(apikey,directionService)
    }
}