package com.haberleslucas.asistenciadevacas

import android.os.Parcel
import android.os.Parcelable

class NuevoModelVaca() : Parcelable {
    var id_vaca: Int? = null
    var nombre_vaca: String? = null
    var estado: Boolean = false  // Propiedad para el Switch

    constructor(parcel: Parcel) : this() {
        id_vaca = parcel.readValue(Int::class.java.classLoader) as? Int
        nombre_vaca = parcel.readString()
        estado = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id_vaca)
        parcel.writeString(nombre_vaca)
        parcel.writeByte(if (estado) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NuevoModelVaca> {
        override fun createFromParcel(parcel: Parcel): NuevoModelVaca {
            return NuevoModelVaca(parcel)
        }

        override fun newArray(size: Int): Array<NuevoModelVaca?> {
            return arrayOfNulls(size)
        }
    }
}