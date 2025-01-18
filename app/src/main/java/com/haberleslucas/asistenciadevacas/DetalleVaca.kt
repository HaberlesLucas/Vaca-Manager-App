package com.haberleslucas.asistenciadevacas

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetalleVaca : AppCompatActivity() {

    companion object {
        var refrescar = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_vaca)
        mostrarDetalle()
        invalidateOptionsMenu()
    }

    override fun onResume() {
        super.onResume()
        if (refrescar == 1) {
            this.mostrarDetalle()
            refrescar = 0
        }
    }

    @SuppressLint("SetTextI18n", "WrongViewCast")
    fun mostrarDetalle() {
        val conexion = ConexionDB(this)

        val nombreVaca = findViewById<TextView>(R.id.txtNombreVaca)
        val nacimientoVaca = findViewById<TextView>(R.id.txtFechaNacimiento)
        val caravanaVaca = findViewById<TextView>(R.id.txtNroCaravana)
        val ubicacionVaca = findViewById<TextView>(R.id.SnnUbicacion)
        val colorVaca = findViewById<TextView>(R.id.SnnColor)
        val imgFotoVaca = findViewById<ImageView>(R.id.imgFotoVaca)

        val idVaca = intent.getIntExtra("id_vaca", -1)
        val vaca = ListaDeVacas.vacas?.find { it.id_vaca == idVaca }

        if (vaca != null) {
            nombreVaca.text = "Nombre: ${vaca.nombre_vaca}"
            caravanaVaca.text = "${vaca.caravana}"
            nacimientoVaca.text = vaca.fecha_nac
            ubicacionVaca.text = ColoresUbicaciones.ubicaciones[vaca.id_ubicacion!!]
            colorVaca.text = ColoresUbicaciones.colores[vaca.id_color_vaca!!]
            if (vaca.foto != null) {
                val bitmap = BitmapFactory.decodeByteArray(vaca.foto, 0, vaca.foto!!.size)
                imgFotoVaca.setImageBitmap(bitmap)
            } else {
                // Mostrar imagen por defecto
                imgFotoVaca.setImageResource(R.drawable.default_img_vaca)
            }
            val btnEliminarVaca = findViewById<Button>(R.id.btnEliminarVaca)
            btnEliminarVaca.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Atención")
                builder.setMessage("Seguro desea eliminar a: ${vaca.nombre_vaca}")
                builder.setPositiveButton("Aceptar") { dialog, which ->
                    // Eliminar la vaca de la base de datos
                    conexion.eliminarVaca(vaca.id_vaca)

                    // Eliminar la vaca de la lista en ListaDeVacas
                    val position = ListaDeVacas.vacas?.indexOfFirst { it.id_vaca == vaca.id_vaca } ?: -1
                    if (position != -1) {
                        ListaDeVacas.vacas?.removeAt(position)
                        // Notificar al adaptador que la vaca ha sido eliminada
                        ListaDeVacas.vacaAdapter?.updateData(ListaDeVacas.vacas!!)
                        ListaDeVacas.vacaAdapter?.notifyItemRemoved(position)
                    }

                    // Mostrar mensaje y cerrar actividad
                    val mensaje = "Se eliminó a: ${vaca.nombre_vaca?.uppercase()}"
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

                    setResult(RESULT_OK) // Informar que se eliminó la vaca
                    finish() // Volver a la actividad principal
                }
                builder.setNegativeButton("Cancelar") { dialog, which ->
                    val mensaje = "No se eliminó a: ${vaca.nombre_vaca?.uppercase()}"
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                }
                builder.create().show()
            }

            val btnEditarVaca = findViewById<Button>(R.id.btnEditarVaca)
            btnEditarVaca.setOnClickListener {
                val intent = Intent(this, AniadirVaca::class.java)
                intent.putExtra("editar", true)
                intent.putExtra("vaca", vaca)
                // Si la vaca tiene foto, se pasa también la foto
                vaca.foto?.let {
                    intent.putExtra("foto_vaca", it)  // Pasamos la foto (byte array)
                }
                startActivity(intent)
            }
            imgFotoVaca.setOnClickListener {
                val vacaFoto = vaca.foto
                if (vacaFoto != null) {
                    val bitmap = BitmapFactory.decodeByteArray(vacaFoto, 0, vacaFoto.size)
                    showImageDialog(bitmap)
                } else {
                    Toast.makeText(this, "No hay imagen disponible", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No se encontró la vaca", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    private fun showImageDialog(bitmap: Bitmap) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_img_fullscreen, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.imgFullscreen)

        // Configura la imagen
        imageView.setImageBitmap(bitmap)

        // Crea el diálogo
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Muestra el diálogo
        dialog.show()

        // Ajustar el tamaño del diálogo al de la imagen
        val window = dialog.window
        window?.setBackgroundDrawableResource(android.R.color.transparent) // Fondo transparente

        // Obtén el tamaño de la imagen
        imageView.post {
            val width = imageView.drawable.intrinsicWidth
            val height = imageView.drawable.intrinsicHeight
            window?.setLayout(width, height) // Ajusta el tamaño del diálogo
        }
    }



}
