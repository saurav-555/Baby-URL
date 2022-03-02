package com.example.android.babyurl

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.babyurl.databinding.FragmentSplashBinding
import com.firebase.ui.auth.AuthUI

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel by viewModels<LoginViewModel>()

    private val signInRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            Log.v("SplashFragment", "Login Successfully")
        }else{
            Log.v("SplashFragment", "Login unsuccessful")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            if (authenticationState == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                binding.loginButton.visibility = View.GONE
                launchHome()
            } else {
                binding.loginButton.visibility = View.VISIBLE
            }
        })

        binding.loginButton.setOnClickListener {
            launchSignInFlow()
        }

    }

    private fun launchSignInFlow(){
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        signInRequest.launch(
            AuthUI
                .getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.loginTheme).build()
        )
    }
    private fun launchHome(){
        (activity as SplashScreenActivity?)?.launchHomeActivity()
    }

}