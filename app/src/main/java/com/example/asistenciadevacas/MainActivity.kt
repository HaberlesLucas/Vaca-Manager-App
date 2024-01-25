package com.example.asistenciadevacas
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnVerListaVacas = findViewById<Button>(R.id.btnVerListaVacas)
        val btnTomarAsistenciaVacas = findViewById<Button>(R.id.btnTomarAsitenciaVacas)

        btnVerListaVacas.setOnClickListener {
            val intent = Intent(this, ListaDeVacas::class.java)
            startActivity(intent)
        }

        btnTomarAsistenciaVacas.setOnClickListener {
            val intent = Intent(this, ListaDeVacasAsistidas::class.java)
            startActivity(intent)
        }
    }
}