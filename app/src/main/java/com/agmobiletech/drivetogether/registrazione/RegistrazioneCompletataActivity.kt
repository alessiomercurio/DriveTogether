package com.agmobiletech.drivetogether.registrazione

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazioneCompletataBinding
import com.agmobiletech.drivetogether.homepage.HomepageActivity

class RegistrazioneCompletataActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrazioneCompletataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneCompletataBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        binding.confermaRegistrazioneButton.setOnClickListener{
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }
    }
}
