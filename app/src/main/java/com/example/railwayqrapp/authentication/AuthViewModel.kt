package com.example.railwayqrapp.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.railwayqrapp.data.PassengerInfo
import com.example.railwayqrapp.data.TrainInfo
import com.example.railwayqrapp.data.User
import com.example.railwayqrapp.indiaCityList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import java.util.UUID

class AuthViewModel : ViewModel() {

    private val auth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }

    init{
        val settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        db.firestoreSettings = settings
    }

    private val _resetPasswordState: MutableStateFlow<ProgressState?> = MutableStateFlow(null)
    val resetPasswordState: StateFlow<ProgressState?> get() = _resetPasswordState

    private val _signInState: MutableStateFlow<ProgressState?> = MutableStateFlow(null)
    val signInState: StateFlow<ProgressState?> get() = _signInState

    private val _signUpState: MutableStateFlow<ProgressState?> = MutableStateFlow(null)
    val signUpState: StateFlow<ProgressState?> get() = _signUpState

    private val _saveUserState: MutableStateFlow<ProgressState?> = MutableStateFlow(null)
    val saveUserState: StateFlow<ProgressState?> get() = _saveUserState

    private val _userData: MutableStateFlow<User?> = MutableStateFlow(null)
    val userData: StateFlow<User?> get() = _userData

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun logOut() = run { FirebaseAuth.getInstance().signOut() }

    fun saveUserToDB(
        user: User
    ) {
        try {
            _saveUserState.value = ProgressState.Loading
            val userRef = db.collection("users").document(user.userId)
            userRef.set(user).addOnCompleteListener { task ->
                Log.d("requestTest", "on Complete Listeners!!")
                if (task.isSuccessful) {
                    saveTrainInfo(user)
                } else {
                    _saveUserState.value = ProgressState.Error(task.exception.toString())
                }
            }
        } catch (e: Exception) {
            _saveUserState.value = ProgressState.Error(e.message.toString())
        }
    }

    private fun saveTrainInfo(
        user: User
    ){
        try{
            val trainInfoRef = db.collection("train_info").document(user.userId)
            val trainInfo = getRandomTrainInfo((1..10000000).random())
            trainInfoRef.set(trainInfo).addOnCompleteListener { task->
                if(task.isSuccessful){
                    Log.d("TrainInfoTest", "Train info saved!!!")
                    _saveUserState.value = ProgressState.Success
                }else{
                    _saveUserState.value = ProgressState.Error(task.exception.toString())
                }
            }
        }catch(e: Exception){
            _saveUserState.value = ProgressState.Error(e.message.toString())
        }
    }

    fun getUserFromFirebase(){
        getCurrentUser()?.let{ currentUser->
            val docRef = db.collection("users").document(currentUser.uid)
            docRef.get().addOnSuccessListener { docSnapshot->
                _userData.value = docSnapshot.toObject()
            }
        }
    }

    fun signInUser(
        email: String,
        password: String
    ) {
        try {
            _signInState.value = ProgressState.Loading
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                Log.d("requestTest", "on Complete Listeners!!")
                if (task.isSuccessful) {
                    _signInState.value = ProgressState.Success
                } else {
                    _signInState.value = ProgressState.Error(task.exception.toString())
                }
            }
        } catch (e: Exception) {
            _signInState.value = ProgressState.Error(e.message.toString())
        }
    }

    fun sendPasswordResetMail(
        email: String
    ) {
        try {
            _resetPasswordState.value = ProgressState.Loading
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                Log.d("requestTest", "on Complete Listeners!!")
                if (task.isSuccessful) {
                    _resetPasswordState.value = ProgressState.Success
                } else {
                    _resetPasswordState.value = ProgressState.Error(task.exception.toString())
                }
            }
        } catch (e: Exception) {
            _resetPasswordState.value = ProgressState.Error(e.message.toString())
        }
    }

    fun signUpUser(
        email: String,
        password: String
    ) {
        try {
            _signUpState.value = ProgressState.Loading
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                Log.d("requestTest", "on Complete Listeners!!")
                if (task.isSuccessful) {
                    _signUpState.value = ProgressState.Success
                } else {
                    _signUpState.value = ProgressState.Error(task.exception.toString())
                }
            }
        } catch (e: Exception) {
            _signUpState.value = ProgressState.Error(e.message.toString())
        }
    }

    private fun getRandomTrainInfo(num: Int): TrainInfo{
        val tl = listOf("AM","PM")
        val trainName = "Train_No_$num"
        val depTime = "${(1..12).random()}:${(0..59).random()} ${tl[(0..1).random()]}"
        val arrTime = "${(1..12).random()}:${(0..59).random()} ${tl[(0..1).random()]}"
        val trainId = UUID.randomUUID().toString().uppercase(Locale.getDefault())
        return TrainInfo(trainId,trainName, depTime,arrTime)
    }

    fun addPassengersData(){
        var cnt=1
        for(coach in 'A'..'J'){
            for(seatNo in 1..10){
                val passengerInfo = getRandomPassengerInfo(cnt++,coach.toString(),seatNo.toString())
                val seatRef = db.collection("passengersInfo").document(passengerInfo.passengerId)
                try{
                    seatRef.set(passengerInfo).addOnCompleteListener { task->
                        if(!task.isSuccessful){
                            Log.d("PassengerAddingError", "Exception -> ${task.exception.toString()}")
                        }
                    }
                }catch (e: Exception){
                    Log.d("PassengerAddingError", e.message.toString())
                }
            }
        }
    }

    private fun getRandomPassengerInfo(num: Int,coach: String, seat: String): PassengerInfo{
        val firstName = "First_Name_$num"
        val lastName = "Last_Name_$num"
        val PNR = (1e9.toLong()..1e17.toLong()).random().toString()
        val passengerId = "${getRandomChar()}${getRandomChar()}${getRandomChar()}${(1e9.toLong()..1e17.toLong()).random()}"
        val age = (1..100).random().toString()
        val size = indiaCityList.size
        val from = indiaCityList[(0 until size).random()]
        val to = indiaCityList[(0 until size).random()]
        return PassengerInfo(
            firstName = firstName,
            lastName = lastName,
            passengerId = passengerId,
            PNR = PNR,
            age = age,
            from = from,
            to = to,
            verified = false,
            coach = coach,
            seatNumber = seat
        )
    }

    private fun getRandomChar() = ('A'..'Z').random()
}