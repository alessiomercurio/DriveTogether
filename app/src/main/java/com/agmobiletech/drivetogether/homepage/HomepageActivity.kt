package com.agmobiletech.drivetogether.homepage

import android.R.style
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityHomepageBinding
import com.agmobiletech.drivetogether.visualizzazioneAuto.CustomAdapter
import com.agmobiletech.drivetogether.visualizzazioneAuto.ItemsViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomepageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomepageBinding
    private lateinit var navigationManager: BottomNavigationManager
    lateinit var mapView: MapView
    lateinit var filePre: SharedPreferences

    private var longitudine : Double? = null
    private var latitudine : Double? = null

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

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

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
                                    caricaAutomobili()
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
                R.drawable.map_car
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
                pointAnnotationManager.create(pointAnnotationOptions)

                pointAnnotationManager.addClickListener(OnPointAnnotationClickListener {
                    mostraDialogPersonalizzato(this, longitudine, latitudine)
                    true
                })
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

    private fun caricaAutomobili(){
        /**
         * mi seleziono la longitudine e la latitudine di tutte le macchine che non sono possedute dall'utente loggato.
         * (n altre parole, seleziono la longitudine e la latitudine delle macchine degli altri utenti)
         * infine aggiungo un marker in base alla loro longitudine e latitudine
         */

        //vedere le amcchine disponibili
        val query = "SELECT A.localizzazioneLongitudinale, A.localizzazioneLatitudinale " +
                "FROM Automobile A, Utente U1, Possesso P " +
                "WHERE A.targa = P.targaAutomobile " +
                "AND U1.email = P.emailProprietario " +
                "AND U1.email NOT IN (  SELECT U2.email " +
                                        "FROM Utente U2 " +
                                        "WHERE U2.email = '${filePre.getString("Email", "")}')"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null) {
                                for (i in 0 until obj.size()) {
                                    longitudine = obj[i].asJsonObject?.get("localizzazioneLongitudinale").toString().trim('"').toDouble()
                                    latitudine = obj[i].asJsonObject?.get("localizzazioneLatitudinale").toString().trim('"').toDouble()
                                    addAnnotationToMap(longitudine!!, latitudine!!)
                                }
                            }
                        }
                    }else{
                        Toast.makeText(this@HomepageActivity, "Nessuna macchina disponibile", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@HomepageActivity, "Errore nel database", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    fun mostraDialogPersonalizzato(context: Context, longitudine: Double, latitudine: Double) {
        /*val builder = android.app.AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.prenotazione_dialog, null)*/

        var dialog = PrenotazioneDialog(this)

        val query = "SELECT A.targa, A.marca, A.modello, A.numeroPosti, A.prezzo, A.localizzazioneNominale, U.nome, U.cognome" +
                " FROM Automobile A, Utente U, Possesso P" +
                " WHERE A.targa = P.targaAutomobile" +
                " AND U.email = P.emailProprietario" +
                " AND localizzazioneLongitudinale = $longitudine AND localizzazioneLatitudinale = $latitudine"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null) {
                                val nomeECognome = obj[0].asJsonObject?.get("nome").toString().trim('"') +" " + obj[0].asJsonObject?.get("cognome").toString().trim('"')
                                dialog.binding.proprietarioTextPrenota.text = nomeECognome
                                dialog.binding.targaTextPrenota.text = obj[0].asJsonObject?.get("targa").toString().trim('"')
                                dialog.binding.marcaTextPrenota.text = obj[0].asJsonObject?.get("marca").toString().trim('"')
                                dialog.binding.modelloTextPrenota.text = obj[0].asJsonObject?.get("modello").toString().trim('"')
                                dialog.binding.numeroPostiTextPrenota.text = obj[0].asJsonObject?.get("numeroPosti").toString().trim('"')
                                dialog.binding.prezzoTextPrenota.text = obj[0].asJsonObject?.get("prezzo").toString().trim('"')
                                dialog.binding.posizioneTextPrenota.text = obj[0].asJsonObject?.get("localizzazioneNominale").toString().trim('"')
                            }
                        }
                    }else{
                        Toast.makeText(this@HomepageActivity, "Nessuna macchina disponibile", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@HomepageActivity, "Errore nel database", Toast.LENGTH_LONG).show()
                }
            }
        )


        dialog.show()
        /*
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()*/
    }


}
