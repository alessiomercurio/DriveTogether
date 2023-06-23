package com.agmobiletech.drivetogether.inserimentoAuto

import android.content.SharedPreferences
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
import java.io.File
import java.util.Objects

class InserimentoAutoActivity : AppCompatActivity(){
    //inserimento spinner
    lateinit var binding : ActivityInserimentoAutoBinding
    lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre : SharedPreferences
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
        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        binding.confermaInserimentoAutoButton.setOnClickListener{
            //se i campi sono vuoti verrò notificato all'utente, tramite un toast, l'errore
            if (binding.targaPlainText.text.trim().isEmpty() || binding.marcaPlainText.text.trim().isEmpty() ||
                binding.modelloPlainText.text.trim().isEmpty() || binding.numeroPostiPlainText.text.trim().isEmpty() ||
                binding.prezzoPlainText.text.trim().isEmpty() || binding.posizionePlainText.text.trim().isEmpty())
            {
                    Toast.makeText(this@InserimentoAutoActivity, "Campi vuoti", Toast.LENGTH_LONG).show()
            }else if (binding.numeroPostiPlainText.text.trim().toString().toInt() <= 1){
                Toast.makeText(this@InserimentoAutoActivity, "Errore nell'inserimento del numero di posti", Toast.LENGTH_LONG).show()
            }else if (binding.prezzoPlainText.text.trim().toString().toDouble() <= 0){
                Toast.makeText(this@InserimentoAutoActivity, "Errore nell'inserimento del prezzo", Toast.LENGTH_LONG).show()
            }
            else {
                val targa = binding.targaPlainText.text.trim().toString()
                val marca = binding.marcaPlainText.text.trim().toString()
                val modello = binding.modelloPlainText.text.trim().toString()
                val numeroPosti = binding.numeroPostiPlainText.text.trim().toString()
                val prezzo = binding.prezzoPlainText.text.trim().toString().toDouble()
                val localizzazione = binding.posizionePlainText.text.trim().toString()
                val flagNoleggio = 0
                val imgMarcaAuto = scegliImmagine(marca)
                //bisogna inserire anche nella tabella possesso la Targa e l'email (presa dal file di testo "credenziali.txt")
                val queryAutomobile =
                "INSERT INTO Automobile (targa, marca, modello, numeroPosti, prezzo, localizzazione, flagNoleggio, imgMarcaAuto) " +
                        "values ('${targa}', '${marca}', '${modello}', '${numeroPosti}', '${prezzo}', '${localizzazione}', '${flagNoleggio}', '${imgMarcaAuto}');"
                val queryPossesso =
                "INSERT INTO Possesso (emailProprietario, targaAutomobile) " +
                        "values ('${filePre.getString("Email", "")}', '${targa}')"
                effettuaQuery(queryAutomobile)
                effettuaQuery(queryPossesso)
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

    fun scegliImmagine(marca : String) : String {
        //data una marca, restuisce una stringa contentente il path, e quindi il campo immagine nel db,  relativo alla marca
        val path = marca.lowercase()
        
        return "media/images/loghi/${path}.svg"
    }
}