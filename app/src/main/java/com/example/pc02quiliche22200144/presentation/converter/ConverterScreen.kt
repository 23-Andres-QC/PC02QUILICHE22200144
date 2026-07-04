package com.example.pc02quiliche22200144.presentation.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pc02quiliche22200144.data.remote.FirebaseAuthManager

@Composable
fun ConverterScreen(
    navController: NavController,
    viewModel: ConverterViewModel = viewModel()
) {
    val rates by viewModel.rates.collectAsState()
    val resultText by viewModel.resultText.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var amount by remember { mutableStateOf("100") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Conversor de Divisas", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = {
                FirebaseAuthManager.logout()
                navController.navigate("login") {
                    popUpTo(0)
                }
            }) {
                Text("Salir")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Monto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("De")
        Box {
            OutlinedButton(
                onClick = { fromExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(fromCurrency)
            }
            DropdownMenu(
                expanded = fromExpanded,
                onDismissRequest = { fromExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                rates.forEach { rate ->
                    DropdownMenuItem(
                        text = { Text(rate.code) },
                        onClick = {
                            fromCurrency = rate.code
                            fromExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("A")
        Box {
            OutlinedButton(
                onClick = { toExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(toCurrency)
            }
            DropdownMenu(
                expanded = toExpanded,
                onDismissRequest = { toExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                rates.forEach { rate ->
                    DropdownMenuItem(
                        text = { Text(rate.code) },
                        onClick = {
                            toCurrency = rate.code
                            toExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.convert(amount, fromCurrency, toCurrency) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convertir")
        }

        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        resultText?.let { text ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("history") }) {
            Text("Ver historial de conversiones")
        }
    }
}
