package com.agmobiletech.drivetogether.inserimentoAuto

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityInserimentoAutoBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.Objects

class InserimentoAutoActivity : AppCompatActivity(){
    lateinit var binding : ActivityInserimentoAutoBinding
    lateinit var navigationManager: BottomNavigationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInserimentoAutoBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        //seleziono la navbar prendendo l'id
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        //rendo "selezionato" l'elemento che clicco, in questo caso l'item di inserimento di una macchina
        bottomNavigationView.selectedItemId = R.id.inserimentoMenuItem
        //cambio activity
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        binding.confermaInserimentoAutoButton.setOnClickListener{
            //se i campi sono vuoti verrò notificato all'utente, tramite un toast, l'errore
            if(binding.targaPlainText.text.trim().isEmpty() || binding.marcaPlainText.text.trim().isEmpty() ||
                binding.modelloPlainText.text.trim().isEmpty() || binding.numeroPostiPlainText.text.trim().isEmpty() ||
                binding.prezzoPlainText.text.trim().isEmpty() || binding.posizionePlainText.text.trim().isEmpty())
            {
                    Toast.makeText(this@InserimentoAutoActivity, "Campi vuoti", Toast.LENGTH_LONG).show()
            }else {
                val targa = binding.targaPlainText.text.trim().toString()
                val marca = binding.marcaPlainText.text.trim().toString()
                val modello = binding.modelloPlainText.text.trim().toString()
                val numeroPosti = binding.numeroPostiPlainText.text.trim().toString()
                val prezzo = binding.prezzoPlainText.text.trim().toString().toDouble()
                val localizzazione = binding.posizionePlainText.text.trim().toString()
                val flagNoleggio = 0
                val query =
                    "INSERT INTO Macchina(targa, marca, modello, numeroPosti, prezzo, localizzazione, flagNoleggio)\n" +
                    "values ('${targa}', '${marca}', '${modello}', '${numeroPosti}', '${prezzo}', '${localizzazione}', '${flagNoleggio}')"
                effettuaQuery(query)
            }
        }
    }

    fun effettuaQuery(query : String) {
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@InserimentoAutoActivity,
                            "Macchina inserita correttamente",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@InserimentoAutoActivity,
                            "Errore nell'inserimento",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@InserimentoAutoActivity,
                        "Errore nel database",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}