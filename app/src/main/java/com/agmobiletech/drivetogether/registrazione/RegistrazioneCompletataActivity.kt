package com.agmobiletech.drivetogether.registrazione

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityRegistrazioneCompletataBinding
import com.agmobiletech.drivetogether.homepage.HomepageActivity

class RegistrazioneCompletataActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrazioneCompletataBinding
    var nomeFileCredenziali = "credenziali.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneCompletataBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        var fileContents = "login confermato"

        /*
        * Visto che andiamo poi alla homepage a fine registrazione,
        * creiamo il file in modo da simulare la sessione dell'utente,
        * cosí anche se l'applicazione viene chiusa comuqnue non c'é bisogno di passare
        * di nuovo dalla pagina di login
        *
        * */
        this@RegistrazioneCompletataActivity.openFileOutput(nomeFileCredenziali, Context.MODE_PRIVATE).use { output ->
            output.write(fileContents.toByteArray())
        }

        // andiamo alla homepage
        binding.confermaRegistrazioneButton.setOnClickListener{
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }
    }
}
