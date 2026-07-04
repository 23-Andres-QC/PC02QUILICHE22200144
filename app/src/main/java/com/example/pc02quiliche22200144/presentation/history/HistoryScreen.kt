package com.example.pc02quiliche22200144.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pc02quiliche22200144.data.model.ConversionRecord
import com.example.pc02quiliche22200144.data.remote.CurrencyRepository
import com.example.pc02quiliche22200144.data.remote.FirebaseAuthManager
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryScreen(navController: NavController) {
    var conversions by remember { mutableStateOf<List<ConversionRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuthManager.currentUser?.uid
        if (uid != null) {
            conversions = CurrencyRepository.getConversionsForUser(uid)
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text("Historial de conversiones", style = MaterialTheme.typography.titleLarge)

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else if (conversions.isEmpty()) {
            Text("Aún no tienes conversiones registradas")
        } else {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(conversions) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "%.2f %s → %.2f %s".format(
                                    record.amount, record.fromCurrency, record.result, record.toCurrency
                                ),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                formatter.format(record.timestamp.toDate()),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
