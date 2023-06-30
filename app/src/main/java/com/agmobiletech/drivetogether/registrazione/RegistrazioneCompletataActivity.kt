package com.agmobiletech.drivetogether.registrazione

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazioneCompletataBinding
import com.agmobiletech.drivetogether.homepage.HomepageActivity

class RegistrazioneCompletataActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrazioneCompletataBinding
    lateinit var filePre : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneCompletataBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        /*
        * Visto che andiamo poi alla homepage a fine registrazione,
        * creiamo il file in modo da simulare la sessione dell'utente,
        * cosí anche se l'applicazione viene chiusa comuqnue non c'é bisogno di passare
        * di nuovo dalla pagina di login
        * */
        val editor = filePre.edit()
        editor.putString("Email", intent.extras?.getString("email"))
        editor.putString("Passw", intent.extras?.getString("passw"))
        editor.apply()

        // andiamo alla homepage
        binding.confermaRegistrazioneButton.setOnClickListener{
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }
    }
}
