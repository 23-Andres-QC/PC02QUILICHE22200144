package com.example.pc02quiliche22200144.data.remote

import com.example.pc02quiliche22200144.data.model.ConversionRecord
import com.example.pc02quiliche22200144.data.model.CurrencyRate
import com.example.pc02quiliche22200144.data.remote.exchangerate.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object CurrencyRepository {

    private const val EXCHANGE_RATE_API_KEY = "7202edf9dcb5817edf3f745b"
    private val supportedCodes = listOf("USD", "EUR", "PEN", "GBP", "JPY")

    private val firestore = FirebaseFirestore.getInstance()
    private val conversionsCollection = firestore.collection("conversions")

    val defaultRates = listOf(
        CurrencyRate("USD", 1.0),
        CurrencyRate("EUR", 1.08),
        CurrencyRate("PEN", 0.27),
        CurrencyRate("GBP", 1.27),
        CurrencyRate("JPY", 0.0067)
    )

    suspend fun getRates(): List<CurrencyRate> {
        return try {
            val response = RetrofitInstance.api.getLatestRates(EXCHANGE_RATE_API_KEY, "USD")
            val rates = supportedCodes.mapNotNull { code ->
                val unitsPerUsd = response.conversion_rates[code] ?: return@mapNotNull null
                CurrencyRate(code = code, usdValue = 1.0 / unitsPerUsd)
            }
            rates.ifEmpty { defaultRates }
        } catch (e: Exception) {
            defaultRates
        }
    }

    suspend fun saveConversion(record: ConversionRecord): Result<Unit> {
        return try {
            conversionsCollection.add(
                hashMapOf(
                    "uid" to record.uid,
                    "timestamp" to record.timestamp,
                    "amount" to record.amount,
                    "fromCurrency" to record.fromCurrency,
                    "toCurrency" to record.toCurrency,
                    "result" to record.result
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConversionsForUser(uid: String): List<ConversionRecord> {
        return try {
            val snapshot = conversionsCollection
                .whereEqualTo("uid", uid)
                .get()
                .await()
            snapshot.documents.map { doc ->
                ConversionRecord(
                    uid = doc.getString("uid") ?: "",
                    timestamp = doc.getTimestamp("timestamp") ?: com.google.firebase.Timestamp.now(),
                    amount = doc.getDouble("amount") ?: 0.0,
                    fromCurrency = doc.getString("fromCurrency") ?: "",
                    toCurrency = doc.getString("toCurrency") ?: "",
                    result = doc.getDouble("result") ?: 0.0
                )
            }.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
