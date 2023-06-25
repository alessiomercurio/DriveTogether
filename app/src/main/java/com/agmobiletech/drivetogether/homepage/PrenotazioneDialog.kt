package com.agmobiletech.drivetogether.homepage

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.agmobiletech.drivetogether.R
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

        binding.prenotaButton.setOnClickListener{
            Toast.makeText(context, "Pagamento effettuato, auto prenotata", Toast.LENGTH_LONG).show()
        }
    }
}