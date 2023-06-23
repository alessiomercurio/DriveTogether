package com.agmobiletech.drivetogether.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.agmobiletech.drivetogether.R
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.agmobiletech.drivetogether.databinding.MapFragmentBinding

class MapFragment : Fragment(R.layout.map_fragment) {

    lateinit var binding : MapFragmentBinding
    var mapView: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MapFragmentBinding.inflate(inflater)

        mapView = view?.findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}