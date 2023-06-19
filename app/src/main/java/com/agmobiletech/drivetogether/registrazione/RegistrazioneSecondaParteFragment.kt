package com.agmobiletech.drivetogether.registrazione

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.RegistrazioneSecondaParteFragmentBinding
import com.agmobiletech.drivetogether.homepage.HomepageActivity
import com.google.gson.JsonObject
import com.squareup.moshi.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrazioneSecondaParteFragment : Fragment(R.layout.registrazione_seconda_parte_fragment) {

  lateinit var binding: RegistrazioneSecondaParteFragmentBinding
  private val TAG = "Fragment 2"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegistrazioneSecondaParteFragmentBinding.inflate(inflater)

        // recuperiamo i campi da inserire nella query
        val email = arguments?.getString("email")
        val nome = arguments?.getString("nome")
        val cognome = arguments?.getString("cognome")
        val data = arguments?.getString("data")

        // se non sono nulli, definiamo il listener nel bottone per concludere la registrazione
        if(email != null && nome != null && cognome != null && data != null){
            binding.confermaRegistrazioneButton.setOnClickListener{
                if(checkCampi()){
                    // creiamo la query e la eseguiamo
                    val query = "INSERT INTO Utente(email, nome, cognome, dataNascita, telefono, cartaCredito, password, immagineProfilo) VALUES(" +
                            "'${email}'," +
                            "'${nome}'," +
                            "'${cognome}'," +
                            "'${data}'," +
                            "${binding.telefonoRegistrazioneText.text.toString().trim()}," +
                            "${binding.creditoRegistrazioneText.text.toString().trim()}," +
                            "'${binding.passwordRegistrazioneText.text.toString().trim()}'," +
                            "''" +
                            ")"
                    effettuaQuery(query)
                }else{
                    Toast.makeText(this.requireContext(), "Campi vuoi", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(this.requireContext(), "Problemi con i fragment", Toast.LENGTH_LONG).show()
        }
        return binding.root
    }

    private fun checkCampi() : Boolean{
        if(binding.telefonoRegistrazioneText.text.isNotEmpty() && binding.creditoRegistrazioneText.text.isNotEmpty()
            && binding.passwordRegistrazioneText.text.isNotEmpty())
            return true
        return false
    }

    /*
    * Se la risposta é successful allora é sicuro che é stato inserito il record nel database,
    * altrimenti vuol dire che qualcosa é andato storto
    * Se é successfull, cambiamo activity ed eseguiamo un intent
    * */
    private fun effettuaQuery(query: String){
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    System.out.println(query)
                    if(response.isSuccessful){
                        val i = Intent(this@RegistrazioneSecondaParteFragment.requireContext(), RegistrazioneCompletataActivity::class.java)
                        startActivity(i)
                    }else{
                        Toast.makeText(
                            this@RegistrazioneSecondaParteFragment.requireContext(),
                            "Errore del Database",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    System.out.println(query + "2")
                    Toast.makeText(
                        this@RegistrazioneSecondaParteFragment.requireContext(),
                        "Errore del Database",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}