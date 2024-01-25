package com.example.asistenciadevacas

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaDeVacasAsistidas : AppCompatActivity() {
    companion object {
        var nuevoVacaAdapter: NuevoVacaAdapter? = null
        var nuevasVacas: MutableList<NuevoModelVaca>? = null
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_de_vacas_asiste)

        val listaVacas = findViewById<RecyclerView>(R.id.rvListaVacaAsiste)
        nuevasVacas = ConexionDB(this).getAllNuevasVacas()
        nuevoVacaAdapter = NuevoVacaAdapter(nuevasVacas!!)

        listaVacas.layoutManager = LinearLayoutManager(this)
        listaVacas.adapter = nuevoVacaAdapter
    }
}