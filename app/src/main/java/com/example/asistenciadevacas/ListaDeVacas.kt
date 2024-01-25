package com.example.asistenciadevacas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class ListaDeVacas : AppCompatActivity() {

    companion object{
        var vacaAdapter: VacaAdapter? = null
        var vacas: MutableList<VacaModel>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_de_vacas)

        val listaVacas = findViewById<RecyclerView>(R.id.rvListaVaca)
        val conexion = ConexionDB(this)
        vacas = conexion.getAllVacas()
        vacaAdapter = VacaAdapter(vacas!!)
        listaVacas.adapter = vacaAdapter

        //añadir vaca
        val btnAniadirVaca = findViewById<Button>(R.id.btnAniadirVaca)
        btnAniadirVaca.setOnClickListener {
            val intent = Intent(this, AniadirVaca::class.java)
            startActivity(intent)
        }
    }
}