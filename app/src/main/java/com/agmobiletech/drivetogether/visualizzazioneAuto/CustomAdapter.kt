package com.agmobiletech.drivetogether.visualizzazioneAuto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agmobiletech.drivetogether.databinding.CardViewLayoutBinding

class CustomAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

    class ViewHolder(binding : CardViewLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        val immagine = binding.immagineMarcaImageView
        val marca = binding.marcaDaInserire
        val modello = binding.modelloDaInserire
        val targa = binding.targaDaInserire
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardViewLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.immagine.setImageResource(ItemsViewModel.immagineMarca)
        holder.marca.setText(ItemsViewModel.marca)
        holder.modello.setText(ItemsViewModel.modello)
        holder.targa.setText(ItemsViewModel.targa)
    }

}