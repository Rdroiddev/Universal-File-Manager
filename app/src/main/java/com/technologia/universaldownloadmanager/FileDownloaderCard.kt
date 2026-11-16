package com.technologia.universaldownloadmanager

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

//
@Composable
fun FileDownloaderCard() {

    var downloadUrl by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var downloadProgress by remember { mutableFloatStateOf(0f)}
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var isDownloading by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Download Manager",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = downloadUrl,
                    onValueChange = { downloadUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Download URL") },
                    placeholder = {
                        Text("https://example.com/file.zip")
                    },
                    singleLine = true,
                    trailingIcon = {
                        TextButton(onClick = { /* Paste later */ }) {
                            Text("Paste")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
                val ctx = LocalContext.current
                val filePath =  ctx.getExternalFilesDir(null)
                Button(
                    onClick = {
                        isDownloading = "download starting....."
                        downloadJob = scope.launch {
//                           Log.d("MAINACTIVITY", "FileDownloaderCard: Download Start")
                            isDownloading = "download started"
                          FileDownloader().download(
                              downloadUrl,
                              File(filePath, "ubuntu.iso"),
                              onProgress = {total,bytesDownloaded ,comp->
                                 downloadProgress = bytesDownloaded * 100.0f / total
                              },

                          )
                            isDownloading = "Download Completed!"
                       }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = downloadUrl.isNotBlank()
                ) {
                    Text("Download")
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (downloadProgress > 0) {
                    Text(
                        text = "Download Information ${String.format("%.2f", downloadProgress)}%",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

//                LinearProgressIndicator(
//                    modifier = Modifier.fillMaxWidth()
//                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
//                    Button(onClick = {}) {
//                        Text("Pause")
//                    }
//
//                    Button(onClick = {}) {
//                        Text("Resume")
//                    }

                    Button(onClick = {
                        isDownloading = "Cancelling Download!"
                        Log.d("MAINACTIVITY", "FileDownloaderCard: Canceling Download")
//
                        downloadJob?.cancel()
                        downloadJob?.invokeOnCompletion { it
                            downloadProgress = 0f
                        }
                        downloadJob = null
                        isDownloading = "Download Canceled"
                    }) {
                        Text("Cancel")
                    }
                    Text(isDownloading)
                }
            }
        }
    }
}



class FileDownloader{
    suspend fun download(urlAddress: String,file: File,onProgress:(total: Long, bytesDownloaded: Long, complete: Boolean)-> Unit) = withContext(Dispatchers.IO){
        // create network connection
        val url = URL(urlAddress)
        val connection = url.openConnection() as HttpURLConnection
        var isCompleted = false
        try {
            val buffer: ByteArray = ByteArray(65536)

            val input = connection.getInputStream()
            // gives total number of bytes of stream = contentLength
            val length =  connection.contentLengthLong
            var downloaded = 0
            var bytesRead : Int
            FileOutputStream(file).use { outputStream ->
                while (true){
                    ensureActive()
                   bytesRead =  input.read(buffer)
                    if (bytesRead == -1)
                        break
                    outputStream.write(buffer,0,bytesRead)
                    downloaded += bytesRead
//                    if (length > 0)
                    onProgress(length, downloaded.toLong(), isCompleted)
                    yield()
                }
            }
            isCompleted = true
        }finally {
            if (!isCompleted)
                file.delete()
        }
    }
}


