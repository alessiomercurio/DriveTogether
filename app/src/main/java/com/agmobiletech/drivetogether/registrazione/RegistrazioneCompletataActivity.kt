package com.agmobiletech.drivetogether.registrazione

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazioneCompletataBinding

class RegistrazioneCompletataActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrazioneCompletataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneCompletataBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
    }
}
