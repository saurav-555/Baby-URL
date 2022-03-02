package com.example.android.babyurl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.babyurl.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI

class MainActivity : AppCompatActivity() {

    private  lateinit var drawerLayout: DrawerLayout

    lateinit var binding : ActivityMainBinding

    private lateinit var listener : AllClearInterface

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        val navController = this.findNavController(R.id.myNavHostFragment)

        drawerLayout = binding.drawerLayout

        NavigationUI.setupActionBarWithNavController(this, navController , drawerLayout)

        navController.addOnDestinationChangedListener{ _: NavController, nd : NavDestination, _: Bundle? ->
            if(nd.id != R.id.homeFragment){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }

        NavigationUI.setupWithNavController(binding.navView, navController)
        setUpActionForDrawerMenuItem()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController , drawerLayout)
    }

    override fun onBackPressed() {
        val navController = this.findNavController(R.id.myNavHostFragment)
        if(navController.currentDestination?.id == R.id.homeFragment){
            moveTaskToBack(true)
        }else{
            super.onBackPressed()
        }
    }

    fun setListener(listener : AllClearInterface) {
        this.listener = listener
    }

    private fun setUpActionForDrawerMenuItem(){
        binding.navView.menu.getItem(0).setOnMenuItemClickListener {
            Toast.makeText(application.applicationContext, "Clearing baby-urls...", Toast.LENGTH_SHORT).show()
            listener.allClear()
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        binding.navView.menu.getItem(1).setOnMenuItemClickListener { feedBackOption->
            NavigationUI.onNavDestinationSelected(feedBackOption , this.findNavController(R.id.myNavHostFragment))
            true
        }
        binding.navView.menu.getItem(2).setOnMenuItemClickListener {
            AuthUI.getInstance().signOut(this)
            true
        }
    }

}

interface AllClearInterface{
    fun allClear();
}
