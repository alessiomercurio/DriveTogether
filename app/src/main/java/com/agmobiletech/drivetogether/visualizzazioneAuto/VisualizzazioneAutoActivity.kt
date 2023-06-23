package com.agmobiletech.drivetogether.visualizzazioneAuto


import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle

import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityVisualizzazioneAutoBinding
import com.agmobiletech.drivetogether.databinding.CardViewLayoutBinding
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisualizzazioneAutoActivity : AppCompatActivity(){
    lateinit var binding : ActivityVisualizzazioneAutoBinding
    lateinit var binding2 : CardViewLayoutBinding
    private lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre: SharedPreferences
    lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizzazioneAutoBinding.inflate(layoutInflater)
        binding2 = CardViewLayoutBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationBar
        bottomNavigationView.selectedItemId = R.id.autoMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        val query = "SELECT Automobile.marca, Automobile.modello, Automobile.targa, Automobile.imgMarcaAuto " +
                "FROM Utente, Automobile, Possesso " +
                "WHERE Utente.email = '${filePre.getString("Email", "")}'" +
                "AND Utente.email = Possesso.emailProprietario " +
                "AND Automobile.targa = Possesso.targaAutomobile"
        resituisciMacchine(query)
        //adapter = CustomAdapter(data)
        //binding.recyclerView.adapter = adapter
        //implementare onClick per la recyclerview

    }

    private fun resituisciMacchine(query : String){
        val data = ArrayList<ItemsViewModel>()
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null) {
                                for (i in 0 until obj.size()) {
                                    val marca = obj[i].asJsonObject?.get("marca").toString()
                                    val modello = obj[i].asJsonObject?.get("modello").toString()
                                    val targa = obj[i].asJsonObject?.get("targa").toString()
                                    val immagine = R.drawable.email
                                    //val imgMarcaAuto  = restituisciImmagineMarca(obj[i].asJsonObject, binding2.immagineMarcaImageView)
                                    //data.add(ItemsViewModel(marca, modello, targa, imgMarcaAuto))
                                    data.add(ItemsViewModel(marca, modello, targa, immagine))
                                    restituisciImmagineMarca(obj[i].asJsonObject, binding2.immagineMarcaImageView)
                                    //binding2.macchinaCardView.invalidate()
                                }
                                //sistemare l'errore E/RecyclerView: No adapter attached; skipping layout
                                adapter = CustomAdapter(data)
                                binding.recyclerView.adapter = adapter

                                adapter.setOnClickListener(object : CustomAdapter.OnClickListener{
                                    override fun onClick(position: Int, model: ItemsViewModel) {
                                        Toast.makeText(this@VisualizzazioneAutoActivity,"Prova per vedere se riesce", Toast.LENGTH_LONG).show()
                                    }

                                })
                            }
                        }
                    }else{
                        Toast.makeText(this@VisualizzazioneAutoActivity, "Non hai inserito nessuna macchina", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@VisualizzazioneAutoActivity, "Errore nel database", Toast.LENGTH_LONG).show()
                }

            }
        )
    }

    //passare come parametro url
    private fun restituisciImmagineMarca(jsonObject: JsonObject, imageView : ImageView){

        val url: String = jsonObject.get("imgMarcaAuto").asString

        ClientNetwork.retrofit.getImage(url).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if(response.isSuccessful) {
                        var immagineProfilo: Bitmap? = null
                        if (response.body()!=null) {
                            immagineProfilo = BitmapFactory.decodeStream(response.body()?.byteStream())
                            imageView.setImageBitmap(immagineProfilo)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@VisualizzazioneAutoActivity, "Errore nel database", Toast.LENGTH_LONG).show()
                }

            }
        )
    }

}