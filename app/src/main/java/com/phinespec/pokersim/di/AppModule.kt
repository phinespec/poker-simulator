package com.phinespec.pokersim.di

import com.phinespec.pokersim.data.remote.HandStrengthApi
import com.phinespec.pokersim.data.repository.PokerSimRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHandStrengthApi(): HandStrengthApi {
        return Retrofit.Builder()
            .baseUrl("https://api.pokerapi.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HandStrengthApi::class.java)
    }

    @Provides
    @Singleton
    fun providePokerSimRepository(api: HandStrengthApi): PokerSimRepository {
        return PokerSimRepository(api)
    }
}