package com.example.asistenciadevacas

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AniadirVaca : AppCompatActivity() {

    lateinit var arrayColores: Spinner
    lateinit var arrayUbicaciones: Spinner

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aniadir_vaca)

        //CONEXION A LA DB
        val conexion = ConexionDB(this)

        val txtNombreVaca = findViewById<EditText>(R.id.txtNombreVaca)
        val txtNroCaravana = findViewById<EditText>(R.id.txtNroCaravana)

        /*INICIO PARA FECHAS*/
        val txtFechaNacimiento = findViewById<EditText>(R.id.txtFechaNacimiento)
        txtFechaNacimiento.setOnClickListener{
            // Obtener la fecha actual
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            // Crear el DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, y, m, d ->
                    // Formatear la fecha seleccionada
                    val fecha = String.format("%02d/%02d/%04d", d, m + 1, y)
                    // Mostrar la fecha en el EditText
                    txtFechaNacimiento.setText(fecha)
                }, year, month, dayOfMonth
            )
            // Mostrar el DatePickerDialog
            datePickerDialog.show()
        }
        txtFechaNacimiento.keyListener = null

        val txtFechaPreniez = findViewById<EditText>(R.id.txtFechaPreniez)
        txtFechaPreniez.setOnClickListener{
            // Obtener la fecha actual
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            // Crear el DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, y, m, d ->
                    // Formatear la fecha seleccionada
                    val fecha = String.format("%02d/%02d/%04d", d, m + 1, y)
                    // Mostrar la fecha en el EditText
                    txtFechaPreniez.setText(fecha)
                }, year, month, dayOfMonth
            )
            // Mostrar el DatePickerDialog
            datePickerDialog.show()
        }
        txtFechaPreniez.keyListener = null
        /*FIN PARA FECHAS*/

        val listaColores = ColoresUbicaciones.colores
        val listaUbicaciones = ColoresUbicaciones.ubicaciones


        val SnnColor = findViewById<Spinner>(R.id.SnnColor)
        val SnnUbicacion = findViewById<Spinner>(R.id.SnnUbicacion)

        arrayColores = findViewById(R.id.SnnColor)
        arrayUbicaciones = findViewById(R.id.SnnUbicacion)

        val adaptadorColores = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,listaColores)
        val adaptadorUbicaciones = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,listaUbicaciones)

        arrayColores.adapter=adaptadorColores
        arrayUbicaciones.adapter=adaptadorUbicaciones

        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val intent = this.intent
        val editar = intent.getBooleanExtra("editar", false)

        if(editar){
            val vaca = intent.getParcelableExtra<VacaModel>("vaca")

            if (vaca != null) {
                txtNombreVaca.setText(vaca.nombre_vaca)
                txtFechaNacimiento.setText(vaca.fecha_nac)
                txtFechaPreniez.setText(vaca.fecha_preniez)
                txtNroCaravana.setText(vaca.caravana)
                vaca.id_color_vaca?.let { SnnColor.setSelection(it) }
                vaca.id_ubicacion?.let { SnnUbicacion.setSelection(it) }
            }

            btnGuardar.setOnClickListener{
                if (vaca != null) {
                    vaca.id_ubicacion = SnnUbicacion.selectedItemPosition
                    vaca.id_color_vaca = SnnColor.selectedItemPosition
                    vaca.nombre_vaca = txtNombreVaca.text.toString()
                    vaca.fecha_nac = txtFechaNacimiento.text.toString()
                    vaca.fecha_preniez = txtFechaPreniez.text.toString()
                    vaca.caravana = txtNroCaravana.text.toString()
                    conexion.editarVaca(vaca)
                    //hay que hacer una funcion de esto
                    ListaDeVacas.vacas!![vaca.position] = vaca
                    ListaDeVacas.vacaAdapter!!.notifyItemChanged(vaca.position)
                    DetalleVaca.refrescar = 1
                }
                finish()
            }
            btnCancelar.setOnClickListener{
                finish()
            }
        }else{
            btnCancelar.setOnClickListener{

                txtNombreVaca.setText("")
                txtFechaNacimiento.setText("")
                txtFechaPreniez.setText("")
                txtNroCaravana.setText("")
                SnnColor.setSelection(0)
                SnnUbicacion.setSelection(0)
                finish()
            }

            btnGuardar.setOnClickListener{
                if((txtNombreVaca.text.toString() != "")){
                    //llamar a la conexion db
                    val vacaNueva = VacaModel(SnnColor.selectedItemPosition, SnnUbicacion.selectedItemPosition,
                        txtNombreVaca.text.toString(), txtFechaNacimiento.text.toString(),
                        txtFechaPreniez.text.toString(), txtNroCaravana.text.toString() )
                    //guardar y limpiar campo (luego hacer metodo)
                    val idVaca = conexion.guardarVaca(vacaNueva)
                    vacaNueva.id_vaca = idVaca!!.toInt()
                    txtNombreVaca.setText("")
                    txtFechaNacimiento.setText("")
                    txtFechaPreniez.setText("")
                    txtNroCaravana.setText("")
                    SnnColor.setSelection(0)
                    SnnUbicacion.setSelection(0)
                    ListaDeVacas.vacas!!.add(vacaNueva)
                    ListaDeVacas.vacaAdapter!!.notifyItemInserted(ListaDeVacas.vacas!!.lastIndex)
                    ListaDeVacas.vacaAdapter!!.ordenarPosiciones()

                    // Vuelve a la ventana anterior
                    finish()
                }else{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Atención!")
                    builder.setMessage("El nombre es campo obligatorio")
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                        // Acción a ejecutar cuando se presiona el botón Aceptar/*
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
    }
}