package com.semid.gpscallback

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.semid.gps.GpsBuilder
import com.semid.gpscallback.databinding.FragmentGpsBinding

@SuppressLint("SetTextI18n")
class GpsFragment : Fragment() {
    private val binding: FragmentGpsBinding by lazy {
        FragmentGpsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        initLocation()

        return binding.root
    }

    private fun initLocation() {
        val manager = GpsBuilder(this)
                .build()

        manager.onNewLocationAvailable = { lat: Double, lon: Double ->
            binding.newLocationTxt.text = "New location : $lat,$lon"
        }

        manager.connect()
    }
}