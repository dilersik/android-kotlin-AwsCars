package com.dilerdesenvolv.carros.domain.dao

import android.arch.persistence.room.Room
import com.dilerdesenvolv.carros.CarrosApplication

object DBm {

    // Singleton Room: banco de dados
    private var dbInstance: CarrosDatabase

    init {
        val appContext = CarrosApplication.getInstance().applicationContext
        // Configura o Room
        dbInstance = Room.databaseBuilder(appContext, CarrosDatabase::class.java, "carros.sqlite")
                //.fallbackToDestructiveMigration()
                .build()
    }

    fun getCarroDAO(): CarroDAO {
        return dbInstance.carroDAO()
    }

}