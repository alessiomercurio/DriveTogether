package com.agmobiletech.drivetogether.homepage

import android.R
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agmobiletech.drivetogether.ClientNetwork

import com.agmobiletech.drivetogether.databinding.PrenotazioneDialogBinding
import com.google.gson.JsonObject
import com.squareup.moshi.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.Date


class PrenotazioneDialog(context : Context) : Dialog(context) {
    lateinit var binding : PrenotazioneDialogBinding
    lateinit var filePre : SharedPreferences

    init {
        setCancelable(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = PrenotazioneDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filePre = context.getSharedPreferences("Credenziali", AppCompatActivity.MODE_PRIVATE)


        creaSpinner()

        binding.prenotaButton.setOnClickListener{
            val dataFineNoleggio = calcolaData(binding.spinnerPrenota.selectedItem.toString()).toString()
            val dataInizioNoleggio = LocalDate.now().toString()
            val targa = binding.targaTextPrenota.text.toString().trim()
            val emailProprietario = filePre.getString("Email", "")

            val creaNoleggio = "INSERT INTO Noleggio(emailNoleggiatore, targaAutomobile, dataInizioNoleggio, dataFineNoleggio) " +
                    "VALUES ('$emailProprietario', '$targa', '$dataInizioNoleggio', '$dataFineNoleggio')"
            Toast.makeText(context, creaNoleggio, Toast.LENGTH_LONG).show()


            ClientNetwork.retrofit.insert(creaNoleggio).enqueue(
                object : Callback<JsonObject>{
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if(response.isSuccessful){
                            val aggiornaAuto = "UPDATE Automobile SET flagNoleggio = 1 WHERE targa = '$targa'"
                            aggiornaAuto(aggiornaAuto)
                        }else{
                            Toast.makeText(context, "Errore ", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(context, "Errore database", Toast.LENGTH_SHORT).show()
                    }

                }
            )
        }
    }

    private fun creaSpinner(){
        val spinner = binding.spinnerPrenota
        val giorni = arrayOf(1, 2, 3, 4, 5, 6, 7)

        val adapterSpinner = ArrayAdapter(context, android.R.layout.simple_spinner_item, giorni)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapterSpinner

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var giorno = giorni[position] // Imposta il primo modello come predefinito
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Fai nulla
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcolaData(giorni : String) : LocalDate{
        var dataCorrente = LocalDate.now()

        dataCorrente = dataCorrente.plusDays(giorni.toLong())
        return dataCorrente
    }

    private fun aggiornaAuto(query : String){
        ClientNetwork.retrofit.update(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        Toast.makeText(context, "Pagamento effettuato", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(context, "Errore ", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(context, "Errore database", Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

}