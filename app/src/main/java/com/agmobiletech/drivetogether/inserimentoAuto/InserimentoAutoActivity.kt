package com.agmobiletech.drivetogether.inserimentoAuto

import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.SearchView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityInserimentoAutoBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.lifecycleScope
import com.mapbox.search.ApiType
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autofill.Query
import com.mapbox.search.offline.OfflineResponseInfo
import com.mapbox.search.offline.OfflineSearchEngine
import com.mapbox.search.offline.OfflineSearchEngineSettings
import com.mapbox.search.offline.OfflineSearchResult
import com.mapbox.search.record.HistoryRecord
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import com.mapbox.search.ui.adapter.engines.SearchEngineUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchMode
import com.mapbox.search.ui.view.SearchResultsView
import kotlinx.coroutines.launch

class InserimentoAutoActivity : AppCompatActivity(){
    //inserimento spinner
    lateinit var binding : ActivityInserimentoAutoBinding
    lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre : SharedPreferences

    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView

    private lateinit var searchResultsView: SearchResultsView
    private lateinit var searchEngineUiAdapter: SearchEngineUiAdapter

    //variabili che servono per inserire le informazioni riguardo la macchina nel database
    private var posizioneNominale : String? = null
    private var latidutine : Double? = null
    private var longitudine : Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInserimentoAutoBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        // prova
        searchResultsView = findViewById<SearchResultsView>(R.id.search_results_view)
        // searchResultsView initialization

        searchResultsView.initialize(
            SearchResultsView.Configuration(
                CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )

        val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
            SearchEngineSettings(getString(R.string.mapbox_access_token))
        )

        val offlineSearchEngine = OfflineSearchEngine.create(
            OfflineSearchEngineSettings(getString(R.string.mapbox_access_token))
        )

        searchEngineUiAdapter = SearchEngineUiAdapter(
            view = searchResultsView,
            searchEngine = searchEngine,
            offlineSearchEngine = offlineSearchEngine,
        )


        searchEngineUiAdapter.addSearchListener(object : SearchEngineUiAdapter.SearchListener {

            override fun onSuggestionsShown(
                suggestions: List<SearchSuggestion>,
                responseInfo: ResponseInfo
            ) {
                // Nothing to do
                System.out.println("Funziona9254")
            }

            override fun onError(e: Exception) {
                System.out.println("Funziona9054")
            }

            override fun onFeedbackItemClick(responseInfo: ResponseInfo) {
                System.out.println("Funziona102")
            }

            override fun onHistoryItemClick(historyRecord: HistoryRecord) {
                System.out.println("Funziona00")
            }

            override fun onOfflineSearchResultSelected(
                searchResult: OfflineSearchResult,
                responseInfo: OfflineResponseInfo
            ) {
                System.out.println("Funziona1312")
            }

            override fun onOfflineSearchResultsShown(
                results: List<OfflineSearchResult>,
                responseInfo: OfflineResponseInfo
            ) {
                System.out.println("Funziona1")
            }

            override fun onPopulateQueryClick(
                suggestion: SearchSuggestion,
                responseInfo: ResponseInfo
            ) {
                System.out.println("Funziona4")
            }

            //metodo che viene richiamato quando si clicca un risultato
            override fun onSearchResultSelected(
                searchResult: SearchResult,
                responseInfo: ResponseInfo
            ) {
                binding.posizionePlainText.setText(searchResult.name.toString())
                searchResultsView.visibility = View.GONE
                //assegnazione dei valori da inserire nel database

                posizioneNominale = searchResult.name
                latidutine = searchResult.coordinate.latitude()
                longitudine = searchResult.coordinate.longitude()
            }

            override fun onSearchResultsShown(
                suggestion: SearchSuggestion,
                results: List<SearchResult>,
                responseInfo: ResponseInfo
            ) {
                System.out.println("Funziona23")
                searchResultsView.visibility = View.VISIBLE
            }

            override fun onSuggestionSelected(searchSuggestion: SearchSuggestion): Boolean {
                return false
            }
        })

        binding.posizionePlainText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = Query.create(p0.toString())
                if (query != null) {
                    searchEngineUiAdapter.search(p0.toString())
                }
                searchResultsView.visibility = View.VISIBLE
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        //seleziono la navbar prendendo l'id
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        //rendo "selezionato" l'elemento che clicco, in questo caso l'item di inserimento di una macchina
        bottomNavigationView.selectedItemId = R.id.inserimentoMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)
        // Recupero credenziali
        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        binding.confermaInserimentoAutoButton.setOnClickListener{
            //se i campi sono vuoti verrò notificato all'utente, tramite un toast, l'errore
           if(checkCampi() == 1){
               System.out.println("Posizione :" + posizioneNominale + ", longitudine: " + longitudine + ", latitudine: " + latidutine)
               Toast.makeText(this@InserimentoAutoActivity, "Campi vuoti", Toast.LENGTH_LONG).show()
           }else if(checkCampi() == 2){
               Toast.makeText(this@InserimentoAutoActivity, "Errore nell'inserimento del numero di posti", Toast.LENGTH_LONG).show()
           }else if(checkCampi() == 3){
               Toast.makeText(this@InserimentoAutoActivity, "Errore nell'inserimento del prezzo", Toast.LENGTH_LONG).show()
           }else {
               val targa = binding.targaPlainText.text.trim().toString()
               val marca = binding.marcaPlainText.text.trim().toString()
               val modello = binding.modelloPlainText.text.trim().toString()
               val numeroPosti = binding.numeroPostiPlainText.text.trim().toString()
               val prezzo = binding.prezzoPlainText.text.trim().toString().toDouble()
               val localizzazione = binding.posizionePlainText.text.trim().toString()
               val flagNoleggio = 0
               val imgMarcaAuto = scegliImmagine(marca)
               //bisogna inserire anche nella tabella possesso la Targa e l'email (presa dal file di testo "credenziali.txt")
               val queryAutomobile = "INSERT INTO Automobile (targa, marca, modello, numeroPosti, prezzo, localizzazioneNominale, localizzazioneLongitudinale, localizzazioneLatitudinale, flagNoleggio, imgMarcaAuto) " +
                       "values ('${targa}', '${marca}', '${modello}', '${numeroPosti}', '${prezzo}', '${localizzazione}', '${longitudine}', '${latidutine}', '${flagNoleggio}', '${imgMarcaAuto}');"
               val queryPossesso = "INSERT INTO Possesso (emailProprietario, targaAutomobile) " +
                       "values ('${filePre.getString("Email", "")}', '${targa}')"
               effettuaQuery(queryAutomobile)
               effettuaQuery(queryPossesso)
            }
        }
    }

    fun effettuaQuery(query : String) {
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@InserimentoAutoActivity,
                            "Macchina inserita correttamente",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@InserimentoAutoActivity,
                            "Errore nell'inserimento",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@InserimentoAutoActivity,
                        "Errore nel database",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    fun checkCampi() : Int{
        if (binding.targaPlainText.text.trim().isEmpty() || binding.marcaPlainText.text.trim().isEmpty() ||
            binding.modelloPlainText.text.trim().isEmpty() || binding.numeroPostiPlainText.text.trim().isEmpty() ||
            binding.prezzoPlainText.text.trim().isEmpty() || binding.posizionePlainText.text.trim().isEmpty())
        {
            return 1
        }else if (binding.numeroPostiPlainText.text.trim().toString().toInt() <= 1){
            return 2
        }else if (binding.prezzoPlainText.text.trim().toString().toDouble() <= 0){
            return 3
        }
        return 0
    }


    fun scegliImmagine(marca : String) : String {
        //data una marca, restuisce una stringa contentente il path, e quindi il campo immagine nel db,  relativo alla marca
        val path = marca.lowercase()
        
        return "media/images/loghi/${path}.svg"
    }

    private fun closeSearchView() {
        toolbar.collapseActionView()
        searchView.setQuery("", false)
    }
}