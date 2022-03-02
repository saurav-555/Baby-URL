package com.example.android.babyurl

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.babyurl.database.UrlDatabase
import com.example.android.babyurl.database.asHome
import com.example.android.babyurl.network.UrlNetwork
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Authentication
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }
    enum class Session {
        STOPPED , RUNNING
    }
    var _session : Session = Session.STOPPED


    private val _user = MutableLiveData<String?>()
    val user: LiveData<String?> = _user

    private val database = UrlDatabase.getInstance(application)

    private val _doneRefreshing = MutableLiveData<Boolean>()
    val doneRefreshing : LiveData<Boolean> = _doneRefreshing

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage : LiveData<String?> = _toastMessage


    val babyUrls = Transformations.map(database.urlDao.getAllUrls()){
        it.asHome()
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else AuthenticationState.UNAUTHENTICATED
    }

    init {
        _toastMessage.value = null
        _doneRefreshing.value = true
    }

    fun logIn(){
        _user.value = FirebaseAuth.getInstance().currentUser?.email
    }
    fun logOut(){
        _user.value = null
    }

    fun deleteUrl(rowId: Long){
        try {
            Firebase.firestore.collection("all_users").document(_user.value!!).collection("urls").document(rowId.toString())
                .delete().addOnSuccessListener {
                    deleteFromDatabase(rowId)
                    _toastMessage.value = "Deleted baby-url successfully"
                    _toastMessage.value = null
                }.addOnCanceledListener {
                    _toastMessage.value = "Deletion cancelled"
                    _toastMessage.value = null
                }.addOnFailureListener{
                    _toastMessage.value = "Deleted failed"
                    _toastMessage.value = null
                }
        }catch (e : Exception) {
            Log.i("MainActivity" , "Home(deleteUrl) : Failed")
        }
    }

    fun clearAll(){
            try {
                Firebase.firestore.collection("all_users").document(_user.value!!).collection("urls").get()
                    .addOnSuccessListener { result->
                        for(document in result){
                            val id = document.reference.id.toLong()
                            document.reference.delete()
                                .addOnSuccessListener {
                                    deleteFromDatabase(id)
                                }
                        }
                    }
            }catch (e : Exception){
                Log.i("MainActivity" , "Home(clearAll) : Failed")
            }
    }

    fun refresh(){
        _doneRefreshing.value = false
        viewModelScope.launch{
            try{
                withContext(Dispatchers.IO){
                    database.urlDao.deleteAll()
                }
                Firebase.firestore.collection("all_users").document(_user.value!!).collection("urls")
                    .get().addOnSuccessListener { result->
                        for(document in result){
                            val urlNetwork = document.toObject(UrlNetwork::class.java)
                            insertIntoDatabase(urlNetwork)
                        }
                    }
            }catch (e : Exception){
                Log.i("MainActivity" , "Home(refresh) : Failed")
            }finally {
                withContext(Dispatchers.Main){
                    _doneRefreshing.value = true
                }
            }
        }
    }

    private fun insertIntoDatabase(urlNetwork: UrlNetwork){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                database.urlDao.insert(urlNetwork.asDatabase())
            }
        }
    }
    private fun deleteFromDatabase(rowId : Long){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                database.urlDao.delete(rowId)
            }
        }
    }
    fun deleteAllFromDatabase(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                database.urlDao.deleteAll()
            }
        }
    }
}

class HomeViewModelFactory(
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}