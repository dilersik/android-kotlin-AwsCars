package com.dilerdesenvolv.carros.domain.event

import com.dilerdesenvolv.carros.domain.model.Carro

data class SaveCarroEvent(val carro: Carro)
data class FavoritoEvent(val carro: Carro)
