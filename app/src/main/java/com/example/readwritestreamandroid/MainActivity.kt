package com.example.readwritestreamandroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.readwritestreamandroid.ui.theme.ReadWriteStreamAndroidTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        permissions handle
        val launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if(Environment.isExternalStorageManager()){
                Log.i("mainActivity"," permission granted")
            }else {
                Log.i("mainActivity"," permission denied")

            }
        }
        if (!Environment.isExternalStorageManager()) {
            try {
                val uri = Uri.fromParts("package", getPackageName(), null);
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
                    it.data= uri
                    launcher.launch(it)
                }
            } catch (e: Exception) {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
                    launcher.launch(it)
                }
            }
        }

        val vm = viewModels<MainViewModel>().value
        setContent {
            ReadWriteStreamAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val scope = rememberCoroutineScope()
                    val fileList = vm.fileListFlow.collectAsState().value

                    if (vm.isCopying) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    if (fileList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Folder is Empty")
                        }
                    } else {
                        LazyColumn() {
                            items(fileList) { file ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .height(70.dp)
                                        .clickable {
                                            scope.launch {
                                                vm.copyFile(file.absolutePath)
                                            }
                                        }
                                ) {
                                    Text(file.name)
                                }
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}

