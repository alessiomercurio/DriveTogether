package com.agmobiletech.drivetogether.homepage

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityHomepageBinding
import com.agmobiletech.drivetogether.databinding.ActivityLoginBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomepageActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomepageBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        /* // Imposta il bordo personalizzato sulla MapView
        val customBorderDrawable = ContextCompat.getDrawable(this, R.drawable.border_map)
        binding.mapView.background = customBorderDrawable

        // Inizializza la MapView
        mapView = findViewById(binding.mapView.id)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Imposta la posizione della mappa
        val latitude = 40.7128
        val longitude = -74.0060
        val location = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(location).title("Marker"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
    }
*/
    }
}