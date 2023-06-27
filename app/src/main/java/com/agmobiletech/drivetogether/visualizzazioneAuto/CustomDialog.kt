package com.agmobiletech.drivetogether.visualizzazioneAuto

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.databinding.CustomDialogBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomDialog(context: Context) : Dialog(context) {

    lateinit var binding : CustomDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.salvaButton.setOnClickListener{
        }


        binding.rimuoviButton.setOnClickListener{

            val query = "DELETE FROM Automobile " +
                    "WHERE targa = '${binding.targaLeMieAuto.text}'"

            ClientNetwork.retrofit.remove(query).enqueue(
                object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if(response.isSuccessful){
                            Toast.makeText(context, "Macchina eliminata", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(context, "Errore nell'eliminare l'auto", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(context, "Errore del server", Toast.LENGTH_LONG).show()
                    }

                }
            )
        }

    }
}