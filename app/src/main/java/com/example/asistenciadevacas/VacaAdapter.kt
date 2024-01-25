package com.example.asistenciadevacas
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class VacaAdapter(val vacas: List<VacaModel>)  : RecyclerView.Adapter<VacaAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
        val nombreVaca: TextView = view.findViewById(R.id.vaca_nombre)
        fun bind(vaca: VacaModel){
            nombreVaca.text = vaca.nombre_vaca
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_lista_vaca, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = vacas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vaca = vacas[position]
        vaca.position = position

        val btnDetalle = holder.itemView.findViewById<Button>(R.id.btnVerDetalle)
        btnDetalle.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetalleVaca::class.java)
            intent.putExtra("position", position)
            startActivity(holder.itemView.context,intent, null)
        }
        holder.bind(vaca)
    }

    fun ordenarPosiciones(){
        for (i in 0..vacas.size-1) {
            vacas[i].position = i
        }
    }
}