package com.agmobiletech.drivetogether

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityLoginBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)


        val query = "SELECT email, nome from webmobile.Utente;"

        ClientNetwork.retrofit.select(query).enqueue(
                object : Callback<JsonObject> {

                    override fun onResponse(call : Call<JsonObject>, response : Response<JsonObject>){
                        if(response.isSuccessful) {
                            if (response.body() != null) {
                                val obj = response.body()
                                System.out.println("RESPONSEEEEEEEEEE " + obj!!.getAsJsonArray("queryset")[0].asJsonObject.get("nome"))
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(
                                this@LoginActivity,
                                "credenziali errate",
                                Toast.LENGTH_LONG
                            ).show()
                    }
                }

        )

    }
}