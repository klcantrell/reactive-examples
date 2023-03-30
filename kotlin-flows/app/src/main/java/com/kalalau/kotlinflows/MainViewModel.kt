package com.kalalau.kotlinflows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

@Serializable
data class StarWarsPerson(val name: String)

@Serializable
data class PeopleResponse(val results: List<StarWarsPerson>)

sealed class PeopleData {
    class Initial : PeopleData()
    class Loading : PeopleData()
    class Fetching(val data: List<StarWarsPerson>) : PeopleData()
    class Loaded(val data: List<StarWarsPerson>) : PeopleData()
    class Error : PeopleData()
}


@OptIn(FlowPreview::class)
class MainViewModel : ViewModel() {
    private val _peopleData = MutableStateFlow<PeopleData>(PeopleData.Initial())
    val peopleData = _peopleData.asStateFlow()

    private val _displayLoader = MutableStateFlow(false)
    val displayLoader = _displayLoader.asStateFlow()

    private val getPeople = MutableSharedFlow<Unit>()

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
        }
    }

    fun getPeople() {
        viewModelScope.launch {
            getPeople.emit(Unit)
        }
    }

    init {
        viewModelScope.launch {
            collectPeopleData()
        }
        viewModelScope.launch {
            collectLoadingState()
        }
    }

    private suspend fun collectPeopleData() {
        getPeople.debounce(300).onEach {
            when (val currentData = peopleData.value) {
                is PeopleData.Initial ->
                    _peopleData.value = PeopleData.Loading()
                is PeopleData.Error ->
                    _peopleData.value = PeopleData.Loading()
                is PeopleData.Loaded ->
                    _peopleData.value = PeopleData.Fetching(data = currentData.data)
                else -> Unit
            }
        }.collectLatest {
            try {
                val response: PeopleResponse =
                    httpClient.get("https://swapi.dev/api/people?page=${Random.nextInt(1, 6)}") {
                        retry {
                            delayMillis { it * 1000L }
                        }
                    }.body()
                _peopleData.value = PeopleData.Loaded(data = response.results)
            } catch (cause: Throwable) {
                if (cause is CancellationException) {
                    return@collectLatest
                }
                _peopleData.value = PeopleData.Error()
            }
        }
    }

    private suspend fun collectLoadingState() {
        _peopleData.collectLatest {
            if (it is PeopleData.Loading) {
                delay(500)
                _displayLoader.value = true
            } else {
                _displayLoader.value = false
            }
        }
    }
}
