package com.dilerdesenvolv.carros.views.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.utils.PermissionUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.act

class MapaFragment : BaseFragment(), OnMapReadyCallback {

    // Obj que controla o Google Maps
    private var map: GoogleMap? = null
    private val carro: Carro by lazy { arguments!!.getParcelable<Carro>("carro") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mapa, container, false)
        // inicia o mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    // Chamado quando a inicialização do mapa estiver ok
    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        // MOstrar a localização do usuario apenas para carros com lat/long 0
        if (carro.latitude?.toDouble() == 0.0) {
            if (PermissionUtils.validate(activity!!.act, 1,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                map?.isMyLocationEnabled = true
            }
        } else {
            val location = LatLng(carro.latitude!!.toDouble(), carro.longitude!!.toDouble())
            // Posiciona o mapa na coordenada zoom = 13
            map?.apply {
                moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                // Marcador local
                addMarker(MarkerOptions()
                        .title(carro.nome)
                        .snippet(carro.desc)
                        .position(location))
            }
        }
        // Tipo: normal, satétile, terreno ou híbrido
        map?.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

        @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                map?.isMyLocationEnabled = true

                return
            }
        }
    }

}
