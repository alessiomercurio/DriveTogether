package com.agmobiletech.drivetogether.visualizzazioneAuto

import android.os.Bundle

import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityVisualizzazioneAutoBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisualizzazioneAutoActivity : AppCompatActivity(){
    lateinit var binding : ActivityVisualizzazioneAutoBinding
    private lateinit var navigationManager: BottomNavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizzazioneAutoBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationBar
        bottomNavigationView.selectedItemId = R.id.autoMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        //bisogna selezionare tutte le entità della tabella Possesso
        //dove l'email dell'utente equivale a quella contenuta nel file "registrazione.txt"
        val query = "SELECT * FROM Possesso"
        resituisciMacchine(query)

        val data = ArrayList<ItemsViewModel>()
        for (i in 1..20){
            data.add(ItemsViewModel("{marca}", "{modello}", "{targa}", R.drawable.euro))
        }
        val adapter = CustomAdapter(data)
        binding.recyclerView.adapter = adapter

    }

    fun resituisciMacchine(query : String){
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")

                            val marca = obj?.get(0)?.asJsonObject?.get("marca")
                            val modello = obj?.get(0)?.asJsonObject?.get("modello")
                            val targa = obj?.get(0)?.asJsonObject?.get("targa")
                            val imgMarcaAuto = obj?.get(0)?.asJsonObject?.get("imgMarcaAuto")
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

}