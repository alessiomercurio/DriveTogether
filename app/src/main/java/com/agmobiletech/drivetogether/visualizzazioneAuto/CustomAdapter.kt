package com.agmobiletech.drivetogether.visualizzazioneAuto

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agmobiletech.drivetogether.databinding.CardViewLayoutBinding
import com.squareup.picasso.Picasso

class CustomAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){
    private var onClickListener: OnClickListener? = null
    class ViewHolder(binding : CardViewLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        val marca = binding.marcaDaInserire
        val modello = binding.modelloDaInserire
        val targa = binding.targaDaInserire
        val immagine = binding.immagineMarcaImageView
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

        holder.marca.text = ItemsViewModel.marca
        holder.modello.text = ItemsViewModel.modello
        holder.targa.text = ItemsViewModel.targa
        holder.immagine.setImageResource(ItemsViewModel.immagineMarca)

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, ItemsViewModel)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, model: ItemsViewModel)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener }

}