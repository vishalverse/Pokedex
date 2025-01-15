package com.example.pokedox.DetailScreen

import com.example.pokedox.remote.Pokemon
import com.example.pokedox.repository.PokemonRepository
import com.example.pokedox.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(private val repository: PokemonRepository){
    suspend fun getPokemonInfo(name: String): Resource<Pokemon>{
        return repository.getPokemon(name)
    }
}