package com.example.android.babyurl

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.babyurl.databinding.FragmentAddBabyUrlBinding

class AddBabyUrlFragment : Fragment() {
    private lateinit var binding: FragmentAddBabyUrlBinding
    private lateinit var viewModel: AddBabyUrlViewModel

    companion object {
        private val map: String = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private val p: Long = 65536
        private val p1: ArrayList<Long> = ArrayList()
        private val p2: ArrayList<Long> = ArrayList()
        private val maxn: Long = 1000
        private val mod1: Long = 1000000000 + 7
        private val mod2: Long = 1000000000 + 97
    }

    init {
        var ith = 1
        p1.add(1)
        p2.add(1)
        while (ith < maxn) {
            p1.add((p1.last() * p) % mod1)
            p2.add((p2.last() * p) % mod2)
            ith++
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_baby_url,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val viewModelFactory = AddBabyUrlViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddBabyUrlViewModel::class.java)


        viewModel.authenticationState.observe(viewLifecycleOwner , Observer { authenticationState->
            if(authenticationState == AddBabyUrlViewModel.AuthenticationState.AUTHENTICATED){
                viewModel.logIn()
            }else{
                viewModel.logOut()
            }
        })
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer { message->
            if(message != null){
                Toast.makeText(application.applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        })

        binding.generateBabyUrl.setOnClickListener {
            hideKeyboard()
            val url = binding.givenUrl.text.toString()

            if (url.isEmpty() || url.length > maxn) {
                Toast.makeText(application.applicationContext, "Invalid length : 1-1000", Toast.LENGTH_SHORT).show()
            } else {
                val shortUrl = generateBabyUrl(url)
                binding.givenUrl.text.clear()
                Toast.makeText(application.applicationContext, "Adding baby-url...", Toast.LENGTH_SHORT).show()
                viewModel.insertUrl(shortUrl , url)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun generateBabyUrl(url: String): String {
        var ith = 0
        var hash1: Long = 0
        var hash2: Long = 0
        while (ith < url.length) {
            hash1 += (url[ith].toLong() * p1[ith]) % mod1
            hash2 += (url[ith].toLong() * p2[ith]) % mod2
            hash1 %= mod1
            hash2 %= mod2
            ith++
        }
        var hash: Long = hash2 * mod2 + hash1
        var babyUrl = ""
        while (hash > 0) {
            babyUrl += map[(hash % 62).toInt()]
            hash /= 62
        }
        return babyUrl
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