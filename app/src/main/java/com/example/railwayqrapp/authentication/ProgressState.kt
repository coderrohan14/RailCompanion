package com.example.railwayqrapp.authentication

sealed class ProgressState{
    object Loading : ProgressState()
    data class Error(var msg: String? = null) : ProgressState()
    object Success : ProgressState()

    override fun toString(): String {
        return when(this){
            is Loading ->{
                ""
            }
            is Error ->{
                msg.toString()
            }
            is Success ->{
                ""
            }
        }
    }
}
