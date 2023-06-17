package com.agmobiletech.drivetogether.registrazione

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazionePrimaParteBinding


class RegistrazionePrimaActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrazionePrimaParteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazionePrimaParteBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        binding.registrazioneButton.setOnClickListener(){
            if(checkCampi())
                Toast.makeText(this@RegistrazionePrimaActivity, "Riempire tutti i campi", Toast.LENGTH_LONG).show()
            else{
                val intent = Intent(this, RegistrazioneSecondaActivity::class.java)
                intent.putExtra("email", binding.emailRegistrazionePlainText.text.toString().trim())
                intent.putExtra("nome", binding.nomeRegistrazionePlainText.text.toString().trim())
                intent.putExtra("cognome", binding.cognomeRegistrazionePlainText.text.toString().trim())
                intent.putExtra("dataNascita", binding.dataEditText.text.toString().trim())
                startActivity(intent)
            }
        }
    }

    fun checkCampi() : Boolean{
        if(binding.nomeRegistrazionePlainText.text.isNotEmpty() && binding.cognomeRegistrazionePlainText.text.isNotEmpty()
            && binding.emailRegistrazionePlainText.text.isNotEmpty() && binding.dataEditText.text.isNotEmpty()
        )
            return false
        return true
    }
}