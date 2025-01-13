package com.example.asistenciadevacas

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ListaDeVacas : AppCompatActivity() {

    companion object {
        var vacaAdapter: VacaAdapter? = null
        var vacas: MutableList<VacaModel>? = null
        const val REQUEST_CODE_DETALLE_VACA = 1
    }

    private lateinit var searchView: androidx.appcompat.widget.SearchView

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_de_vacas)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val listaVacas = findViewById<RecyclerView>(R.id.rvListaVaca)
        val conexion = ConexionDB(this)
        vacas = conexion.getAllVacas()
        vacaAdapter = VacaAdapter(vacas!!)
        listaVacas.adapter = vacaAdapter

        val btnAniadirVaca = findViewById<Button>(R.id.btnAniadirVaca)
        btnAniadirVaca.setOnClickListener {
            val intent = Intent(this, AniadirVaca::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETALLE_VACA && resultCode == RESULT_OK) {
            // Recargar las vacas desde la base de datos
            val conexion = ConexionDB(this)
            vacas = conexion.getAllVacas()

            // Actualizar el adaptador con la nueva lista
            vacaAdapter?.updateData(vacas!!)

            // Notificar al adaptador que los datos han cambiado
            vacaAdapter?.notifyDataSetChanged()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_filtro, menu)
        val searchItem = menu.findItem(R.id.menu_buscar)
        searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

        searchView.queryHint = "Buscar vaca por nombre"

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filtrarVacas(newText)
                }
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                vacaAdapter?.restaurarListaOriginal()
                return true
            }
        })

        return true
    }

    private fun restaurarListaCompleta() {
        vacaAdapter?.restaurarListaOriginal()
    }

    private fun filtrarVacas(nombre: String) {
        if (nombre.isEmpty()) {
            restaurarListaCompleta()
            return
        }

        val vacasFiltradas = vacas?.filter {
            it.nombre_vaca?.contains(nombre, ignoreCase = true) == true
        }
        vacaAdapter?.updateData(vacasFiltradas ?: emptyList())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.orden_cargado -> {
                OrdenadorDeVacas.ordenarPorId(vacas!!, { it.id_vaca ?: 0 })
            }
            R.id.orden_az -> {
                OrdenadorDeVacas.ordenarPorNombreAZ(vacas!!, { it.nombre_vaca })
            }
            R.id.orden_za -> {
                OrdenadorDeVacas.ordenarPorNombreZA(vacas!!, { it.nombre_vaca })
            }
        }
        vacaAdapter?.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}
