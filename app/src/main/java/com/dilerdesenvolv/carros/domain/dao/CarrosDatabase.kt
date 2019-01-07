package com.dilerdesenvolv.carros.domain.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.dilerdesenvolv.carros.domain.model.Carro

// define as classes que precisam ser persistidas e a versão do banco

@Database(entities = arrayOf(Carro::class), version = 1)
abstract class CarrosDatabase: RoomDatabase() {
    abstract fun carroDAO(): CarroDAO
}

