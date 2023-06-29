package com.agmobiletech.drivetogether.profiloUtente

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityModificaProfiloBinding
import com.agmobiletech.drivetogether.registrazione.RegistrazioneCompletataActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ModificaUtenteActivity : AppCompatActivity() {
    lateinit var binding : ActivityModificaProfiloBinding
    lateinit var filePre : SharedPreferences
    lateinit var navigationManager: BottomNavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModificaProfiloBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Creiamo la navigation bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.profiloMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        val datiProfilo = intent.extras?: return

        binding.emailProfilo.setText(datiProfilo.getString("Email"))
        binding.nomeModificaProfilo.setText(datiProfilo.getString("Nome"))
        binding.cognomeModificaProfilo.setText(datiProfilo.getString("Cognome"))
        binding.dataNascitaProfilo.setText(datiProfilo.getString("DataNascita"))
        binding.cartaCreditoProfilo.setText(datiProfilo.getString("cartaCredito"))
        binding.telefonoProfilo.setText(datiProfilo.getString("Telefono"))
        binding.passwordProfilo.setText(datiProfilo.getString("Password"))

        // inizializziamo un oggetto di tipo calendar da utilizzare per il date picker
        val calendar = Calendar.getInstance()

        // creiamo l'oggetto datepicker e inizializziamo un listener
        val datePicker = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(calendar)
        }

        // mostriamo il datepicker
        binding.selezionaData2.setOnClickListener{
            DatePickerDialog(this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.confermaModifica.setOnClickListener{
            if(checkCampi()){
                var query = "UPDATE Utente SET email = '${binding.emailProfilo.text.toString().trim()}'," +
                        " nome = '${binding.nomeModificaProfilo.text.toString().trim()}'," +
                        " cognome = '${binding.cognomeModificaProfilo.text.toString().trim()}'," +
                        " dataNascita = '${binding.dataNascitaProfilo.text.toString().trim()}'," +
                        " telefono = ${binding.telefonoProfilo.text.toString().trim()}," +
                        " cartaCredito = ${binding.cartaCreditoProfilo.text.toString().trim()}," +
                        " password = '${binding.passwordProfilo.text.toString().trim()}'" +
                        " WHERE email = '${datiProfilo.getString("Email")}'"
                aggiornaProfilo(query)
            }
        }
    }

    private fun checkCampi() : Boolean{
        val patternTelefono = Regex("^[0-9]{10}")
        val patternCartaDiCredito = Regex("^[0-9]{16}")
        val patterNomeCognomeEmail = Regex("^[0-9]+")
        var check = false

        if(
            binding.emailProfilo.text.isNotEmpty() && binding.nomeModificaProfilo.text.isNotEmpty()
            && binding.dataNascitaProfilo.text.isNotEmpty() && binding.telefonoProfilo.text.isNotEmpty() &&
            binding.cartaCreditoProfilo.text.isNotEmpty() && binding.passwordProfilo.text.isNotEmpty()
            && binding.cognomeModificaProfilo.text.isNotEmpty()
        ){
            check = true
            if(binding.emailProfilo.text.matches(patterNomeCognomeEmail)){
                check = false
                Toast.makeText(this, "Inserire una email valida", Toast.LENGTH_LONG).show()
            }else if(binding.nomeModificaProfilo.text.matches(patterNomeCognomeEmail)){
                check = false
                Toast.makeText(this, "Inserire un nome valido", Toast.LENGTH_LONG).show()
            }
            else if(binding.cognomeModificaProfilo.text.matches(patterNomeCognomeEmail)){
                check = false
                Toast.makeText(this, "Inserire un cognome valido", Toast.LENGTH_LONG).show()
            }else if(!binding.telefonoProfilo.text.matches(patternTelefono)){
                check = false
                Toast.makeText(this, "Inserire un numero di telefono valido", Toast.LENGTH_LONG).show()
            }else if(!binding.cartaCreditoProfilo.text.matches(patternCartaDiCredito)){
                check = false
                Toast.makeText(this, "Inserire una numero di carta valido", Toast.LENGTH_LONG).show()
            }
        }else
            Toast.makeText(this, "I campi sono vuoti", Toast.LENGTH_LONG).show()
        return check
    }

    private fun updateLable(calendar: Calendar) {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        binding.dataNascitaProfilo.setText(sdf.format(calendar.time))
    }

    private fun aggiornaProfilo(query: String){
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    System.out.println(query)
                    if(response.isSuccessful){
                        filePre = this@ModificaUtenteActivity.getSharedPreferences("Credenziali", MODE_PRIVATE)
                        val editor = filePre.edit()
                        editor.clear()
                        editor.putString("Email", binding.emailProfilo.text.toString().trim())
                        editor.putString("Passw", binding.passwordProfilo.text.toString().trim())
                        editor.apply()
                        val i = Intent(this@ModificaUtenteActivity, ProfiloUtenteActivity::class.java)
                        startActivity(i)
                    }else{
                        Toast.makeText(
                            this@ModificaUtenteActivity,
                            "Errore del Database",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    System.out.println(query + "2")
                    Toast.makeText(
                        this@ModificaUtenteActivity,
                        "Errore del Database",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}