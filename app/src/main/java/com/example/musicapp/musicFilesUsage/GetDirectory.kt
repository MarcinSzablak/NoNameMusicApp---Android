package com.example.musicapp.musicFilesUsage

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import com.example.musicapp.settings.SettingsDataStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.log

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun GetDirectory(
    database: DBHelper
) {
    val context = LocalContext.current

    val settings = SettingsDataStore(context)

    val selectedUri = remember { mutableStateOf<Uri?>(null) }

    // this part of code cast Director selector
    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        selectedUri.value = uri

        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
        GlobalScope.launch {
            settings.saveDirectoryPath(uri.toString())
            findAlbums(
                uri = uri,
                context = context,
                database = database,
                albumsList = AlbumsWhichExists.list,
            ).await()
        }
    }

    Button(onClick = { directoryPickerLauncher.launch(null) }) {
        Text(text = "Pick Directory")
    }

    selectedUri.value?.let {
        Text(text = "Selected directory: $it")
    } ?: Text(text = "No directory selected")
}