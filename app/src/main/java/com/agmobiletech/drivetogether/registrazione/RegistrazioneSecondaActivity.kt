package com.agmobiletech.drivetogether.registrazione

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazioneCompletataBinding
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazioneSecondaParteBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrazioneSecondaActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrazioneSecondaParteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneSecondaParteBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val informazioniPrimaParte : Bundle? = intent.extras

        binding.confermaRegistrazioneButton.setOnClickListener(){
            if(informazioniPrimaParte != null && checkCampi())
                effettuaQueryRegistrazione(informazioniPrimaParte)
            else
                Toast.makeText(this@RegistrazioneSecondaActivity, "Inserisci qualcosa nei campi", Toast.LENGTH_LONG).show()
        }
    }

    // Stare attenti quando si prende il .text, perchè restituisce una spannable string invece che una string
    // si puó risolvere semplicemente richiamando il metodo .toString()
    fun effettuaQueryRegistrazione(informazioniPrimaParte : Bundle){
        val dataNascita = informazioniPrimaParte.getString("dataNascita").toString().replace("/", "-")
        val query = "INSERT INTO Utente(email, nome, cognome, dataNascita, telefono, cartaCredito, password, immagineProfilo) VALUES(" +
                "'${informazioniPrimaParte.getString("email").toString()}'," +
                "'${informazioniPrimaParte.getString("nome").toString()}'," +
                "'${informazioniPrimaParte.getString("cognome").toString()}'," +
                "'${dataNascita}'," +
                "${binding.telefonoRegistrazioneText.text.toString()}," +
                "${binding.creditoRegistrazioneText.text.toString()}," +
                "'${binding.passwordRegistrazioneText.text.toString()}'," +
                "''" +
                ")"

        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        vaiAPaginaDiConferma()
                    }else
                        Toast.makeText(this@RegistrazioneSecondaActivity, "Errore database", Toast.LENGTH_SHORT).show()

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@RegistrazioneSecondaActivity, "Problemi con il server", Toast.LENGTH_LONG).show()
                }
            })
    }

    fun vaiAPaginaDiConferma(){
        val intent = Intent(this, RegistrazioneCompletataActivity::class.java)
        startActivity(intent)
    }

    fun checkCampi() : Boolean{
        if(binding.telefonoRegistrazioneText.text.isNotEmpty()
            && binding.creditoRegistrazioneText.text.isNotEmpty() && binding.passwordRegistrazioneText.text.isNotEmpty()
        )
            return true
        return false
    }

}
