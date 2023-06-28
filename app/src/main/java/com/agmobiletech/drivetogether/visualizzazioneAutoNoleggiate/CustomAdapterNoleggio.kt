package com.agmobiletech.drivetogether.visualizzazioneAutoNoleggiate

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.agmobiletech.drivetogether.ClientNetwork
import com.agmobiletech.drivetogether.R
import com.agmobiletech.drivetogether.databinding.CardViewCronologiaLayoutBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class CustomAdapterNoleggio(private val mList: ArrayList<ItemsViewModelNoleggio>) : RecyclerView.Adapter<CustomAdapterNoleggio.ViewHolder>(){
    class ViewHolder(binding : CardViewCronologiaLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        val marca = binding.marcaNoleggioCronologiaDaInserire
        val modello = binding.modelloNoleggioCronologiaDaInserire
        val targa = binding.targaNoleggioCronologiaDaInserire
        val immagine = binding.marcaAutoNoleggiataCronologiaImageView
        val inizioNoleggio = binding.inizioNoleggioDaInserire
        val fineNoleggio = binding.fineNoleggioDaInserire
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardViewCronologiaLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModelNoleggio = mList[position]

        holder.marca.text = ItemsViewModelNoleggio.marca
        holder.modello.text = ItemsViewModelNoleggio.modello
        holder.targa.text = ItemsViewModelNoleggio.targa
        val immagineURL = ItemsViewModelNoleggio.immagineURL!!
        restituisciImmagineMarca(immagineURL, holder.immagine)
        holder.inizioNoleggio.text = ItemsViewModelNoleggio.inizioNoleggio
        holder.fineNoleggio.text = ItemsViewModelNoleggio.fineNoleggio
    }

    private fun restituisciImmagineMarca(url: String, imageView: ImageView) {
        ClientNetwork.retrofit.getImage(url).enqueue(
            object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful && response.body() != null) {
                        val immagine = BitmapFactory.decodeStream(response.body()!!.byteStream())
                        imageView.setImageBitmap(immagine)
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    imageView.setImageResource(R.drawable.carmarker)
                }
            }
        )
    }

}