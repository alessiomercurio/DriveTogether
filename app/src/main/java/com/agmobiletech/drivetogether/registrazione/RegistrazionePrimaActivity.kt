package com.agmobiletech.drivetogether.registrazione

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Window
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
    }
}