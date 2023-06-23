package com.agmobiletech.drivetogether.homepage

import android.R.style
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityHomepageBinding
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location


class HomepageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomepageBinding
    private lateinit var navigationManager: BottomNavigationManager
    lateinit var mapView: MapView

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted : Boolean ->
        if(isGranted){
            Toast.makeText(this, "Permesso accordato", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        setupPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        setupPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)

        mapView = binding.mapView

        val bottomNavigationView = binding.bottomNavigationBar
        bottomNavigationView.selectedItemId = R.id.homepageMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        val permissionCoarseLoc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        //val permissionFineLoc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)

        if(permissionCoarseLoc == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapboxMap().loadStyleUri(
                Style.MAPBOX_STREETS,
                // After the style is loaded, initialize the Location component.
                object : Style.OnStyleLoaded {
                    override fun onStyleLoaded(style: Style) {
                        mapView.location.updateSettings {
                            enabled = true
                            pulsingEnabled = false
                        }
                    }
                }
            )

            val locationComponentPlugin = mapView.location
            locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupPermission(permission : String){
        if(shouldShowRequestPermissionRationale(permission)){
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Il permesso è un requisito per poter usufruire dell'app")
            builder.setTitle("Permission required")
            builder.setPositiveButton("Ok"){
                    _, _ ->
                requestPermission.launch(permission)
            }
            val dialog = builder.create()
            dialog.show()
        }else{
            requestPermission.launch(permission)
        }
    }
}
