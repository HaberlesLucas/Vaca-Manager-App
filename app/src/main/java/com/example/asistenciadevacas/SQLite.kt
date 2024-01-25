package com.example.asistenciadevacas
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLite(
    context: Context?,
    dbH: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, dbH, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        //tabla COLOR
        db?.execSQL("""
            CREATE TABLE color (
                id_color_vaca INTEGER PRIMARY KEY AUTOINCREMENT, 
                descripcion VARCHAR(30)
            )
        """)
        //tabla UBICACIÖN
        db?.execSQL( """
            CREATE TABLE ubicacion (
                id_ubicacion INTEGER PRIMARY KEY AUTOINCREMENT, 
                descripcion_ubicacion VARCHAR(50)
            )
        """)
        //tabla VACA
        db?.execSQL("""
            CREATE TABLE vaca ( 
                id_vaca INTEGER PRIMARY KEY AUTOINCREMENT, 
                id_color_vaca INTEGER, 
                id_ubicacion INTEGER, 
                nombre_vaca TEXT, 
                fecha_nac TEXT, 
                fecha_preniez TEXT, 
                foto BLOB, 
                caravana VARCHAR(10),
                FOREIGN KEY (id_color_vaca) REFERENCES color(id_color_vaca),
                FOREIGN KEY (id_ubicacion) REFERENCES ubicacion(id_ubicacion)                
            )
        """)
        //tabla ASISTENCIA
        db?.execSQL("""
            CREATE TABLE asistencia(
                id_asistencia INTEGER PRIMARY KEY AUTOINCREMENT, 
                id_vaca INTEGER, 
                id_ubicacion INTEGER, 
                fecha_asistencia TEXT,
                FOREIGN KEY (id_vaca) REFERENCES vaca(id_vaca),
                FOREIGN KEY (id_ubicacion) REFERENCES ubicacion(id_ubicacion)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }

}