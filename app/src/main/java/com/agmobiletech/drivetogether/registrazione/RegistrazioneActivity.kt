package com.agmobiletech.drivetogether.registrazione

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazionePrimaParteBinding


class RegistrazioneActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrazionePrimaParteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazionePrimaParteBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.add(binding.fragmentContainerView.id, RegistrazionePrimaParteFragment())
            transaction.commit()
        }

    }
}

/*
*         val dataNascita = informazioniPrimaParte.getString("dataNascita").toString().replace("/", "-")
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
                * */