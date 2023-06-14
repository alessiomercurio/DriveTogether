package com.agmobiletech.drivetogether

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agmobiletech.drivetogether.databinding.ActivityLoginBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener() {

            if (binding.mailTextLogin.text.isEmpty() || binding.passwLoginText.text.isEmpty()) {
                Toast.makeText(
                    this@LoginActivity,
                    "Inserisci qualcosa nei campi di inseriento",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // query
                val query =
                    "SELECT email from webmobile.Utente WHERE email = '${binding.mailTextLogin.text}' AND password = '${binding.passwLoginText.text}'"
                effettuaQuery(query)
            }
        }

        binding.nuovoUtenteLoginText.setOnClickListener(){
            //portare alla homepage
            //nella homepage richiedere autorizzazione per la posizione

        }
    }

    fun effettuaQuery(query : String){

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
//
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj?.size() != 0 && obj?.get(0)?.asJsonObject?.get("email")?.equals("null") == false){
                                // cambiamo activity e salviamo le credenziali
                                Toast.makeText(this@LoginActivity, "Credenziali giuste", Toast.LENGTH_LONG).show()
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

    /*
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted : Boolean ->
        if(isGranted){
            Toast.makeText(this@LoginActivity, "Permesso accordato", Toast.LENGTH_LONG).show()
        }
    }


    private fun setupPermission(){
        val permission = ContextCompat.checkSelfPermission(this@LoginActivity, android.Manifest.permission.INTERNET)

        if(permission == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this@LoginActivity, "Permesso accordato", Toast.LENGTH_LONG).show()

        }else if(shouldShowRequestPermissionRationale(android.Manifest.permission.INTERNET)){
            val builder = AlertDialog.Builder(this@LoginActivity)
            builder.setMessage("Il permesso è un requisito per poter usufruire dell'app")
            builder.setTitle("Permission required")
            builder.setPositiveButton("Ok"){
                diag, id ->
                requestPermission.launch(android.Manifest.permission.INTERNET)
            }
            val dialog = builder.create()
            dialog.show()
        }else{
            requestPermission.launch(android.Manifest.permission.INTERNET)
        }
    }
*/
}
