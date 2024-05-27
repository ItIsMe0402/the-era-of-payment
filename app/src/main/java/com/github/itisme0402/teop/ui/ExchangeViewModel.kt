package com.github.itisme0402.teop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.itisme0402.teop.core.balance.BalanceUseCase
import com.github.itisme0402.teop.core.exchange.calculator.ExchangeCalculator
import com.github.itisme0402.teop.core.exchange.ExchangeUseCase
import com.github.itisme0402.teop.core.MoneyAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    balanceUseCase: BalanceUseCase,
    private val exchangeCalculator: ExchangeCalculator,
    private val exchangeUseCase: ExchangeUseCase,
) : ViewModel() {

    val balances: StateFlow<List<String>> = balanceUseCase.getBalances().map { balances ->
        (DEFAULT_CURRENCIES + balances.keys).toSet().sortedWith(CURRENCY_COMPARATOR).map {
            (balances[it]
                ?: MoneyAmount(BigDecimal.ZERO.setScale(it.defaultFractionDigits), it))
                .formatted()
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val availableCurrencies: StateFlow<List<String>> =
        exchangeCalculator.observeAvailableCurrencies().map { currencies ->
            (DEFAULT_CURRENCIES + currencies).toSet().sortedWith(CURRENCY_COMPARATOR).map { it.currencyCode }
        }.stateIn(viewModelScope, SharingStarted.Lazily, DEFAULT_CURRENCIES.map { it.currencyCode })

    private val _currencyToSell = MutableStateFlow(DEFAULT_CURRENCY_TO_SELL_CODE)
    val currencyToSell: StateFlow<String> = _currencyToSell

    private val _currencyToReceive = MutableStateFlow(DEFAULT_CURRENCY_TO_RECEIVE_CODE)
    val currencyToReceive: StateFlow<String> = _currencyToReceive

    private val _amountToReceive = MutableStateFlow("")
    val amountToReceive: StateFlow<String> = _amountToReceive

    private val _fee = MutableStateFlow<String?>(null)
    val fee: StateFlow<String?> = _fee

    private val _sellAmountInputFlow = MutableStateFlow("")
    val sellAmountInputFlow: StateFlow<String> = _sellAmountInputFlow

    private val _dialog = MutableStateFlow<DialogState?>(null)
    val dialog: StateFlow<DialogState?> = _dialog

    private val _isInputCorrect = MutableStateFlow(true)
    val isInputCorrect: StateFlow<Boolean> = _isInputCorrect

    private val _isExchangePossible = MutableStateFlow(false)
    val isExchangePossible: StateFlow<Boolean> = _isExchangePossible

    sealed class DialogState {
        data class CurrencyExchanged(
            val amountSold: String,
            val amountReceived: String,
            val fee: String?,
        ) : DialogState()
        data object Error : DialogState()
    }

    init {
        @OptIn(ExperimentalCoroutinesApi::class)
        viewModelScope.launch {
            combine(
                sellAmountInputFlow,
                currencyToSell,
                currencyToReceive,
            ) { _, _, _ ->
            }.transformLatest<Unit, Unit> {
                if (sellAmountInputFlow.value.isBlank()) {
                    _isInputCorrect.value = true
                    _amountToReceive.value = ""
                    _fee.value = null
                    _isExchangePossible.value = false
                    return@transformLatest
                }
                try {
                    val amountToSell = getAmountToSell()
                    val targetCurrency = getTargetCurrency()
                    exchangeCalculator.getCalculationFlow(amountToSell, targetCurrency).collect {
                        it.onSuccess { calculation ->
                            _isInputCorrect.value = true
                            _amountToReceive.value = "+" + calculation.amountReceived.formatted(withCurrency = false)
                            _fee.value = calculation.fee?.let { "-" + calculation.fee.formatted() }
                            _isExchangePossible.value = true
                        }.onFailure {
                            _isInputCorrect.value = false
                            _amountToReceive.value = ""
                            _fee.value = null
                            _isExchangePossible.value = false
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _isInputCorrect.value = false
                    _amountToReceive.value = ""
                    _fee.value = null
                    _isExchangePossible.value = false
                    Timber.e(e)
                }
            }.collect()
        }
    }

    fun onSellAmountChanged(sellAmount: String) {
        _sellAmountInputFlow.value = sellAmount
    }

    fun onCurrencyToSellPicked(currency: String) {
        _currencyToSell.value = currency
    }

    fun onCurrencyToReceivePicked(currency: String) {
        _currencyToReceive.value = currency
    }

    fun onSubmitClicked() {
        viewModelScope.launch {
            try {
                val amountToSell = getAmountToSell()
                val (amountReceived, fee) = exchangeUseCase.performExchange(
                    amountToSell,
                    getTargetCurrency(),
                )
                _dialog.value = DialogState.CurrencyExchanged(
                    amountToSell.formatted(),
                    amountReceived.formatted(),
                    fee?.formatted(),
                )
                _sellAmountInputFlow.value = ""
            } catch (e: Exception) {
                _dialog.value = DialogState.Error
                Timber.e(e)
            }
        }
    }

    private fun getAmountToSell(): MoneyAmount {
        val currency = Currency.getInstance(currencyToSell.value)
        return MoneyAmount(
            sellAmountInputFlow.value.toBigDecimal()
                .setScale(
                    currency.defaultFractionDigits,
                    RoundingMode.UNNECESSARY //Deliberate, as we want to treat "too precise" input as an error
                ),
            currency
        )
    }

    private fun getTargetCurrency() = Currency.getInstance(currencyToReceive.value)

    fun onDismissDialog() {
        _dialog.value = null
    }

    private companion object {
        val EUR: Currency = Currency.getInstance("EUR")
        val USD: Currency = Currency.getInstance("USD")
        val DEFAULT_CURRENCY_TO_SELL_CODE: String = EUR.currencyCode
        val DEFAULT_CURRENCY_TO_RECEIVE_CODE: String = USD.currencyCode
        val DEFAULT_CURRENCIES = listOf(EUR, USD)

        val CURRENCY_COMPARATOR = Comparator<Currency> { c1, c2 ->
            val i1 = DEFAULT_CURRENCIES.indexOf(c1)
            val i2 = DEFAULT_CURRENCIES.indexOf(c2)
            return@Comparator when {
                i1 >= 0 && i2 >= 0 -> i1.compareTo(i2)
                i1 >= 0 && i2 < 0 -> -1
                i1 < 0 && i2 >= 0 -> 1
                else -> c1.currencyCode.compareTo(c2.currencyCode)
            }
        }

        val formatter = DecimalFormat().apply {
            decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US)
        }

        fun MoneyAmount.formatted(withCurrency: Boolean = true): String {
            val pattern = currency.defaultFractionDigits.let {
                if (it > 0) "0.${"0".repeat(it)}" else ""
            }
            com.github.itisme0402.teop.ui.ExchangeViewModel.formatter.applyPattern(pattern)
            return com.github.itisme0402.teop.ui.ExchangeViewModel.formatter.format(amount) + if (withCurrency) " $currency" else ""
        }
    }
}