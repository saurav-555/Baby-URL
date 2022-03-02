package com.example.android.babyurl

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.babyurl.database.UrlDatabase
import com.example.android.babyurl.network.UrlNetwork
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddBabyUrlViewModel(application: Application) : AndroidViewModel(application) {

    // Authentication
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    private val _user = MutableLiveData<String?>()
    val user: LiveData<String?> = _user

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage : LiveData<String?> = _toastMessage

    private val database = UrlDatabase.getInstance(application)

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else AuthenticationState.UNAUTHENTICATED
    }
    init {
        _toastMessage.value = null
    }

    fun logIn(){
        _user.value = FirebaseAuth.getInstance().currentUser?.email
    }

    fun logOut(){
        _user.value = null
    }

    fun insertUrl(shortUrl : String , longUrl : String){
        val urlNetwork = UrlNetwork(System.currentTimeMillis() , shortUrl , longUrl)
        val data = hashMapOf( "longUrl" to urlNetwork.longUrl)
            try {
                Firebase.firestore.collection("all_urls").document(urlNetwork.shortUrl).set(data)
                Firebase.firestore.collection("all_users").document(_user.value!!)
                    .collection("urls").document(urlNetwork.rowId.toString()).set(urlNetwork)
                    .addOnSuccessListener {
                        insertIntoDatabase(urlNetwork)
                        _toastMessage.value = "Baby-url added successfully"
                        _toastMessage.value = null
                    }.addOnCanceledListener {
                        _toastMessage.value = "Adding baby-url cancelled"
                        _toastMessage.value = null
                    }.addOnFailureListener {
                        _toastMessage.value = "Adding baby-url failed"
                        _toastMessage.value = null
                    }
            }
            catch(e : Exception){
                Log.i("MainActivity" , "AddBabyUrl(insertUrl) : Failed")
            }
    }

    private fun insertIntoDatabase(urlNetwork : UrlNetwork){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                database.urlDao.insert(urlNetwork.asDatabase())
            }
        }
    }
}

class AddBabyUrlViewModelFactory(
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddBabyUrlViewModel::class.java)) {
            return AddBabyUrlViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
