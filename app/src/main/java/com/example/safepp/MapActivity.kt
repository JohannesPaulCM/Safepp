package com.example.safepp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapActivity : AppCompatActivity(), OnMapReadyCallback {



    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val objetoIntent1: Intent= intent
        var la = objetoIntent1.getStringExtra("latitud")
        var lo = objetoIntent1.getStringExtra("longitud")


        setup()
        createMapFragment()




    }

    private fun setup() {
        title = "Mapa de Rondin"
    }

    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
    }

    private fun createMarker() {

        val objetoIntent1: Intent= intent
        var la = objetoIntent1.getStringExtra("latitud")
        var lo = objetoIntent1.getStringExtra("longitud")

        var la1 = lo.toDouble()
        var lo1 = la.toDouble()

        val favoritePlace = LatLng(la1 ,lo1)
        map.addMarker(MarkerOptions().position(favoritePlace).title("PUNTO DE CONTROL").icon(
            BitmapDescriptorFactory.fromBitmap(resizeBitmap("icono_2",60,60))
        ).anchor(0.0f, 0.0f))
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(favoritePlace, 18f),
            4000,
            null
        )
    }

    fun resizeBitmap(drawableName: String?, width: Int, height: Int): Bitmap? {
        val imageBitmap = BitmapFactory.decodeResource(
            resources,
            resources.getIdentifier(drawableName, "drawable", packageName)
        )
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }





}