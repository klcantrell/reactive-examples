package com.kal.swapistore

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.random.Random

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

@ExperimentalJsExport
@JsExport
class PeopleList internal constructor(private val value: List<StarWarsPerson>) :
    List<StarWarsPerson> by value {

    fun toArray() = value.toTypedArray()

    companion object {
        fun fromArray(arrayValue: Array<StarWarsPerson>): PeopleList {
            return PeopleList(arrayValue.toList())
        }
    }
}

@ExperimentalJsExport
@JsExport
@Serializable
data class StarWarsPerson(val name: String)

@ExperimentalJsExport
@Serializable
internal data class PeopleResponse(val results: List<StarWarsPerson>)

@ExperimentalJsExport
@JsExport
sealed class PeopleData {
    class Initial : PeopleData()
    class Loading : PeopleData()
    class Fetching(val data: PeopleList) : PeopleData()
    class Loaded(val data: PeopleList) : PeopleData()
    class Error : PeopleData()
}

@DelicateCoroutinesApi
@FlowPreview
@ExperimentalJsExport
@JsExport
class SwapiStore {
    private val peopleData = MutableStateFlow<PeopleData>(PeopleData.Initial())
    private val displayLoader = MutableStateFlow(false)
    private val getPeople = MutableSharedFlow<Unit>()

    private var peopleSubscriptions = mutableSetOf<Job>()
    private var showLoaderSubscriptions = mutableSetOf<Job>()

    private val httpClient = httpClient {
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
        GlobalScope.launch {
            getPeople.emit(Unit)
        }
    }

    fun subscribePeople(subscriptionFunction: (PeopleData) -> Unit): () -> Unit {
        val subscription = GlobalScope.launch {
            peopleData.collect {
                subscriptionFunction(it)
            }
        }
        peopleSubscriptions.add(subscription)

        return { unsubscribePeople(subscription) }
    }

    private fun unsubscribePeople(subscription: Job) {
        peopleSubscriptions.find { it == subscription }?.cancel()
    }

    fun subscribeShowLoader(subscriptionFunction: (Boolean) -> Unit): () -> Unit {
        val subscription = GlobalScope.launch {
            displayLoader.collect {
                subscriptionFunction(it)
            }
        }
        showLoaderSubscriptions.add(subscription)

        return { unsubscribeShowLoader(subscription) }
    }

    private fun unsubscribeShowLoader(subscription: Job) {
        showLoaderSubscriptions.find { it == subscription }?.cancel()
    }

    init {
        GlobalScope.launch {
            collectPeopleData()
        }
        GlobalScope.launch {
            collectLoadingState()
        }
    }

    private suspend fun collectPeopleData() {
        getPeople.debounce(300).onEach {
            when (val currentData = peopleData.value) {
                is PeopleData.Initial, PeopleData.Error() -> peopleData.value =
                    PeopleData.Loading()

                is PeopleData.Loaded -> peopleData.value =
                    PeopleData.Fetching(data = currentData.data)

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
                peopleData.value = PeopleData.Loaded(data = PeopleList(response.results))
            } catch (cause: Throwable) {
                if (cause is CancellationException) {
                    return@collectLatest
                }
                peopleData.value = PeopleData.Error()
            }
        }
    }

    private suspend fun collectLoadingState() {
        peopleData.collectLatest {
            if (it is PeopleData.Loading) {
                delay(500)
                displayLoader.value = true
            } else {
                displayLoader.value = false
            }
        }

    }
}
