package com.example.angelocapstoneproject.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.domain.helper.checkPermission
import com.example.angelocapstoneproject.databinding.ActivityHomeBinding
import com.example.angelocapstoneproject.domain.CallEmergencyReceiver
import com.example.angelocapstoneproject.ui.home.fragments.EmergencyContactFragment
import com.example.angelocapstoneproject.ui.home.fragments.PlaybackFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class Home : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController


    private val requestCallPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (!isGranted){
            Toast.makeText(this, "Need call permission to proceeed..", Toast.LENGTH_SHORT).show()
            finish()
        }
    }



    private val requestContactPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        if(
            !(it[Manifest.permission.READ_CONTACTS] == true
                    && it[Manifest.permission.WRITE_CONTACTS] == true)
        ){
            Toast.makeText(this, "Need contact permission to proceeed..", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.live_view, R.id.playback, R.id.devices, R.id.emergency_contact
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(onDestinationChangedListener)

        if (!checkPermission(this, Manifest.permission.CALL_PHONE)){
            requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }

        if (!checkPermission(this, Manifest.permission.READ_CONTACTS)
            || !checkPermission(this, Manifest.permission.WRITE_CONTACTS)
        ){
            requestContactPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                )
            )
        }

        if(intent?.action == EmergencyContactFragment.EMERGENCY_NOTIF_ACTION || intent?.action == EmergencyContactFragment.EMERGENCY_CALL_ACTION){
            onNewIntent(intent)
        }
    }

    private val onDestinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            binding.appBarHome.titleBar.text = destination.label
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        when (intent?.action) {
            EmergencyContactFragment.EMERGENCY_NOTIF_ACTION -> {
                val deviceIp = intent.getStringExtra(PlaybackFragment.DEVICE_IP) ?: return
                val playbackUrl = intent.getStringExtra(PlaybackFragment.PLAYBACK_URL) ?: return
                val date = intent.getStringExtra(PlaybackFragment.DATE) ?: return
                val number = intent.getStringExtra(PlaybackFragment.CALL_NUMBER)

                navController.popBackStack()

                val args = Bundle().also {
                    it.putString(PlaybackFragment.DEVICE_IP, deviceIp)
                    it.putString(PlaybackFragment.PLAYBACK_URL, playbackUrl)
                    it.putString(PlaybackFragment.DATE, date)
                    number?.apply { it.putString(PlaybackFragment.CALL_NUMBER, this) }
                }

                navController.navigate(R.id.playback, args)
            }
            else -> super.onNewIntent(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}