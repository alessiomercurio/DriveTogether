package com.agmobiletech.drivetogether.visualizzazioneAuto


import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityVisualizzazioneAutoBinding
import com.agmobiletech.drivetogether.databinding.CardViewLayoutBinding
import com.agmobiletech.drivetogether.databinding.CustomDialogBinding
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisualizzazioneAutoActivity : AppCompatActivity(){
    lateinit var binding : ActivityVisualizzazioneAutoBinding
    lateinit var binding2 : CardViewLayoutBinding
    lateinit var binding3 : CustomDialogBinding
    private lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre: SharedPreferences
    lateinit var adapter: CustomAdapter

    private var immaginaMarcaSecure : ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizzazioneAutoBinding.inflate(layoutInflater)
        binding2 = CardViewLayoutBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationBar
        bottomNavigationView.selectedItemId = R.id.profiloMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        val query = "SELECT Automobile.marca, Automobile.modello, Automobile.targa, Automobile.numeroPosti, Automobile.prezzo, Automobile.localizzazioneNominale, Automobile.imgMarcaAuto " +
                "FROM Utente, Automobile, Possesso " +
                "WHERE Utente.email = '${filePre.getString("Email", "")}'" +
                "AND Utente.email = Possesso.emailProprietario " +
                "AND Automobile.targa = Possesso.targaAutomobile"
        resituisciMacchine(query)
        //adapter = CustomAdapter(data)
        //binding.recyclerView.adapter = adapter
        //implementare onClick per la recyclerview

    }

    private fun resituisciMacchine(query : String){
        val data = ArrayList<ItemsViewModel>()
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null) {
                                var marca : String? = ""
                                var modello : String? = ""
                                var targa : String? = ""
                                var numeroPosti : String? = ""
                                var prezzo : String? = ""
                                var posizione : String? = ""

                                for (i in 0 until obj.size()) {
                                    marca = obj[i].asJsonObject?.get("marca")?.toString()?.trim('"')
                                    modello = obj[i].asJsonObject?.get("modello")?.toString()?.trim('"')
                                    targa = obj[i].asJsonObject?.get("targa")?.toString()?.trim('"')
                                    numeroPosti = obj[i].asJsonObject?.get("numeroPosti")?.toString()?.trim('"')
                                    prezzo = obj[i].asJsonObject?.get("prezzo")?.toString()?.trim('"')
                                    posizione = obj[i].asJsonObject?.get("localizzazioneNominale")?.toString()?.trim('"')
                                    val immagine = R.id.autoMenuItem
                                    val immagineURL = obj[i].asJsonObject?.get("imgMarcaAuto")?.asString
                                    data.add(ItemsViewModel(marca, modello, targa,numeroPosti, prezzo, posizione, immagine, immagineURL))
                                    //restituisciImmagineMarca(obj[i].asJsonObject, binding2.immagineMarcaImageView)
                                    //binding2.macchinaCardView.invalidate()
                                    // prova
                                }
                                //sistemare l'errore E/RecyclerView: No adapter attached; skipping layout
                                adapter = CustomAdapter(data)
                                binding.recyclerView.adapter = adapter

                                adapter.setOnClickListener(object : CustomAdapter.OnClickListener{
                                    override fun onClick(position: Int, model: ItemsViewModel) {
                                        mostraDialogPersonalizzato(this@VisualizzazioneAutoActivity, model.targa, model.marca, model.modello, model.numeroPosti, model.prezzo, model.posizione)
                                    }

                                })
                            }
                        }
                    }else{
                        Toast.makeText(this@VisualizzazioneAutoActivity, "Non hai inserito nessuna macchina", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@VisualizzazioneAutoActivity, "Errore nel database", Toast.LENGTH_LONG).show()
                }

            }
        )
    }

    //passare come parametro url
    private fun restituisciImmagineMarca(jsonObject: JsonObject, imageView : ImageView){

        val url: String = jsonObject.get("imgMarcaAuto").asString

        ClientNetwork.retrofit.getImage(url).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if(response.isSuccessful) {
                        var immagineProfilo: Bitmap? = null
                        if (response.body()!=null) {
                            immagineProfilo = BitmapFactory.decodeStream(response.body()?.byteStream())
                            imageView.setImageBitmap(immagineProfilo)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@VisualizzazioneAutoActivity, "Errore nel database", Toast.LENGTH_LONG).show()
                }

            }
        )
    }


    fun mostraDialogPersonalizzato(context: Context, targa : String?, marca : String?, modello : String?, numeroPosti : String?, prezzo : String?, posizione : String?) {
        val dialog = CustomDialog(this@VisualizzazioneAutoActivity, marca, modello)
        dialog.show()

        dialog.binding.targaLeMieAuto.setText(targa)
        dialog.binding.postiLeMieAuto.setText(numeroPosti)
        dialog.binding.prezzoLeMieAuto.setText(prezzo)
        dialog.binding.posizioneLeMieAuto.setText(posizione)
    }

}
