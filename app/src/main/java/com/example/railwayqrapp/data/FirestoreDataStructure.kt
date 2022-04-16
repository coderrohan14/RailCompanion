package com.example.railwayqrapp.data

import kotlin.random.Random

data class FirestoreDataStructure(
    val data: HashMap<String, HashMap<String, PassengerInfo>> = HashMap()
) {
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return Random.nextInt()
    }
}
