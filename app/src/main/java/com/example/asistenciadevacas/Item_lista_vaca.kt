package com.example.asistenciadevacas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class Item_lista_vaca : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_lista_vaca)

        // Obtener referencia al ConstraintLayout
        val itemContainer = findViewById<ConstraintLayout>(R.id.itemContainer)

        // Establecer el listener de clic en_todo el contenedor
        itemContainer.setOnClickListener {
            val intent = Intent(this, DetalleVaca::class.java)
            startActivity(intent)
        }
    }
}

