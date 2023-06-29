package com.agmobiletech.drivetogether.visualizzazioneAuto

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.CustomDialogBinding
import com.google.gson.JsonObject
import com.mapbox.maps.plugin.Plugin
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
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
import com.mapbox.search.ui.view.SearchResultsView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomDialog(context: Context) : Dialog(context) {

    lateinit var binding : CustomDialogBinding
    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView

    private lateinit var searchResultsView: SearchResultsView
    private lateinit var searchEngineUiAdapter: SearchEngineUiAdapter

    //variabili che servono per inserire le informazioni riguardo la macchina nel database
    private var posizioneNominale : String? = null
    private var latidutine : Double? = null
    private var longitudine : Double? = null
    private var marca : String? = null
    private var modello : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        creaSpinner()

        // prova
        searchResultsView = findViewById<SearchResultsView>(R.id.search_results_view)
        // searchResultsView initialization

        searchResultsView.initialize(
            SearchResultsView.Configuration(
                CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )

        val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
            SearchEngineSettings(context.getString(R.string.mapbox_access_token))
        )

        val offlineSearchEngine = OfflineSearchEngine.create(
            OfflineSearchEngineSettings(context.getString(R.string.mapbox_access_token))
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
                binding.posizioneLeMieAuto.setText(searchResult.name.toString())
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

        binding.posizioneLeMieAuto.addTextChangedListener(object : TextWatcher {
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


        binding.salvaButton.setOnClickListener{
            val modificaAutoQuery = "UPDATE Automobile" +
                    " SET targa = '${binding.targaLeMieAuto.text}'" +
                    ", marca = '${binding.marcaLeMieAuto.selectedItem}'" +
                    ", modello = '${binding.modelloLeMieAuto.selectedItem}'" +
                    ", numeroPosti = '${binding.postiLeMieAuto.text}'" +
                    ", prezzo = '${binding.prezzoLeMieAuto.text}'" +
                    ", localizzazioneNominale = '${posizioneNominale}'" +
                    ", localizzazioneLongitudinale = '${longitudine}'" +
                    ", localizzazioneLatitudinale = '${latidutine}'" +
                    " WHERE targa = '${binding.targaLeMieAuto.text}'"

            ClientNetwork.retrofit.update(modificaAutoQuery).enqueue(
                object : Callback<JsonObject>{
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if(response.isSuccessful){
                            Toast.makeText(context, "Auto aggiornata", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(context, "Errore nell'aggiornare l'auto", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(context, "Errore del server", Toast.LENGTH_LONG).show()
                    }

                }
            )
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

    private fun creaSpinner(){
        val spinnerMarca = binding.marcaLeMieAuto
        val spinnerModello = binding.modelloLeMieAuto

        val marcheAuto = arrayOf("Audi", "BMW", "FIAT", "Ford", "Hyundai", "Jeep", "Lamborghini", "Mercedes", "Porsche", "Tesla", "Toyota", "Volkswagen")

        val modelliAuto = arrayOf(
            arrayOf("A1", "A3", "A4", "A6", "Q3", "Q5"),
            arrayOf("Series 1", "Series 3", "Series 5", "X1", "X3", "X5"),
            arrayOf("500", "Panda", "Tipo", "Punto", "500X"),
            arrayOf("Mustang", "Focus", "F-150"),
            arrayOf("i10", "i20", "i30", "Kona", "Tucson"),
            arrayOf("Wrangler", "Grand Cherokee", "Compass"),
            arrayOf("Huracan", "Aventador", "Gallardo", "Urus"),
            arrayOf("C-Class", "E-Class", "S-Class", "GLC", "GLE", "GLA"),
            arrayOf("911", "Cayman", "Panamera", "Macan", "Taycan"),
            arrayOf("Model S", "Model 3", "Model X", "Model Y"),
            arrayOf("Corolla", "Camry", "RAV4", "Highlander", "C-HR"),
            arrayOf("Golf", "Passat", "Tiguan", "Polo")
        )

        val adapterMarche = ArrayAdapter(context, android.R.layout.simple_spinner_item, marcheAuto)
        adapterMarche.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMarca.adapter = adapterMarche

        val adapterModelli = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayOf<String>())
        adapterModelli.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModello.adapter = adapterModelli


        spinnerMarca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var marcaSelezionata = modelliAuto[position] // Imposta il primo modello come predefinito

                // imposta gli elementi dello spinner dei modelli
                val adapterModelliMarca = ArrayAdapter(this@CustomDialog.context, android.R.layout.simple_spinner_item, marcaSelezionata)
                adapterModelliMarca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerModello.adapter = adapterModelliMarca



                marca = marcheAuto.get(position)
                if (marcaSelezionata.isNotEmpty()) {
                        modello = marcaSelezionata[0] // Imposta il primo modello come predefinito

                        spinnerModello.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                modello = marcaSelezionata[position] // Ottieni il modello selezionato
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // Fai nulla
                            }
                        }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Fai nulla
            }
        }
    }
}