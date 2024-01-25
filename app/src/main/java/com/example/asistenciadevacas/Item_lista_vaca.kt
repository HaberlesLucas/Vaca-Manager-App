package com.example.asistenciadevacas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Item_lista_vaca : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_lista_vaca)

        val btnVerDetalle = findViewById<Button>(R.id.btnVerDetalle)
        btnVerDetalle.setOnClickListener {
            val intent = Intent(this, DetalleVaca::class.java)
            startActivity(intent)
        }
    }
}