package com.dilerdesenvolv.carros.domain.service

import com.dilerdesenvolv.carros.domain.dao.DBm
import com.dilerdesenvolv.carros.domain.model.Carro

object FavoritosService {

    // Retorna todos os carros favoritados
    fun getCarros(): MutableList<Carro> {
        return DBm.getCarroDAO().findAll()
    }

    fun isFavorito(carro: Carro): Boolean {
        return DBm.getCarroDAO().getById(carro.id!!) != null
    }

    fun save(carro: Carro): Boolean {
        if (isFavorito(carro)) {
            DBm.getCarroDAO().delete(carro)
            return false
        }
        DBm.getCarroDAO().insert(carro)
        return true
    }

}