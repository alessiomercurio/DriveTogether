package com.agmobiletech.drivetogether

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agmobiletech.drivetogether.databinding.ActivityLoginBinding
import com.agmobiletech.drivetogether.homepage.HomepageActivity
import com.agmobiletech.drivetogether.registrazione.RegistrazionePrimaActivity
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private var nomeFileCredenziali = "credenziali.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val file = File(this.filesDir, nomeFileCredenziali)
        if (file.exists() && file.readText().isNotEmpty()) {
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
                    // query
                    val query =
                        "SELECT email from webmobile.Utente WHERE email = '${binding.mailTextLogin.text}' AND password = '${binding.passwLoginText.text}'"
                    effettuaQuery(query)
                }
            }
        }

        binding.nuovoUtenteLoginText.setOnClickListener(){
            //nella homepage richiedere autorizzazione per la posizione
            val intent = Intent(this, RegistrazionePrimaActivity::class.java)
            startActivity(intent)
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
                                val email = binding.mailTextLogin.text.toString()
                                val password = binding.passwLoginText.text.toString()
                                val fileContents = "$email\n$password"
                                this@LoginActivity.openFileOutput(nomeFileCredenziali, Context.MODE_PRIVATE).use { output ->
                                    output.write(fileContents.toByteArray())
                                }
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
    /* permessi app
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
