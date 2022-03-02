package com.example.android.babyurl

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.babyurl.database.UrlDatabase
import com.example.android.babyurl.network.FeedbackNetwork
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedbackViewModel(application: Application) : AndroidViewModel(application) {

    // Authentication
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    private val _user = MutableLiveData<String?>()
    val user: LiveData<String?> = _user

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val database = UrlDatabase.getInstance(application)

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else AuthenticationState.UNAUTHENTICATED
    }

    init {
        _toastMessage.value = null
    }

    fun logIn() {
        _user.value = FirebaseAuth.getInstance().currentUser?.email
    }

    fun logOut() {
        _user.value = null
    }

    fun sendFeedback(message: String) {
        val feedbackNetwork = FeedbackNetwork(message, _user.value!!)
        try{
            Firebase.firestore.collection("all_feedbacks").document(System.currentTimeMillis().toString()).set(feedbackNetwork)
                .addOnSuccessListener {
                _toastMessage.value = "Feedback sent successfully"
                _toastMessage.value = null
            }.addOnCanceledListener {
                _toastMessage.value = "Feedback cancelled"
                _toastMessage.value = null
            }.addOnFailureListener {
                _toastMessage.value = "Feedback failed"
                _toastMessage.value = null
            }
        }catch(e : Exception){
            Log.i("MainActivity" , "Feedback(sendFeedback) : Failed")
        }
    }
}

class FeedbackViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedbackViewModel::class.java)) {
            return FeedbackViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}