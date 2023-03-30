package com.kalalau.android_client

import androidx.lifecycle.ViewModel
import com.kal.swapistore.PeopleData
import com.kal.swapistore.SwapiStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.js.ExperimentalJsExport

@ExperimentalJsExport
@DelicateCoroutinesApi
@FlowPreview
class MainViewModel : ViewModel() {
    private val store = SwapiStore()

    private val _peopleData = MutableStateFlow<PeopleData>(PeopleData.Initial())
    val peopleData = _peopleData.asStateFlow()

    private val _displayLoader = MutableStateFlow(false)
    val displayLoader = _displayLoader.asStateFlow()

    private var unsubscribeFromPeople: (() -> Unit)? = null
    private var unsubscribeFromShowLoading: (() -> Unit)? = null

    fun getPeople() {
        store.getPeople()
    }

    init {
        unsubscribeFromPeople = store.subscribePeople {
            _peopleData.value = it
        }
        unsubscribeFromShowLoading = store.subscribeShowLoader {
            _displayLoader.value = it
        }
    }

    fun dispose() {
        unsubscribeFromPeople?.invoke()
        unsubscribeFromShowLoading?.invoke()
    }
}
