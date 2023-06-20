package com.agmobiletech.drivetogether.visualizzazioneAuto

import android.os.Bundle

import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.agmobiletech.drivetogether.BottomNavigationManager
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.ActivityVisualizzazioneAutoBinding

class VisualizzazioneAutoActivity : AppCompatActivity(){
    lateinit var binding : ActivityVisualizzazioneAutoBinding
    private lateinit var navigationManager: BottomNavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizzazioneAutoBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationBar
        bottomNavigationView.selectedItemId = R.id.autoMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val data = ArrayList<ItemsViewModel>()
        for (i in 1..20){
            data.add(ItemsViewModel("{marca}", "{modello}", "{targa}", R.drawable.euro))
        }
        val adapter = CustomAdapter(data)
        binding.recyclerView.adapter = adapter
    }

}