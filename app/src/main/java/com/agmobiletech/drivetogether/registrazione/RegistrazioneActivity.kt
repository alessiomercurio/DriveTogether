package com.agmobiletech.drivetogether.registrazione

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazionePrimaParteBinding


class RegistrazioneActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrazionePrimaParteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazionePrimaParteBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
    }
}