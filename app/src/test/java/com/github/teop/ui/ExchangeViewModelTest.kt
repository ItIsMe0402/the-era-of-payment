package com.github.teop.ui

import com.github.teop.EUR
import com.github.teop.USD
import com.github.itisme0402.teop.core.MoneyAmount
import com.github.itisme0402.teop.core.balance.BalanceUseCase
import com.github.itisme0402.teop.core.exchange.calculator.ExchangeCalculator
import com.github.itisme0402.teop.core.exchange.ExchangeData
import com.github.itisme0402.teop.ui.ExchangeViewModel
import com.github.itisme0402.teop.core.exchange.ExchangeUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeViewModelTest {
    @get:Rule
    val mockKRule = MockKRule(this)

    @MockK
    private lateinit var balanceUseCase: BalanceUseCase

    @MockK
    private lateinit var exchangeCalculator: ExchangeCalculator

    @MockK
    private lateinit var exchangeUseCase: ExchangeUseCase

    private lateinit var scheduler: TestCoroutineScheduler

    private lateinit var sut: ExchangeViewModel

    @Before
    fun setUp() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        scheduler = dispatcher.scheduler
        every { balanceUseCase.getBalances() } returns emptyFlow()
        every { exchangeCalculator.observeAvailableCurrencies() } returns emptyFlow()
        sut = ExchangeViewModel(
            balanceUseCase,
            exchangeCalculator,
            exchangeUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSellAmountChanged updates amountToReceive to calculated value`() = runTest {
        val expectedAmountToReceive = "+50.00"
        val calculated = ExchangeData(
            MoneyAmount(50.toBigDecimal(), USD),
            null,
        )
        every {
            exchangeCalculator.getCalculationFlow(
                MoneyAmount("42.00".toBigDecimal(), EUR),
                USD,
            )
        } returns flowOf(Result.success(calculated))

        sut.onSellAmountChanged("42")
        scheduler.runCurrent()

        Assert.assertEquals(expectedAmountToReceive, sut.amountToReceive.value)
    }

    @Test
    fun `onSubmitClicked performs exchange`() = runBlocking {
        val sellAmount = "12.10"
        val exchangeData = ExchangeData(MoneyAmount(BigDecimal.ONE, USD), null)
        coEvery {
            exchangeCalculator.calculateExchange(any(), any())
        } returns exchangeData
        coEvery {
            exchangeUseCase.performExchange(MoneyAmount(sellAmount.toBigDecimal(), EUR), USD)
        } returns exchangeData

        sut.onSellAmountChanged(sellAmount)
        sut.onSubmitClicked()
        scheduler.runCurrent()

        Assert.assertEquals(
            ExchangeViewModel.DialogState.CurrencyExchanged(
                "12.10 EUR",
                "1.00 USD",
                null,
            ),
            sut.dialog.value
        )
    }
}