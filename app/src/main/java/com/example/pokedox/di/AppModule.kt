package com.example.pokedox.di

import androidx.compose.animation.core.rememberTransition
import com.example.pokedox.retrofit.PokeApi
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    fun providesApi(): Retrofit{
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("https://pokeapi.co/api/v2/").build()
    }
    fun providesPokemonApi(retrofit: Retrofit): PokeApi {
        return retrofit.create(PokeApi::class.java)
    }

}