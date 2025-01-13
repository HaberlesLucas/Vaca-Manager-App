package com.example.asistenciadevacas
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class VacaAdapter(initialVacas: MutableList<VacaModel>) : RecyclerView.Adapter<VacaAdapter.ViewHolder>() {

    private var vacasOriginal: List<VacaModel> = initialVacas.toList()
    private var vacasFiltradas: MutableList<VacaModel> = initialVacas

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreVaca: TextView = view.findViewById(R.id.vaca_nombre)

        fun bind(vaca: VacaModel) {
            nombreVaca.text = vaca.nombre_vaca
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_lista_vaca, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = vacasFiltradas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vaca = vacasFiltradas[position]
        vaca.position = position

        val itemContainer = holder.itemView.findViewById<ConstraintLayout>(R.id.itemContainer)
        itemContainer.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetalleVaca::class.java)
            intent.putExtra("id_vaca", vaca.id_vaca)
            (holder.itemView.context as AppCompatActivity).startActivityForResult(intent, ListaDeVacas.REQUEST_CODE_DETALLE_VACA)
        }

        holder.bind(vaca)
    }

    fun ordenarPosiciones() {
        for (i in vacasFiltradas.indices) {
            vacasFiltradas[i].position = i
        }
    }

    fun updateData(nuevasVacas: List<VacaModel>) {
        vacasFiltradas = nuevasVacas.toMutableList()
        ordenarPosiciones()
        notifyDataSetChanged()
    }

    fun restaurarListaOriginal() {
        vacasFiltradas = vacasOriginal.toMutableList()
        ordenarPosiciones()
        notifyDataSetChanged()
    }
}
