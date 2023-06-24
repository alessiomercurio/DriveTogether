package com.agmobiletech.drivetogether.homepage

import android.R.style
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityHomepageBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
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

        val bottomNavigationView = binding.bottomNavigationBar
        bottomNavigationView.selectedItemId = R.id.homepageMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        val permissionPos = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if(permissionPos == PackageManager.PERMISSION_GRANTED) {
            val permissionFine = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            if(permissionFine == PackageManager.PERMISSION_GRANTED) {
                    mapView = binding.mapView
                    mapView.visibility = View.VISIBLE
                    mapView.getMapboxMap().loadStyleUri(
                        com.mapbox.maps.Style.MAPBOX_STREETS,
                        object : com.mapbox.maps.Style.OnStyleLoaded {
                            override fun onStyleLoaded(style: com.mapbox.maps.Style) {
                                mapView.location.updateSettings {
                                    enabled = true
                                    pulsingEnabled = false
                                    addAnnotationToMap(13.3447344, 38.1223044)
                                }
                            }
                        }
                    )

                    val locationComponentPlugin = mapView.location
                    locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
                    locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
            }else{
                binding.mapView.visibility = View.GONE
            }
        }else{
            binding.mapView.visibility = View.GONE
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
    private fun addAnnotationToMap(longitudine : Double, latitudine: Double) {
    // Create an instance of the Annotation API and get the PointAnnotationManager.
            bitmapFromDrawableRes(
                this@HomepageActivity,
                R.drawable.red_marker
            )?.let {
                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
    // Set options for the resulting symbol layer.
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
    // Define a geographic coordinate.
                    .withPoint(Point.fromLngLat(longitudine, latitudine))
    // Specify the bitmap you assigned to the point annotation
    // The bitmap will be added to map style automatically.
                    .withIconImage(it)
    // Add the resulting pointAnnotation to the map.
                pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}
