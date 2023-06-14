package com.agmobiletech.drivetogether.registrazione

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazioneConfermataBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrazioneCompletataActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrazioneConfermataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneConfermataBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
    }
}
