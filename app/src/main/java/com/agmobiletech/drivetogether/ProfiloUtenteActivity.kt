package com.agmobiletech.drivetogether

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityProfiloUtenteBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File


class ProfiloUtenteActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfiloUtenteBinding
    lateinit var navigationManager: BottomNavigationManager
    var nomeFileCredenziali = "credenziali.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfiloUtenteBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.profiloMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)
        binding.logOutButton.setOnClickListener{
            val file = File(this.filesDir, nomeFileCredenziali)
            file.delete()
            Toast.makeText(this@ProfiloUtenteActivity,"Disconnessione avvenuta con succeso",Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}