package com.agmobiletech.drivetogether.registrazione

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.agmobiletech.drivetogether.R
import java.text.SimpleDateFormat
import java.util.*
import com.agmobiletech.drivetogether.databinding.RegistrazionePrimaParteFragmentBinding

class RegistrazionePrimaParteFragment : Fragment(R.layout.registrazione_prima_parte_fragment) {

    lateinit var binding : RegistrazionePrimaParteFragmentBinding
    private val TAG = "Fragment 1"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegistrazionePrimaParteFragmentBinding.inflate(inflater)
        val parentManager = parentFragmentManager

        // inizializziamo un oggetto di tipo calendar da utilizzare per il date picker
        val calendar = Calendar.getInstance()

        // creiamo l'oggetto datepicker e inizializziamo un listener
        val datePicker = DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(calendar)
        }

        // mostriamo il datepicker
        binding.selezionaData.setOnClickListener{
            DatePickerDialog(this.requireContext(), datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.registrazioneButton.setOnClickListener{
            // controlliamo che tutti i campi non siano vuoti
            if(checkCampi()){
                /* nel caso in cui il fragment della seconda parte della registrazione non esiste,
                * aggiungiamo il seconod fragment effettuanod un replace, prima però aggiungiamo
                * gli argomenti da passare al secondo fragment
                * */
                if(!fragmentExists(parentManager, "Fragment 2")){
                    val transaction = parentManager.beginTransaction()
                    val newFrag = RegistrazioneSecondaParteFragment()
                    newFrag.arguments = addArgs()
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                    transaction.replace(R.id.fragmentContainerView, newFrag, "Fragment 2").commit()
                }else{
                    val frag2 = parentManager.findFragmentByTag("Fragment 2")
                    if(frag2 != null){
                        val transaction = parentManager.beginTransaction()
                        val newFrag = RegistrazioneSecondaParteFragment()
                        newFrag.arguments = addArgs()
                        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                        transaction.replace(R.id.fragmentContainerView, newFrag, "Fragment 2").commit()
                    }
                }
            }else{
                Toast.makeText(this.requireContext(),  "Campi vuoi", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

    // restituisce un oggetto di tipo bundle per inserier gli argomenti da passare al secondo fragment di registrazione
    private fun addArgs() : Bundle{
        val message = Bundle()
        message.putString("email", binding.emailRegistrazionePlainText.text.toString().trim())
        message.putString("nome", binding.nomeRegistrazionePlainText.text.toString().trim())
        message.putString("cognome", binding.cognomeRegistrazionePlainText.text.toString().trim())
        message.putString("data", binding.dataEditText.text.toString().trim())
        return message
    }

    // mostriamo la data a schermo effettuando un parse della data
    private fun updateLable(calendar: Calendar) {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        binding.dataEditText.text = sdf.format(calendar.time)
    }

    private fun checkCampi() : Boolean{
        if(binding.emailRegistrazionePlainText.text.isNotEmpty() && binding.nomeRegistrazionePlainText.text.isNotEmpty()
            && binding.dataEditText.text.isNotEmpty() && binding.cognomeRegistrazionePlainText.text.isNotEmpty())
            return true
        return false
    }

    private fun fragmentExists(parentManager: FragmentManager, tag: String): Boolean {
        val fragment = parentManager.findFragmentByTag(tag)
        return fragment != null
    }

}