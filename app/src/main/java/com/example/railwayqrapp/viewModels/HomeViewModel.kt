package com.example.railwayqrapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.railwayqrapp.authentication.ProgressState
import com.example.railwayqrapp.data.PassengerInfo
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val db by lazy { Firebase.firestore }

    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
        .build()

    fun initializeDBSettings() = run { db.firestoreSettings = settings }

    private val _passengersState: MutableStateFlow<HashMap<String, HashMap<String, PassengerInfo>>> =
        MutableStateFlow(HashMap())
    val passengersState: StateFlow<HashMap<String, HashMap<String, PassengerInfo>>> get() = _passengersState

    private val _passengerVerificationState: MutableStateFlow<ProgressState?> =
        MutableStateFlow(null)
    val passengerVerificationState: StateFlow<ProgressState?> get() = _passengerVerificationState

    private val _passengerUpdateState: MutableStateFlow<PassengerInfo?> = MutableStateFlow(null)

    fun resetProgressState() = run { _passengerVerificationState.value = null }

    private lateinit var trainRefListener: ListenerRegistration

    fun addPassengerDataListener() {
        Log.d("HomeViewModelPassengerData", "HERE START!")
        trainRefListener = db.collection("passengersInfo")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.d("PassengerSnapshotError", "Listen failed.")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    for (dc in snapshot.documentChanges) {
                        val passengerInfoMap = dc.document.data
                        val passenger = getPassengerFromMap(passengerInfoMap)
                        Log.d(
                            "HomeViewModelPassengerData",
                            "Passenger -> ${passenger.coach}, ${passenger.seatNumber}"
                        )
                        if ((dc.type == DocumentChange.Type.ADDED) || (dc.type == DocumentChange.Type.MODIFIED)) {
                            if (snapshot.metadata.isFromCache) {
                                // local data change
                                _passengerUpdateState.value?.let { localPassenger ->
                                    if (localPassenger.passengerId == passenger.passengerId && _passengerVerificationState.value == ProgressState.Loading) {
                                        _passengerVerificationState.value = ProgressState.Success
                                        _passengerUpdateState.value = null
                                    }
                                }
                            }
                            if (_passengersState.value.containsKey(passenger.coach)) {
                                if (_passengersState.value[passenger.coach]!!.containsKey(passenger.seatNumber)) {
                                    _passengersState.value[passenger.coach]!![passenger.seatNumber] =
                                        passenger
                                } else {
                                    _passengersState.value[passenger.coach]!![passenger.seatNumber] =
                                        passenger
                                }
                            } else {
                                val hm = hashMapOf<String, PassengerInfo>()
                                hm[passenger.seatNumber] = passenger
                                _passengersState.value[passenger.coach] = hm
                            }
                        } else {
                            _passengersState.value[passenger.coach]?.remove(passenger.seatNumber)
                        }
                    }
                } else {
                    Log.d("PassengerSnapshotError", "Snapshot is null")
                }
            }
    }

    fun verifyPassengerOnDB(passenger: PassengerInfo) {
        _passengerUpdateState.value = passenger
        _passengerVerificationState.value = ProgressState.Loading
        try {
            val passengerRef = db.collection("passengersInfo").document(passenger.passengerId)
            passengerRef.update("verified", true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("PassUpdateState", "Passenger Verified!!")
                    _passengerVerificationState.value = ProgressState.Success
                } else {
                    _passengerVerificationState.value =
                        ProgressState.Error(task.exception.toString())
                }
            }
        } catch (e: Exception) {
            _passengerVerificationState.value = ProgressState.Error(e.message.toString())
        }
    }

    private fun getPassengerFromMap(passengerInfoMap: MutableMap<String, Any>): PassengerInfo {
        val firstName = passengerInfoMap["firstName"].toString()
        val lastName = passengerInfoMap["lastName"].toString()
        val age = passengerInfoMap["age"].toString()
        val coach = passengerInfoMap["coach"].toString()
        val from = passengerInfoMap["from"].toString()
        val to = passengerInfoMap["to"].toString()
        val passengerId = passengerInfoMap["passengerId"].toString()
        val pnr = passengerInfoMap["pnr"].toString()
        val seatNumber = passengerInfoMap["seatNumber"].toString()
        val verified = passengerInfoMap["verified"] as Boolean
        return PassengerInfo(
            firstName = firstName,
            lastName = lastName,
            age = age,
            coach = coach,
            from = from,
            to = to,
            passengerId = passengerId,
            PNR = pnr,
            seatNumber = seatNumber,
            verified = verified
        )
    }

    override fun onCleared() {
        trainRefListener.remove()
        super.onCleared()
    }
}