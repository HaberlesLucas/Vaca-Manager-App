package com.haberleslucas.asistenciadevacas

import android.os.Parcel
import android.os.Parcelable

class VacaModel() : Parcelable {
    var id_vaca: Int? = null
    var id_color_vaca: Int? = null
    var id_ubicacion: Int? = null
    var nombre_vaca: String? = null
    var fecha_nac: String? = null
    var fecha_preniez: String? = null
    //var foto: Blob? = null
    var foto: ByteArray? = null
    var caravana: String? = null
    var position: Int = -1

    constructor(parcel: Parcel) : this() {
        id_vaca = parcel.readValue(Int::class.java.classLoader) as? Int
        id_color_vaca = parcel.readInt()
        id_ubicacion = parcel.readInt()
        nombre_vaca = parcel.readString()
        fecha_nac = parcel.readString()
        caravana = parcel.readString()
        position = parcel.readInt()
        foto = parcel.createByteArray()
    }

    constructor(id_vaca: Int?,id_color_vaca: Int, id_ubicacion: Int, nombre_vaca: String, fecha_nac: String, caravana: String) : this() {
        this.id_vaca = id_vaca
        this.id_color_vaca=id_color_vaca
        this.id_ubicacion=id_ubicacion
        this.nombre_vaca=nombre_vaca
        this.fecha_nac=fecha_nac
        this.caravana=caravana
    }

    constructor(id_color_vaca: Int, id_ubicacion: Int, nombre_vaca: String, fecha_nac: String, caravana: String) : this() {
        this.id_color_vaca=id_color_vaca
        this.id_ubicacion=id_ubicacion
        this.nombre_vaca=nombre_vaca
        this.fecha_nac=fecha_nac
        this.caravana=caravana
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id_vaca)
        id_color_vaca?.let { parcel.writeInt(it) }
        id_ubicacion?.let { parcel.writeInt(it) }
        parcel.writeString(nombre_vaca)
        parcel.writeString(fecha_nac)
        parcel.writeString(caravana)
        parcel.writeInt(position)
        parcel.writeByteArray(foto)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VacaModel> {
        override fun createFromParcel(parcel: Parcel): VacaModel {
            return VacaModel(parcel)
        }

        override fun newArray(size: Int): Array<VacaModel?> {
            return arrayOfNulls(size)
        }
    }
}