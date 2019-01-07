package com.dilerdesenvolv.carros.domain.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.dilerdesenvolv.carros.domain.model.Carro

/**
 * Created by dilerdesenvolv on 30/08/2017.
 */
@Dao
interface CarroDAO {

    @Query("SELECT * FROM carros where id = :id")
    fun getById(id: Long): Carro?

    @Query("SELECT * FROM carros")
    fun findAll(): MutableList<Carro>

    @Insert
    fun insert(carro: Carro)

    @Delete
    fun delete(carro: Carro)

}