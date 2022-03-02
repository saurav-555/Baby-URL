package com.example.android.babyurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.babyurl.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("UNUSED_VARIABLE")
        val binding : ActivitySplashScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        val navController = this.findNavController(R.id.mySplashNavHostFragment)

        NavigationUI.setupActionBarWithNavController(this, navController)

        navController.addOnDestinationChangedListener{ _: NavController, nd : NavDestination, _: Bundle? ->
            if(nd.id == R.id.splashFragment){
                supportActionBar?.hide()
            }else{
                supportActionBar?.show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.mySplashNavHostFragment)
        return navController.navigateUp()
    }

    @Suppress("DEPRECATION")
    fun launchHomeActivity(){
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } , 2000)
    }

}