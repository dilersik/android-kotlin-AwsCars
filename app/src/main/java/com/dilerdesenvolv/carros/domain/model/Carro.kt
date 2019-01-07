package com.dilerdesenvolv.carros.domain.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

// Parcelable mais performático que Serializable
@Entity(tableName = "carros")
class Carro() : Parcelable {

    @PrimaryKey
    var id:Long? = null
    var date_post:String? = null
    var date_update:String? = null
    var tipo:String? = null
    var nome:String? = null
    var desc:String? = null
//   se no Json for !=, usar @SerializedName("url_foto")
    var urlFoto:String? = null
    var urlInfo:String? = null
    var urlVideo:String? = null
    var latitude:String? = null
        get() = if (field.isNullOrEmpty()) "0.0" else field
    var longitude:String? = null
        get() = if (field.isNullOrEmpty()) "0.0" else field

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        date_post = parcel.readString()
        date_update = parcel.readString()
        tipo = parcel.readString()
        nome = parcel.readString()
        desc = parcel.readString()
        urlFoto = parcel.readString()
        urlInfo = parcel.readString()
        urlVideo = parcel.readString()
        latitude = parcel.readString()
        longitude = parcel.readString()
    }

    override fun toString(): String {
        return "Carro(nome='$nome')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(date_post)
        parcel.writeString(date_update)
        parcel.writeString(tipo)
        parcel.writeString(nome)
        parcel.writeString(desc)
        parcel.writeString(urlFoto)
        parcel.writeString(urlInfo)
        parcel.writeString(urlVideo)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Carro> {
        override fun createFromParcel(parcel: Parcel): Carro {
            return Carro(parcel)
        }

        override fun newArray(size: Int): Array<Carro?> {
            return arrayOfNulls(size)
        }
    }

}