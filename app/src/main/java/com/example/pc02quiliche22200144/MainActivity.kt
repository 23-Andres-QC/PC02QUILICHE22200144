package com.example.pc02quiliche22200144

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pc02quiliche22200144.presentation.navigation.AppNavGraph
import com.example.pc02quiliche22200144.ui.theme.PC02QUILICHE22200144Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PC02QUILICHE22200144Theme {
                AppNavGraph()
            }
        }
    }
}