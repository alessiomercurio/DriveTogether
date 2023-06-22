package com.agmobiletech.drivetogether.profiloUtente

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.LoginActivity
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityProfiloUtenteBinding
import com.agmobiletech.drivetogether.homepage.HomepageActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ProfiloUtenteActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfiloUtenteBinding
    lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfiloUtenteBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        // Creiamo la navigation bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.profiloMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        val query =
            "SELECT email, nome, cognome, dataNascita, telefono, cartaCredito, password, immagineProfilo from webmobile.Utente WHERE email = '${filePre.getString("Email", "")}' AND password = '${filePre.getString("Passw", "")}'"
        recuperaProfilo(query)

        // Listener sul button per effettuare il logout, eliminiamo il file che veniva utilizzato per
        // ricordare l'utente. Ci riporta alla activity di login
        binding.logOutButton.setOnClickListener{
            val editor = filePre.edit()
            editor.clear()
            editor.apply()
            Toast.makeText(this@ProfiloUtenteActivity,"Disconnessione avvenuta con succeso",Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // On click per il bottone di modifica profilo
        binding.modificaProfiloButton.setOnClickListener{
            val i = Intent(this, ModificaUtenteActivity::class.java)
            i.putExtra("Email", binding.emailProfilo.text.toString().trim())
            i.putExtra("Nome", binding.nomeProfilo.text.toString().trim())
            i.putExtra("Cognome", binding.cognomeProfilo.text.toString().trim())
            i.putExtra("DataNascita", binding.dataNascitaProfilo.text.toString().trim())
            i.putExtra("Telefono", binding.telefonoProfilo.text.toString().trim())
            i.putExtra("cartaCredito", binding.cartaCreditoProfilo.text.toString().trim())
            i.putExtra("Password", binding.passwordProfilo.text.toString().trim())
            startActivity(i)
        }

    }

    fun recuperaProfilo(query : String){
        var email = ""
        var nome = ""
        var cognome = ""
        var dataNascita = ""
        var telefono = ""
        var cartaCredito = ""
        var password = ""
        var immagineProfilo = ""
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                //
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        System.out.println(response.body())
                        if (response.body() != null) {
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj?.size() != 0 && obj?.get(0)?.asJsonObject?.get("email")?.equals("null") == false){
                                email = obj.get(0).asJsonObject.get("email").toString().replace("\"", "")
                                nome = obj.get(0).asJsonObject.get("nome").toString().replace("\"", "")
                                cognome = obj.get(0).asJsonObject.get("cognome").toString().replace("\"", "")
                                dataNascita = obj.get(0).asJsonObject.get("dataNascita").toString().replace("\"", "")
                                telefono = obj.get(0).asJsonObject.get("telefono").toString().replace("\"", "")
                                cartaCredito = obj.get(0).asJsonObject.get("cartaCredito").toString().replace("\"", "")
                                password = obj.get(0).asJsonObject.get("password").toString().replace("\"", "")
                                immagineProfilo = obj.get(0).asJsonObject.get("immagineProfilo").toString().replace("\"", "")

                                binding.emailProfilo.text = email
                                binding.nomeProfilo.text = nome
                                binding.cognomeProfilo.text = cognome
                                binding.dataNascitaProfilo.text = dataNascita
                                binding.telefonoProfilo.text = telefono
                                binding.cartaCreditoProfilo.text = cartaCredito
                                binding.passwordProfilo.text = password

                            }else{
                                Toast.makeText(this@ProfiloUtenteActivity, "Credenziali errate", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@ProfiloUtenteActivity,
                        "Errore del Database",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

}