package com.bcontrol.app.bcontrol

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import org.altbeacon.beacon.permissions.BeaconScanPermissionsActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    // Initialise the DrawerLayout, NavigationView and ToggleBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
//        setSupportActionBar(toolbar)
//        supportActionBar?.title="Inicio"
        Log.d("DEBUG", "Main activity created")
        setContentView(R.layout.activity_main)
        // Call findViewById on the DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById<NavigationView>(R.id.navView)
        // Pass the ActionBarToggle action into the drawerListener
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)
        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()
        // Call findViewById on the NavigationView
        navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(navView, navController)
//        replaceFragment(LoginFragment(), "Ingresar")



//        navView.setupWithNavController(navController)
        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener(this)

    }

    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
//        val navController = this.findNavController(R.id.nav_host_fragment)
        return true;//NavigationUI.navigateUp(navController,drawerLayout)
    }

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        Log.d("MainActivity", "onPause")
        super.onPause()
    }

    override fun onResume() {
        Log.d("MainActivity", "onResume")
        super.onResume()
        // You MUST make sure the following dynamic permissions are granted by the user to detect beacons
        //
        //    Manifest.permission.BLUETOOTH_SCAN
        //    Manifest.permission.BLUETOOTH_CONNECT
        //    Manifest.permission.ACCESS_FINE_LOCATION
        //    Manifest.permission.ACCESS_BACKGROUND_LOCATION // only needed to detect in background
        //
        // The code needed to get these permissions has become increasingly complex, so it is in
        // its own file so as not to clutter this file focussed on how to use the library.

        if (!BeaconScanPermissionsActivity.allPermissionsGranted(
                this,
                true
            )
        ) {
            val intent = Intent(this, BeaconScanPermissionsActivity::class.java)
            intent.putExtra("backgroundAccessRequested", true)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.fragment_container, fragment)
//        fragmentTransaction.commit()
        // supportActionBar?.title=title
        setTitle(title)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        drawerLayout.closeDrawers()
        menuItem.isChecked = true
        when (menuItem.itemId) {
            R.id.home -> {
                Log.d("DEBUG", "pressed home")
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                if(myUser.roleId==1) {
                    navController.navigate(R.id.homeSupervisorFragment)
                }else{
                    navController.navigate(R.id.homeWorkerFragment)
                }
                true
            }
            R.id.profileFragment -> {
                Log.d("DEBUG", "pressed profile")
                Toast.makeText(this, "Perfil de usuario", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.profileFragment)
                true
            }
            R.id.logout -> {
                Log.d("DEBUG", "pressed logout")
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                true
            }
            else -> {
                false
            }
        }
        return true
    }
}