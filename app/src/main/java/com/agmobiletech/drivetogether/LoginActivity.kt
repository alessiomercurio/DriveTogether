package com.agmobiletech.drivetogether

import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityLoginBinding
import com.agmobiletech.drivetogether.homepage.HomepageActivity
import com.agmobiletech.drivetogether.registrazione.RegistrazioneActivity
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var filePre : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        if (!filePre.getString("Email", "").equals("")) {
            //alla fase di logout, verranno eliminati email e password
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }else {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            binding.buttonLogin.setOnClickListener() {

                if (binding.mailTextLogin.text.isEmpty() || binding.passwLoginText.text.isEmpty()) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Inserisci qualcosa nei campi di inserimento",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val query =
                        "SELECT email from webmobile.Utente WHERE email = '${binding.mailTextLogin.text}' AND password = '${binding.passwLoginText.text}'"
                    effettuaQuery(query)
                }
            }
        }

        binding.nuovoUtenteLoginText.setOnClickListener(){
            val intent = Intent(this, RegistrazioneActivity::class.java)
            startActivity(intent)
        }
    }

    fun effettuaQuery(query : String){

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj?.size() != 0 && obj?.get(0)?.asJsonObject?.get("email")?.equals("null") == false){
                                // cambiamo activity e salviamo le credenziali
                                Toast.makeText(this@LoginActivity, "Credenziali giuste", Toast.LENGTH_LONG).show()
                                val email = binding.mailTextLogin.text.toString().trim()
                                val password = binding.passwLoginText.text.toString().trim()

                                filePre = this@LoginActivity.getSharedPreferences("Credenziali", Context.MODE_PRIVATE)
                                val editor = filePre.edit()
                                editor.putString("Email", email)
                                editor.putString("Passw", password)
                                editor.apply()

                                val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@LoginActivity, "Credenziali errate", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Errore del Database",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

}
