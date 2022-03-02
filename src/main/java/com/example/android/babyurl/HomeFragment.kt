package com.example.android.babyurl

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.babyurl.databinding.FragmentHomeBinding


class HomeFragment : Fragment() , AllClearInterface{
    private lateinit var  binding : FragmentHomeBinding
    private lateinit var viewModel : HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val viewModelFactory = HomeViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        (activity as MainActivity).setListener(this)

        viewModel.authenticationState.observe(viewLifecycleOwner , Observer { authenticationState->
            if(authenticationState == HomeViewModel.AuthenticationState.AUTHENTICATED){
                if(viewModel._session == HomeViewModel.Session.STOPPED){
                    viewModel.logIn()
                    viewModel._session = HomeViewModel.Session.RUNNING
                    (activity as MainActivity).binding.navView.getHeaderView(0).findViewById<TextView>(R.id.userEmailId).text = viewModel.user.value
                }
            }else{
                viewModel.logOut()
                viewModel._session = HomeViewModel.Session.STOPPED
                viewModel.deleteAllFromDatabase()
                launchSplashActivity()
            }
        })

        viewModel.doneRefreshing.observe(viewLifecycleOwner, Observer { doneRefreshing ->
            if (doneRefreshing) {
                setHasOptionsMenu(true)
            } else {
                setHasOptionsMenu(false)
            }
        })


        val navController = findNavController()
        binding.addBabyUrl.setOnClickListener{
            navController.navigate(R.id.action_homeFragment_to_addBabyUrlFragment)
        }

        val manager = LinearLayoutManager(activity)
        binding.babyUrlList.layoutManager = manager
        val adapter = BabyUrlAdapter(BabyUrlListener({ shortUrl ->
            try{
                val clipboard : ClipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData : ClipData = ClipData.newPlainText(label.toString(), shortUrl)
                clipboard.setPrimaryClip(clipData)
                Toast.makeText(application.applicationContext, "Baby-url copied to clipboard", Toast.LENGTH_SHORT).show()
            }catch (e : Exception){
                Toast.makeText(application.applicationContext, "Baby-url copy failed", Toast.LENGTH_SHORT).show()
            }
        }, { rowId ->
            Toast.makeText(application.applicationContext, "Deleting baby-url...", Toast.LENGTH_SHORT).show()
            viewModel.deleteUrl(rowId)
        }))
        binding.babyUrlList.adapter = adapter

        viewModel.babyUrls.observe(viewLifecycleOwner , Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.toastMessage.observe(viewLifecycleOwner , Observer { message->
            if(message != null){
                Toast.makeText(application.applicationContext, message , Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.refreshIcon){
            Toast.makeText(context , "Refreshing..." , Toast.LENGTH_SHORT).show()
            viewModel.refresh()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun allClear() {
        viewModel.clearAll()
    }

    @Suppress("DEPRECATION")
    private fun launchSplashActivity(){
        Handler().postDelayed({
            val intent = Intent(activity, SplashScreenActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } , 500)
    }
}