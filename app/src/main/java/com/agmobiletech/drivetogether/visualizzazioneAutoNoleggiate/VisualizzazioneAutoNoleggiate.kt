package com.agmobiletech.drivetogether.visualizzazioneAutoNoleggiate

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityVisualizzazioneAutoNoleggiateBinding
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisualizzazioneAutoNoleggiate : AppCompatActivity() {
    lateinit var binding : ActivityVisualizzazioneAutoNoleggiateBinding
    lateinit var filePre : SharedPreferences
    private lateinit var navigationManager: BottomNavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizzazioneAutoNoleggiateBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationBar
        bottomNavigationView.selectedItemId = R.id.autoMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)
        restituisciMacchinaAttualmenteNoleggiata()
    }

    private fun restituisciMacchinaAttualmenteNoleggiata(){
        val query = "SELECT A.marca, A.modello, A.targa, A.imgMarcaAuto, N.dataInizioNoleggio " +
                "FROM  Noleggio N, Automobile A, Utente U " +
                "WHERE N.targaAutomobile = A.targa " +
                "AND N.emailNoleggiatore = U.email " +
                "AND U.email = '${filePre.getString("Email", "")}' " +
                "AND A.flagNoleggio = 1 " +
                "ORDER BY N.dataInizioNoleggio DESC " + //aggiungere data ordierna che deve essere compresa tra datainizio e fine
                "LIMIT 1;"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")?.get(0)
                            if(obj != null){
                                binding.marcaNoleggioDaInserire.text = obj.asJsonObject?.get("marca")?.asString
                                binding.modelloNoleggioDaInserire.text = obj.asJsonObject?.get("modello")?.asString
                                binding.targaNoleggioDaInserire.text = obj.asJsonObject?.get("targa")?.asString
                                restituisciImmagineMarca(obj.asJsonObject?.get("imgMarcaAuto")?.asString, binding.marcaAutoNoleggiataImageView)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    binding.marcaAutoNoleggiataImageView.setImageResource(R.drawable.carmarker)
                }

            }
        )
    }

    private fun restituisciImmagineMarca(url: String?, imageView: ImageView) {
        if (url != null) {
            ClientNetwork.retrofit.getImage(url).enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful && response.body() != null) {
                            val immagineAuto = BitmapFactory.decodeStream(response.body()!!.byteStream())
                            imageView.setImageBitmap(immagineAuto)
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        imageView.setImageResource(R.drawable.carmarker)
                    }
                }
            )
        }
    }

    private fun restituisciCronologiaMacchineNoleggiate(){
        val query = ""
    }
}