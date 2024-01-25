package com.example.asistenciadevacas
import android.content.ContentValues
import android.content.Context

class ConexionDB {
    var conexion: SQLite
    constructor(context: Context){
        conexion = SQLite(context, "dbH", null, 1)
    }

    //guardar vaca
    fun guardarVaca(vaca:VacaModel): Long? {
        var db = conexion.writableDatabase

        val values = ContentValues().apply {
            put("id_color_vaca", vaca.id_color_vaca)
            put("id_ubicacion", vaca.id_ubicacion)
            put("nombre_vaca", vaca.nombre_vaca)
            put("fecha_nac", vaca.fecha_nac)
            put("fecha_preniez", vaca.fecha_preniez)
            put("caravana", vaca.caravana)
        }
        return db?.insert("vaca", "foto", values)

    }

    fun editarVaca(vaca:VacaModel){
        var db = conexion.writableDatabase
        val selection = "id_vaca = " + vaca.id_vaca.toString()
        val values = ContentValues().apply {
            put("id_color_vaca", vaca.id_color_vaca)
            put("id_ubicacion", vaca.id_ubicacion)
            put("nombre_vaca", vaca.nombre_vaca)
            put("fecha_nac", vaca.fecha_nac)
            put("fecha_preniez", vaca.fecha_preniez)
            put("caravana", vaca.caravana)
        }
        println(db.update("vaca", values, selection, null))
    }

    fun getAllVacas(): MutableList<VacaModel> {
        val db = conexion.readableDatabase

        val cursor = db.query(
            "vaca",
            null,
            null,
            null,
            null,
            null,
            null
        )

        val vacas = mutableListOf<VacaModel>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_vaca"))
            val id_color = cursor.getInt(cursor.getColumnIndexOrThrow("id_color_vaca"))
            val id_ubicacion = cursor.getInt(cursor.getColumnIndexOrThrow("id_ubicacion"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_vaca"))
            val nacimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nac"))
            val preniez = cursor.getString(cursor.getColumnIndexOrThrow("fecha_preniez"))
            val caravana = cursor.getString(cursor.getColumnIndexOrThrow("caravana"))

            vacas.add(VacaModel(id,id_color, id_ubicacion, nombre, nacimiento, preniez, caravana))
        }
        cursor.close()
        return vacas
    }

    fun eliminarVaca(idVaca: Int?){
        val db = conexion.writableDatabase
        val selection = "id_vaca = " + idVaca.toString()
        val selectionArgs = arrayOf(idVaca.toString())
        val deletedRows = db.delete("vaca", selection, null)
        println(deletedRows)
    }

    fun getAllNuevasVacas(): MutableList<NuevoModelVaca> {
        val db = conexion.readableDatabase

        val cursor = db.query(
            "vaca",
            null,
            null,
            null,
            null,
            null,
            null
        )

        val nuevasVacas = mutableListOf<NuevoModelVaca>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_vaca"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_vaca"))

            // Crear un NuevoModelVaca utilizando el constructor sin argumentos
            val nuevoModel = NuevoModelVaca()
            nuevoModel.id_vaca = id
            nuevoModel.nombre_vaca = nombre

            nuevasVacas.add(nuevoModel)
        }
        cursor.close()
        return nuevasVacas
    }
}