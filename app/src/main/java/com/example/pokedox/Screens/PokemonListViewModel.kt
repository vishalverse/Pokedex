package com.example.pokedox.Screens

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.graphics.Palette
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedox.PokemonList.PokedexListEntry
import com.example.pokedox.repository.PokemonRepository
import com.example.pokedox.utils.Constants.PAGE_SIZE
import com.example.pokedox.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    private var currPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var cachedPokemonList = listOf<PokedexListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun searchPokemonList(query: String){
        Log.d("Search", "Inside searchPokemonList")
        var listToSearch = if(isSearchStarting) {
            pokemonList.value
        } else {
            cachedPokemonList
        }

        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty()) {
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
            if (isSearchStarting) {
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }

    fun loadPokemonPaginated(){
        viewModelScope.launch {
            isLoading.value = true

            val result = repository.getPokemonList(
                limit = PAGE_SIZE,
                offset = currPage * PAGE_SIZE
            )

            when(result){
                is Resource.Success -> {
                    endReached.value = (PAGE_SIZE * currPage) >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if(entry.url.endsWith("/")){
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        }
                        else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val imageURL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"

                        PokedexListEntry(
                            pokemonName = entry.name.capitalize(Locale.ROOT),
                            number = number.toInt(),
                            imageURL = imageURL
                        )
                    }

                    currPage++
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }

                else -> {Log.d("error real one","here Vishal Sharma")}
            }
        }
    }

    fun calcDominantColor(
        drawable: Drawable,
        onFinished: (Color) -> Unit
    ){
        val bmp = (drawable as BitmapDrawable).bitmap.copy(
            Bitmap.Config.ARGB_8888,
            true
        )

        Palette.from(bmp).generate{ palette->
            palette?.dominantSwatch?.rgb?.let { colorValue->
                onFinished(Color(colorValue))
            }
        }
    }
}