package com.example.pizzastore.presentation.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsScreenViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow<ContactsScreenState>(ContactsScreenState.Initial)
    val state = _state.asStateFlow()

    init {
        initScreen()
    }

    private fun initScreen() {
        viewModelScope.launch {
            _state.emit(ContactsScreenState.Content)
        }
    }
}