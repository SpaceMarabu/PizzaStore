package com.example.pizzastore.presentation.contacts

sealed class ContactsScreenState {

    data object Initial : ContactsScreenState()

    data object Content : ContactsScreenState()
}
