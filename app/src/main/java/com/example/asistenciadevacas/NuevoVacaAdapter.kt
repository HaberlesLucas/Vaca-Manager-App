package com.example.asistenciadevacas

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NuevoVacaAdapter(val vacas: List<NuevoModelVaca>) : RecyclerView.Adapter<NuevoVacaAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreVaca: TextView = view.findViewById(R.id.vaca_nombre_asiste)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val switchEstado: Switch = view.findViewById(R.id.swAsiste)

        fun bind(vaca: NuevoModelVaca) {
            nombreVaca.text = vaca.nombre_vaca
            switchEstado.isChecked = vaca.estado
            switchEstado.setOnCheckedChangeListener { _, isChecked ->
                //aca podría hacer algo cuando el sw cambie de estado
                vaca.estado = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_vaca_asistencia, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = vacas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vaca = vacas[position]
        holder.bind(vaca)
    }
}
