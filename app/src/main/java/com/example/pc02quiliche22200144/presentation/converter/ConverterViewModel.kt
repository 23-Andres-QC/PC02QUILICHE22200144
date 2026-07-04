package com.example.pc02quiliche22200144.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pc02quiliche22200144.data.model.ConversionRecord
import com.example.pc02quiliche22200144.data.model.CurrencyRate
import com.example.pc02quiliche22200144.data.remote.CurrencyRepository
import com.example.pc02quiliche22200144.data.remote.FirebaseAuthManager
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConverterViewModel : ViewModel() {

    private val _rates = MutableStateFlow(emptyList<CurrencyRate>())
    val rates: StateFlow<List<CurrencyRate>> = _rates

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _resultText = MutableStateFlow<String?>(null)
    val resultText: StateFlow<String?> = _resultText

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadRates()
    }

    fun loadRates() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _rates.value = CurrencyRepository.getRates()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "No se pudieron obtener las tasas de cambio"
            }
            _isLoading.value = false
        }
    }

    fun convert(amountText: String, fromCurrency: String, toCurrency: String) {
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _errorMessage.value = "Ingresa un monto válido"
            _resultText.value = null
            return
        }

        val fromRate = _rates.value.find { it.code == fromCurrency }?.usdValue
        val toRate = _rates.value.find { it.code == toCurrency }?.usdValue

        if (fromRate == null || toRate == null) {
            _errorMessage.value = "Tasas no disponibles"
            _resultText.value = null
            return
        }

        val result = (amount * fromRate) / toRate
        _errorMessage.value = null
        _resultText.value = "%.2f %s equivalen a %.2f %s".format(amount, fromCurrency, result, toCurrency)

        val uid = FirebaseAuthManager.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                CurrencyRepository.saveConversion(
                    ConversionRecord(
                        uid = uid,
                        timestamp = Timestamp.now(),
                        amount = amount,
                        fromCurrency = fromCurrency,
                        toCurrency = toCurrency,
                        result = result
                    )
                )
            }
        }
    }
}
