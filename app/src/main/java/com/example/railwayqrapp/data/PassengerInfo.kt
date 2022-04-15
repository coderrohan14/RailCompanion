package com.example.railwayqrapp.data

data class PassengerInfo(
    val firstName: String = "",
    val lastName: String = "",
    val passengerId: String = "",
    val PNR: String = "",
    val age: String = "",
    val from: String = "",
    val to: String = "",
    val verified: Boolean = false,
    val coach: String = "",
    val seatNumber: String = ""
)
