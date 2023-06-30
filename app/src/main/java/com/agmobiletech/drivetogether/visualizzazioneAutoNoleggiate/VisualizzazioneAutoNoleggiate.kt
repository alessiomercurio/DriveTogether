package com.agmobiletech.drivetogether.visualizzazioneAutoNoleggiate

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityVisualizzazioneAutoNoleggiateBinding
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class VisualizzazioneAutoNoleggiate : AppCompatActivity() {
    lateinit var binding : ActivityVisualizzazioneAutoNoleggiateBinding
    lateinit var filePre : SharedPreferences
    private lateinit var navigationManager: BottomNavigationManager
    lateinit var adapter: CustomAdapterNoleggio

    @RequiresApi(Build.VERSION_CODES.O)
    private var dataAttuale = LocalDate.now().toString()

    private var idNoleggio : Int? = null
    private var targaNoleggioAttuale : String? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizzazioneAutoNoleggiateBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationBar
        bottomNavigationView.selectedItemId = R.id.autoMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        restituisciCronologiaMacchineNoleggiate(dataAttuale)
        restituisciMacchinaAttualmenteNoleggiata(dataAttuale)

        binding.CronologiaButton.setOnClickListener{
            if(binding.cronologiaFrameLayout.isVisible){
                binding.CronologiaButton.text = "Mostra cronologia"
                binding.cronologiaFrameLayout.visibility = View.GONE
            }else{
                binding.CronologiaButton.text = "Nascondi cronologia"
                binding.cronologiaFrameLayout.visibility = View.VISIBLE
            }
        }

        binding.macchinaNoleggiataCardView.setOnClickListener{
            mostraDialogConfermaRimozione(this)
        }
    }

    private fun restituisciMacchinaAttualmenteNoleggiata(dataAttuale : String){
        val query = "SELECT N.idNoleggio, A.marca, A.modello, A.targa, A.imgMarcaAuto, N.dataInizioNoleggio " +
                "FROM  Noleggio N, Automobile A, Utente U " +
                "WHERE N.targaAutomobile = A.targa " +
                "AND N.emailNoleggiatore = U.email " +
                "AND U.email = '${filePre.getString("Email", "")}' " +
                "AND A.flagNoleggio = 1 " +
                "AND '${dataAttuale}' BETWEEN N.dataInizioNoleggio AND N.dataFineNoleggio " +
                "ORDER BY N.dataInizioNoleggio DESC " +
                "LIMIT 1;"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val queryset = response.body()?.getAsJsonArray("queryset")
                            if(queryset != null && queryset.size() > 0){
                                val obj = response.body()?.getAsJsonArray("queryset")?.get(0)
                                idNoleggio = obj?.asJsonObject?.get("idNoleggio")?.asInt
                                binding.marcaNoleggioDaInserire.text = obj?.asJsonObject?.get("marca")?.asString
                                binding.modelloNoleggioDaInserire.text = obj?.asJsonObject?.get("modello")?.asString
                                binding.targaNoleggioDaInserire.text = obj?.asJsonObject?.get("targa")?.asString
                                targaNoleggioAttuale = binding.targaNoleggioDaInserire.text.toString()
                                restituisciImmagineMarca(obj?.asJsonObject?.get("imgMarcaAuto")?.asString, binding.marcaAutoNoleggiataImageView)
                            }else{
                                binding.macchinaNoleggiataCardView.visibility = View.GONE
                                binding.noleggioAttualeTextView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    binding.marcaAutoNoleggiataImageView.setImageResource(R.drawable.carmarker)
                }

            }
        )
    }

    private fun restituisciImmagineMarca(url: String?, imageView: ImageView) {
        if (url != null) {
            ClientNetwork.retrofit.getImage(url).enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful && response.body() != null) {
                            val immagineAuto = BitmapFactory.decodeStream(response.body()!!.byteStream())
                            imageView.setImageBitmap(immagineAuto)
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        imageView.setImageResource(R.drawable.carmarker)
                    }
                }
            )
        }
    }

    private fun restituisciCronologiaMacchineNoleggiate(dataAttuale : String){
        val data = ArrayList<ItemsViewModelNoleggio>()
        val query = "SELECT A.marca, A.modello, A.targa, A.imgMarcaAuto, N.dataInizioNoleggio , N.dataFineNoleggio " +
                "FROM  Noleggio N, Automobile A, Utente U " +
                "WHERE N.targaAutomobile = A.targa " +
                "AND N.emailNoleggiatore = U.email " +
                "AND U.email = '${filePre.getString("Email", "")}' " +
                "AND '${dataAttuale}' >  N.dataFineNoleggio " +
                "ORDER BY N.dataInizioNoleggio DESC"

        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val queryset = response.body()?.getAsJsonArray("queryset")
                            if(queryset != null && queryset.size() > 0){
                                var marca : String? = ""
                                var modello : String? = ""
                                var targa : String? = ""
                                var inizioNoleggio : String? = ""
                                var fineNoleggio : String? = ""
                                for (i in 0 until queryset.size()) {
                                    System.out.println(queryset[i]?.asJsonObject)
                                    marca = queryset[i]?.asJsonObject?.get("marca")?.asString
                                    modello = queryset[i]?.asJsonObject?.get("modello")?.asString
                                    targa = queryset[i]?.asJsonObject?.get("targa")?.asString
                                    val immagine = R.id.autoMenuItem
                                    val immagineURL = queryset[i].asJsonObject?.get("imgMarcaAuto")?.asString
                                    inizioNoleggio = queryset[i]?.asJsonObject?.get("dataInizioNoleggio")?.asString
                                    fineNoleggio = queryset[i]?.asJsonObject?.get("dataFineNoleggio")?.asString
                                    data.add(ItemsViewModelNoleggio(marca, modello, targa, immagine, immagineURL, inizioNoleggio, fineNoleggio))
                                }
                                adapter = CustomAdapterNoleggio(data)
                                binding.cronologiaRecyclerView.adapter = adapter
                                binding.cronologiaRecyclerView.layoutManager = LinearLayoutManager(this@VisualizzazioneAutoNoleggiate)
                            }else{
                                binding.noleggioPassatoTextView.visibility = View.VISIBLE
                                binding.CronologiaButton.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@VisualizzazioneAutoNoleggiate, "Errore nel database", Toast.LENGTH_LONG).show()
                }

            }
        )
    }

    fun mostraDialogConfermaRimozione(context: Context) {
        val builder = AlertDialog.Builder(context)

        // Imposta il titolo del dialog
        builder.setTitle("Termina noleggio")

        // Imposta il messaggio del dialog
        builder.setMessage("Sei sicuro di voler terminare il noleggio di questo auto?")

        // Aggiunge il pulsante "Rimuovi"
        builder.setPositiveButton("Termina") { dialog, which ->
            // Funzione chiamata quando viene premuto il pulsante "Rimuovi"
            eseguiRimozioneContenuto()
        }

        // Aggiunge il pulsante "Annulla"
        builder.setNegativeButton("Annulla") { dialog, which ->
            // Funzione chiamata quando viene premuto il pulsante "Annulla"
            dialog.dismiss()
        }

        // Crea e mostra il dialog
        val dialog = builder.create()
        dialog.show()

        // Ottieni i pulsanti positiveButton e negativeButton dal dialog
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        // Imposta il colore del testo dei pulsanti
        positiveButton.setTextColor(Color.parseColor("#ff4040"))
        negativeButton.setTextColor(Color.parseColor("#00987D"))
    }

    fun eseguiRimozioneContenuto() {
        val query = "DELETE FROM Noleggio " +
                    "WHERE idNoleggio = ${idNoleggio}"

        ClientNetwork.retrofit.remove(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            Toast.makeText(this@VisualizzazioneAutoNoleggiate, "Noleggio terminato con successo", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@VisualizzazioneAutoNoleggiate, "Errore nel database", Toast.LENGTH_LONG).show()
                }

            }
        )
        val queryUpdate = "UPDATE Automobile " +
                "SET flagNoleggio = 0 " +
                "WHERE targa = '${targaNoleggioAttuale}'"

        ClientNetwork.retrofit.update(queryUpdate).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val intent = Intent(this@VisualizzazioneAutoNoleggiate, VisualizzazioneAutoNoleggiate::class.java)
                            startActivity(intent)
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@VisualizzazioneAutoNoleggiate, "Errore nel database", Toast.LENGTH_LONG).show()
                }

            }
        )
    }
}