package com.haberleslucas.asistenciadevacas

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.util.Calendar

class AniadirVaca : AppCompatActivity() {

    lateinit var arrayColores: Spinner
    lateinit var arrayUbicaciones: Spinner
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageBytes: ByteArray? = null

    @SuppressLint("CutPasteId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aniadir_vaca)

        //no rotate pantasha
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //CONEXION A LA DB
        val conexion = ConexionDB(this)

        val txtNombreVaca = findViewById<EditText>(R.id.txtNombreVaca)
        val txtNroCaravana = findViewById<EditText>(R.id.txtNroCaravana)
        val btnSeleccionarFoto = findViewById<Button>(R.id.btnSeleccionarFoto)
        val imgVaca = findViewById<ImageView>(R.id.imgVaca)
        val imgEliminarFoto = findViewById<ImageView>(R.id.imgEliminarFoto)

        // Ocultar la "X" inicialmente
        imgEliminarFoto.visibility = ImageView.GONE

        // Seleccionar una foto
        btnSeleccionarFoto.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST)
        }

        // Eliminar la foto seleccionada
        imgEliminarFoto.setOnClickListener {
            imgVaca.setImageResource(R.drawable.default_img_vaca) // Imagen predeterminada
            imgEliminarFoto.visibility = ImageView.GONE // Ocultar la "X"
            selectedImageBytes = null // Limpiar la imagen seleccionada
        }

        /* INICIO PARA FECHAS */
        val txtFechaNacimiento = findViewById<EditText>(R.id.txtFechaNacimiento)
        txtFechaNacimiento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, y, m, d ->
                    val fecha = String.format("%02d/%02d/%04d", d, m + 1, y)
                    txtFechaNacimiento.setText(fecha)
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        }
        txtFechaNacimiento.keyListener = null
        // FIN PARA FECHAS

        val listaColores = ColoresUbicaciones.colores
        val listaUbicaciones = ColoresUbicaciones.ubicaciones

        val SnnColor = findViewById<Spinner>(R.id.SnnColor)
        val SnnUbicacion = findViewById<Spinner>(R.id.SnnUbicacion)

        val adaptadorColores = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaColores)
        val adaptadorUbicaciones = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaUbicaciones)

        SnnColor.adapter = adaptadorColores
        SnnUbicacion.adapter = adaptadorUbicaciones

        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val intent = this.intent
        val editar = intent.getBooleanExtra("editar", false)

        if (editar) {
            val vaca = intent.getParcelableExtra<VacaModel>("vaca")

            if (vaca != null) {
                txtNombreVaca.setText(vaca.nombre_vaca)
                txtFechaNacimiento.setText(vaca.fecha_nac)
                txtNroCaravana.setText(vaca.caravana)
                vaca.id_color_vaca?.let { SnnColor.setSelection(it) }
                vaca.id_ubicacion?.let { SnnUbicacion.setSelection(it) }

                // Mostrar la foto si existe
                vaca.foto?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    imgVaca.setImageBitmap(bitmap)
                    selectedImageBytes = it
                    imgEliminarFoto.visibility = ImageView.VISIBLE // Mostrar la "X"
                }
            }

            btnGuardar.setOnClickListener {
                if (vaca != null && txtNombreVaca.text.toString().trim().isNotEmpty()) {
                    vaca.id_ubicacion = SnnUbicacion.selectedItemPosition
                    vaca.id_color_vaca = SnnColor.selectedItemPosition
                    vaca.nombre_vaca = txtNombreVaca.text.toString()
                    vaca.fecha_nac = txtFechaNacimiento.text.toString()
                    vaca.caravana = txtNroCaravana.text.toString()
                    vaca.foto = selectedImageBytes

                    conexion.editarVaca(vaca)
                    ListaDeVacas.vacas!![vaca.position] = vaca
                    ListaDeVacas.vacaAdapter!!.notifyItemChanged(vaca.position)
                    DetalleVaca.refrescar = 1
                    finish()
                } else {
                    mostrarDialogoError()
                }
            }

        } else {
            btnGuardar.setOnClickListener {
                if (txtNombreVaca.text.toString().trim().isNotEmpty()) {
                    val vacaNueva = VacaModel(
                        SnnColor.selectedItemPosition,
                        SnnUbicacion.selectedItemPosition,
                        txtNombreVaca.text.toString(),
                        txtFechaNacimiento.text.toString(),
                        txtNroCaravana.text.toString()
                    )
                    vacaNueva.foto = selectedImageBytes

                    val idVaca = conexion.guardarVaca(vacaNueva)
                    vacaNueva.id_vaca = idVaca!!.toInt()

                    txtNombreVaca.setText("")
                    txtFechaNacimiento.setText("")
                    txtNroCaravana.setText("")
                    SnnColor.setSelection(0)
                    SnnUbicacion.setSelection(0)
                    imgVaca.setImageResource(R.drawable.default_img_vaca)
                    imgEliminarFoto.visibility = ImageView.GONE
                    selectedImageBytes = null

                    ListaDeVacas.vacas!!.add(vacaNueva)
                    ListaDeVacas.vacaAdapter!!.notifyItemInserted(ListaDeVacas.vacas!!.lastIndex)
                    finish()
                } else {
                    mostrarDialogoError()
                }
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val bytes = inputStream?.readBytes()

                if (bytes != null) {
                    selectedImageBytes = comprimirImagen(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(selectedImageBytes, 0, selectedImageBytes!!.size)
                    findViewById<ImageView>(R.id.imgVaca).setImageBitmap(bitmap)
                    findViewById<ImageView>(R.id.imgEliminarFoto).visibility = ImageView.VISIBLE
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun comprimirImagen(bytes: ByteArray): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val maxSize = 800
        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (ratio > 1) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }

        val bitmapRedimensionado = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        val outputStream = ByteArrayOutputStream()
        bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return outputStream.toByteArray()
    }

    fun mostrarDialogoError() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setTitle("ATENCIÓN")
        val messageText = SpannableString("INGRESE UN NOMBRE VÁLIDO.")
        messageText.setSpan(ForegroundColorSpan(Color.WHITE), 0, messageText.length, 0)
        builder.setMessage(messageText)
        builder.setPositiveButton("Aceptar") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }
}