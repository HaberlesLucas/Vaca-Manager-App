package com.haberleslucas.asistenciadevacas

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.TouchDelegate
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaDeVacasAsistidas : AppCompatActivity() {
    companion object {
        var nuevoVacaAdapter: NuevoVacaAdapter? = null
        var nuevasVacas: MutableList<NuevoModelVaca>? = null
        var listaOriginal: MutableList<NuevoModelVaca>? = null
    }

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_de_vacas_asiste)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // No rotar pantalla
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val listaVacas = findViewById<RecyclerView>(R.id.rvListaVacaAsiste)
        nuevasVacas = ConexionDB(this).getAllNuevasVacas()
        listaOriginal = ArrayList(nuevasVacas) // Guardar copia de la lista original

        nuevoVacaAdapter = NuevoVacaAdapter(nuevasVacas!!)
        listaVacas.layoutManager = LinearLayoutManager(this)
        listaVacas.adapter = nuevoVacaAdapter
    }

    // Inflar el menú con SearchView
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filtro, menu)

        val searchItem = menu?.findItem(R.id.menu_buscar)
        val searchView = searchItem?.actionView as SearchView
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.black))
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.gray))

        searchView.queryHint = "Buscar vaca por nombre"

        // Listener para texto del buscador
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

        // Listener para abrir/cerrar el SearchView
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                restaurarListaCompleta()
                return true
            }
        })

        val closeButton = searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn)
        closeButton?.let { btn ->
            val parent = btn.parent as View
            parent.post {
                val rect = Rect()
                btn.getHitRect(rect)
                rect.inset(-8, -8) // Aumenta el área táctil en 8dp
                parent.touchDelegate = TouchDelegate(rect, btn)
            }
        }
        return true
    }

    // Filtrar vacas según el texto ingresado
    private fun filtrarVacas(nombre: String) {
        if (nombre.isEmpty()) {
            restaurarListaCompleta()
            return
        }

        val vacasFiltradas = listaOriginal?.filter {
            it.nombre_vaca?.contains(nombre, ignoreCase = true) == true
        }

        nuevasVacas?.clear()
        nuevasVacas?.addAll(vacasFiltradas ?: emptyList())
        nuevoVacaAdapter?.notifyDataSetChanged()
    }

    // Restaurar la lista original
    private fun restaurarListaCompleta() {
        nuevasVacas?.clear()
        nuevasVacas?.addAll(listaOriginal!!)
        nuevoVacaAdapter?.notifyDataSetChanged()
    }

    // Manejar las opciones del menú (ordenar)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.orden_cargado -> {
                OrdenadorDeVacas.ordenarPorId(nuevasVacas!!, { it.id_vaca ?: 0 })
            }
            R.id.orden_az -> {
                OrdenadorDeVacas.ordenarPorNombreAZ(nuevasVacas!!, { it.nombre_vaca })
            }
            R.id.orden_za -> {
                OrdenadorDeVacas.ordenarPorNombreZA(nuevasVacas!!, { it.nombre_vaca })
            }
        }
        nuevoVacaAdapter?.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}
