package com.example.pokedox.repository

import com.example.pokedox.PokemonList.PokemonList
import com.example.pokedox.remote.Pokemon
import com.example.pokedox.retrofit.PokeApi
import com.example.pokedox.utils.Resource
import javax.inject.Inject

class PokemonRepository @Inject constructor(private val pokeApi: PokeApi) {
    suspend fun getPokemonList(limit: Int,offset:Int): Resource<PokemonList>{
        val response = try{
            pokeApi.getPokemonList(
                limit = limit,
                offset = offset
            )
        }catch (e: Exception){
            return Resource.Error(message="An unknown error occurred.")
        }

        return Resource.Success(response)
    }
    suspend fun getPokemon(name: String): Resource<Pokemon>{
        val response = try{
            pokeApi.getPokemonInfo(
                name = name
            )
        }catch (e: Exception){
            return Resource.Error(message="An unknown error occurred.")
        }

        return Resource.Success(response)
    }


}