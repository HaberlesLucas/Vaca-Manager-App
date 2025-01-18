package com.haberleslucas.asistenciadevacas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private var driveService: Drive? = null
    private var accionPendiente: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar botones
        val btnVerListaVacas = findViewById<Button>(R.id.btnVerListaVacas)
        val btnTomarAsistenciaVacas = findViewById<Button>(R.id.btnTomarAsitenciaVacas)
        val btnBackup = findViewById<Button>(R.id.btnBackup)
        val btnRestaurar = findViewById<Button>(R.id.btnRestaurar)
        //val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Configuración de Google Sign-In
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .requestProfile()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE)) //incluir el alcance de Drive
                .build()
        //.requestIdToken("517719569772-7d6692dc3ai7kqr2qcipmr93hn7f5do4.apps.googleusercontent.com")

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //verificar si ya hay una sesión activa
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            handleSignInSuccess(account)
        }

        //btnLogin.setOnClickListener {
        //    signIn()
        //}

        btnLogout.setOnClickListener {
            signOut()
        }

        btnVerListaVacas.setOnClickListener {
            startActivity(Intent(this, ListaDeVacas::class.java))
        }

        btnTomarAsistenciaVacas.setOnClickListener {
            startActivity(Intent(this, ListaDeVacasAsistidas::class.java))
        }

        //btnBackup.setOnClickListener {
        //    realizarBackup()
        //}
        //btnBackup.setOnClickListener {
        //    val account = GoogleSignIn.getLastSignedInAccount(this)
        //    if (account == null) {
        //        Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
        //        signIn() // Inicia sesión automáticamente
        //    } else {
        //        realizarBackup() // Si ya está logueado, realiza el backup
        //    }
        //}
        btnBackup.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account == null) {
                Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
                signIn("backup") // Acción especificada como "backup"
            } else {
                realizarBackup()
            }
        }


        //restaurar ______________________________________________________________________________________________________

        //btnRestaurar.setOnClickListener {
        //    val opciones = arrayOf("Restaurar desde archivo local", "Restaurar desde Google Drive")
        //    val builder = AlertDialog.Builder(this)
        //    builder.setTitle("Selecciona una opción")
        //    builder.setItems(opciones) { _, which ->
        //        when (which) {
        //            0 -> seleccionarArchivoLocal()
        //            1 -> buscarBackupsEnDrive()
        //        }
        //    }
        //    builder.show()
        //}
        btnRestaurar.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account == null) {
                Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
                signIn("restore") // Acción especificada como "restore"
            } else {
                mostrarOpcionesDeRestauracion()
            }
        }


        //restaurar ______________________________________________________________________________________________________


    }

    //private fun signIn() {
    //    try {
    //        val signInIntent = googleSignInClient.signInIntent
    //        startActivityForResult(signInIntent, RC_SIGN_IN)
    //    } catch (e: Exception) {
    //        Log.e("SignIn", "Error iniciando sesión: ${e.message}")
    //        Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
    //    }
    //}
    private fun signIn(accion: String = "backup") {
        accionPendiente = accion
        try {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Exception) {
            Log.e("SignIn", "Error iniciando sesión: ${e.message}")
            Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun handleSignInSuccess(account: GoogleSignInAccount) {
        // Mostrar el botón de cierre de sesión
        findViewById<Button>(R.id.btnLogout).visibility = View.VISIBLE
        Toast.makeText(this, "Sesión iniciada: ${account.email}", Toast.LENGTH_SHORT).show()
    }

    private fun signOut() {
        try {
            googleSignInClient.signOut().addOnCompleteListener(this) {
                // Ocultar el botón de cierre de sesión
                findViewById<Button>(R.id.btnLogout).visibility = View.GONE
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SignOut", "Error cerrando sesión: ${e.message}")
            Toast.makeText(this, "Error al cerrar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun realizarBackup() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            Toast.makeText(this, "Por favor, inicia sesión primero", Toast.LENGTH_SHORT).show()
            return
        }

        // Reinicializar el servicio Drive antes de cada uso
        initializeDriveService(account)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val backupFile = exportarDatabase()
                if (backupFile != null) {
                    withContext(Dispatchers.IO) {
                        subirArchivoADrive(backupFile)
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "Backup completado con éxito",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("Backup", "Error en backup: ${e.message}", e)
                Toast.makeText(
                    this@MainActivity,
                    "Error al realizar el backup: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun exportarDatabase(): java.io.File? {
        val dbPath = getDatabasePath("dbH").absolutePath
        val backupFile = java.io.File(getExternalFilesDir(null), "backup_db.sqlite")

        return try {
            val databaseFile = java.io.File(dbPath)
            databaseFile.copyTo(backupFile, overwrite = true)
            backupFile
        } catch (e: Exception) {
            Log.e("Database", "Error al exportar DB: ${e.message}", e)
            null
        }
    }

    private suspend fun subirArchivoADrive(file: java.io.File) {
        withContext(Dispatchers.IO) {
            try {
                val fileMetadata = File()
                // Obtener la fecha actual en formato yyyy_MM_dd_HH:mm
                val sdf = SimpleDateFormat("yyyy_MM_dd_HH:mm", Locale.getDefault())
                val currentDateTime = sdf.format(Date())
                fileMetadata.name = "backup_$currentDateTime.sqlite"

                // Subir el nuevo archivo sin eliminar los existentes
                val mediaContent = FileContent("application/x-sqlite3", file)
                val resultado =
                    driveService?.files()?.create(fileMetadata, mediaContent)?.setFields("id, name")
                        ?.execute()

                Log.d("Drive", "Archivo subido con ID: ${resultado?.id}")
            } catch (e: Exception) {
                Log.e("Drive", "Error al subir: ${e.message}", e)
                throw e
            }
        }
    }


    /*
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == RC_SIGN_IN) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    handleSignInSuccess(account) //maneja el inicio de sesión exitoso
                    //realizar el backup directamente después de iniciar sesión
                    realizarBackup()
                } catch (e: ApiException) {
                    Log.e("GoogleSignIn", "Error en la autenticación: ${e.message}")
                    Toast.makeText(this, "La autenticación falló: ${e.statusCode}", Toast.LENGTH_LONG).show()
                }
            }

            if (requestCode == RC_RESTORE_LOCAL && resultCode == RESULT_OK) {
                val uri = data?.data
                uri?.let {
                    val backupFile = java.io.File(cacheDir, "temp_backup.sqlite")
                    contentResolver.openInputStream(it)?.use { input ->
                        backupFile.outputStream().use { output -> input.copyTo(output) }
                    }
                    restaurarBaseDeDatos(backupFile)
                }
            }
        }
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                handleSignInSuccess(account)

                when (accionPendiente) {
                    "backup" -> realizarBackup()
                    "restore" -> mostrarOpcionesDeRestauracion()
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error en la autenticación: ${e.message}")
                Toast.makeText(this, "La autenticación falló: ${e.statusCode}", Toast.LENGTH_LONG)
                    .show()
            }
        }

        if (requestCode == RC_RESTORE_LOCAL && resultCode == RESULT_OK) {
            val uri = data?.data
            uri?.let {
                val backupFile = java.io.File(cacheDir, "temp_backup.sqlite")
                contentResolver.openInputStream(it)?.use { input ->
                    backupFile.outputStream().use { output -> input.copyTo(output) }
                }
                restaurarBaseDeDatos(backupFile)
            }
        }
    }


    private fun initializeDriveService(account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(
            this, Collections.singleton(DriveScopes.DRIVE_FILE)
        ).apply {
            selectedAccount = account.account
        }

        driveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), credential
        ).setApplicationName("Asistencia de Vacas").build()
    }


    //restaurar ______________________________________________________________________________________________________

    private fun seleccionarArchivoLocal() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/x-sqlite3"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(
            Intent.createChooser(intent, "Selecciona un archivo de backup"), RC_RESTORE_LOCAL
        )
    }

    private suspend fun listarArchivosDeDrive(): List<File>? {
        return withContext(Dispatchers.IO) {
            try {
                val result = driveService?.files()?.list()?.setQ("name contains 'backup_'")
                    ?.setFields("files(id, name, modifiedTime)")?.execute()
                result?.files
            } catch (e: Exception) {
                Log.e("Drive", "Error al listar archivos: ${e.message}", e)
                null
            }
        }
    }

    private fun mostrarDialogoDeArchivos(archivos: List<File>) {
        val nombresArchivos = archivos.map { it.name }.toTypedArray()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona un archivo de backup")
        builder.setItems(nombresArchivos) { _, which ->
            val archivoSeleccionado = archivos[which]

            // Lanzar una coroutina en el MainScope
            CoroutineScope(Dispatchers.Main).launch {
                descargarArchivoDeDrive(archivoSeleccionado.id, archivoSeleccionado.name)
            }
        }
        builder.show()
    }


    private suspend fun descargarArchivoDeDrive(fileId: String, fileName: String) {
        withContext(Dispatchers.IO) {
            try {
                val outputFile = java.io.File(getExternalFilesDir(null), fileName)
                val outputStream = outputFile.outputStream()
                driveService?.files()?.get(fileId)?.executeMediaAndDownloadTo(outputStream)
                outputStream.flush()
                outputStream.close()
                Log.d("Drive", "Archivo descargado: $fileName")
                restaurarBaseDeDatos(outputFile)
            } catch (e: Exception) {
                Log.e("Drive", "Error al descargar archivo: ${e.message}", e)
            }
        }
    }

    private fun restaurarBaseDeDatos(archivoBackup: java.io.File) {
        val dbPath = getDatabasePath("dbH").absolutePath
        val databaseFile = java.io.File(dbPath)

        try {
            archivoBackup.copyTo(databaseFile, overwrite = true)
            Toast.makeText(this, "Base de datos restaurada con éxito", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("Restore", "Error al restaurar la base de datos: ${e.message}", e)
            Toast.makeText(
                this,
                "Error al restaurar la base de datos: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun buscarBackupsEnDrive() {
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {
            // Si no hay una sesión activa, solicitar inicio de sesión
            Toast.makeText(
                this,
                "Por favor, inicia sesión para buscar backups.",
                Toast.LENGTH_SHORT
            ).show()
            signIn("restore")
            return
        }

        // Si el usuario está logueado, inicializar el servicio de Drive y buscar archivos
        initializeDriveService(account)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    val query = "name contains 'backup_'"
                    val result =
                        driveService?.files()?.list()?.setQ(query)?.setFields("files(id, name)")
                            ?.execute()

                    val archivos = result?.files.orEmpty()
                    if (archivos.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "No se encontraron backups en Drive",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            mostrarDialogoDeArchivos(archivos)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Drive", "Error buscando backups: ${e.message}", e)
                Toast.makeText(
                    this@MainActivity,
                    "Error buscando backups: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarOpcionesDeRestauracion() {
        val opciones = arrayOf("Restaurar desde archivo local", "Restaurar desde Google Drive")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona una opción")
        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> seleccionarArchivoLocal()
                1 -> buscarBackupsEnDrive()
            }
        }
        builder.show()
    }


    //restaurar ______________________________________________________________________________________________________


    companion object {
        private const val RC_SIGN_IN = 1001
        private const val RC_RESTORE_LOCAL = 1002
    }
}
