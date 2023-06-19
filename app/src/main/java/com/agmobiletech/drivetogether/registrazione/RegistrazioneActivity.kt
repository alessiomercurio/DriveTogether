package com.agmobiletech.drivetogether.registrazione

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
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