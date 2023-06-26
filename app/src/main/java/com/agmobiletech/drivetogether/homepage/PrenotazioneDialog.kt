package com.agmobiletech.drivetogether.homepage

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.agmobiletech.drivetogether.databinding.PrenotazioneDialogBinding


class PrenotazioneDialog(context : Context) : Dialog(context) {
    lateinit var binding : PrenotazioneDialogBinding

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = PrenotazioneDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        creaSpinner()

        binding.prenotaButton.setOnClickListener{
            Toast.makeText(context, "Pagamento effettuato, auto prenotata", Toast.LENGTH_LONG).show()
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
}