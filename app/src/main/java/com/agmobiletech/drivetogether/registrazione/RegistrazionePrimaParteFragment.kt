package com.agmobiletech.drivetogether.registrazione

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.agmobiletech.drivetogether.R
import java.text.SimpleDateFormat
import java.util.*
import com.agmobiletech.drivetogether.databinding.RegistrazionePrimaParteFragmentBinding

class RegistrazionePrimaParteFragment : Fragment(R.layout.registrazione_prima_parte_fragment) {

    lateinit var binding : RegistrazionePrimaParteFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegistrazionePrimaParteFragmentBinding.inflate(layoutInflater)

        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(calendar)
        }

        binding.selezionaData.setOnClickListener{
            DatePickerDialog(this.requireContext(), datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        return binding.root
    }

    private fun updateLable(calendar: Calendar) {
        val format = "yyyy-mm-dd"
        val sdf = SimpleDateFormat(format, Locale.US)
        binding.dataEditText.text = sdf.format(calendar.time)
    }

}