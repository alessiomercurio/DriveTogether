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
        val query = "SELECT * FROM Macchina"
        resituisciMacchine(query)
    }

    private fun resituisciMacchine(query : String){
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null) {
                                val data = ArrayList<ItemsViewModel>()
                                for (i in 0 until obj.size()) {
                                    val marca = obj[i].asJsonObject?.get("marca").toString()
                                    val modello = obj[i].asJsonObject?.get("modello").toString()
                                    val targa = obj[i].asJsonObject?.get("targa").toString()
                                    val imgMarcaAuto = R.drawable.email
                                    //obj?.get(0)?.asJsonObject?.get("imgMarcaAuto").toString() oppure Bitmap
                                    data.add(ItemsViewModel(marca, modello, targa, imgMarcaAuto))
                                }
                                //sistemare l'errore E/RecyclerView: No adapter attached; skipping layout
                                val adapter = CustomAdapter(data)
                                binding.recyclerView.adapter = adapter
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

}