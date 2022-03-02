package com.example.android.babyurl

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.babyurl.databinding.FragmentFeedbackBinding

class FeedbackFragment : Fragment() {

    private lateinit var binding: FragmentFeedbackBinding
    private lateinit var viewModel: FeedbackViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val viewModelFactory = FeedbackViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FeedbackViewModel::class.java)

        viewModel.authenticationState.observe(viewLifecycleOwner , Observer { authenticationState->
            if(authenticationState == FeedbackViewModel.AuthenticationState.AUTHENTICATED){
                viewModel.logIn()
            }else{
                viewModel.logOut()
            }
        })
        viewModel.toastMessage.observe(viewLifecycleOwner , Observer { message->
            if(message != null){
                Toast.makeText(application.applicationContext , message , Toast.LENGTH_SHORT).show()
            }
        })

        binding.submitFeedback.setOnClickListener {
            hideKeyboard()
            val message = binding.feedbackInput.text.toString()
            if (message.isEmpty() || message.length > 200) {
                Toast.makeText(application.applicationContext, "Invalid length : 1-200", Toast.LENGTH_SHORT).show()
            } else {
                binding.feedbackInput.text.clear()
                Toast.makeText(application.applicationContext, "Sending feedback...", Toast.LENGTH_SHORT).show()
                viewModel.sendFeedback(message)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        // Check if no view has focus:
        val view = requireActivity().currentFocus
        if (view != null) {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

}