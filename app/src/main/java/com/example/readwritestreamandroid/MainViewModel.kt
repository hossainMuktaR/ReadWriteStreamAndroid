package com.example.readwritestreamandroid

import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private val TAG = "mainviewmodel"
class MainViewModel : ViewModel() {
    var fileListFlow = MutableStateFlow<List<File>>(listOf())
    var isCopying by mutableStateOf(false)

    //TODO: here you set file dir neither crash app
    val basePath = Environment.getExternalStorageDirectory().absolutePath+"/snaptube/download/SnapTube Video"

    init {
        viewModelScope.launch {
            fetchFileList(basePath)
        }
    }
    suspend fun fetchFileList(path: String) {
        var fileList = File(path).listFiles()
        if(!fileList.isNullOrEmpty()){
            fileListFlow.emit(fileList.toMutableList())
        }
    }

    suspend fun copyFile(inputStreamPath: String){
        return withContext(Dispatchers.IO) {
            Log.i(TAG,"copy path fun called ")
            val inputStream = FileInputStream(File(inputStreamPath))
            val outputDir = File(basePath+"/copy/")
            if(!outputDir.exists()){
                outputDir.mkdir()
            }
            val outputStream = FileOutputStream(File(outputDir,"copyVideo.mp4"))
            Log.i(TAG,"input out stream created")
            if(copyFile(inputStream, outputStream)){
                isCopying = false
                Log.i(TAG,"file operation succed")
            }
        }
    }

    private fun copyFile(from: InputStream, to: OutputStream): Boolean {
        isCopying = true
        Log.i(TAG,"copyFile Called")
        val buffer = ByteArray(1024)
        return try {
            to.use { outputStream ->
                var bytesRead: Int
                while(from.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
            Log.i(TAG,"input out stream done")
            true
        }catch (e: IOException){
            e.printStackTrace()
            isCopying = false
            Log.i(TAG,"input out stream catch exeption")

            false
        }
    }
}