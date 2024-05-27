package com.github.teop.core.exchange.calculator

import com.github.teop.EUR
import com.github.teop.HKD
import com.github.teop.USD
import com.github.itisme0402.teop.core.MoneyAmount
import com.github.itisme0402.teop.core.exchange.ExchangeData
import com.github.itisme0402.teop.core.exchange.calculator.ExchangeCalculator
import com.github.itisme0402.teop.core.exchange.calculator.FeeRepository
import com.github.itisme0402.teop.core.rate.ExchangeRateInfo
import com.github.itisme0402.teop.core.rate.ExchangeRateRepository
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class ExchangeCalculatorTest {
    @get:Rule
    val mockKRule = MockKRule(this)

    @MockK
    private lateinit var rateRepository: ExchangeRateRepository

    @MockK
    private lateinit var feeRepository: FeeRepository

    private lateinit var sut: ExchangeCalculator

    @Before
    fun setUp() {
        sut = ExchangeCalculator(
            rateRepository,
            feeRepository,
        )
        every { feeRepository.calculateFee(any(), any()) } returns null
    }

    @Test
    fun `calculateExchange returns scaled rounded down amount for simple conversion`() = runTest {
        every { rateRepository.observeExchangeRates() } returns flowOf(
            ExchangeRateInfo(
                EUR,
                mapOf(
                    USD to "1.128".toBigDecimal(),
                ),
            )
        )

        val (amountReceived, fee) = sut.calculateExchange(
            MoneyAmount(1.toBigDecimal(), EUR),
            USD
        )
        Assert.assertEquals(MoneyAmount("1.12".toBigDecimal(), USD), amountReceived)
        Assert.assertEquals(null, fee)
    }

    @Test
    fun `calculateExchange performs double down-rounding conversion when sold currency isn't base`() =
        runTest {
            every { rateRepository.observeExchangeRates() } returns flowOf(
                ExchangeRateInfo(
                    EUR,
                    mapOf(
                        USD to "1.128".toBigDecimal(),
                        HKD to 8.toBigDecimal()
                    ),
                )
            )

            val (amountReceived, fee) = sut.calculateExchange(
                MoneyAmount(1.toBigDecimal(), HKD),
                USD
            )
            Assert.assertEquals(MoneyAmount("0.13".toBigDecimal(), USD), amountReceived)
            Assert.assertEquals(null, fee)
        }

    @Test(expected = IllegalArgumentException::class)
    fun `calculateExchange throws IllegalArgumentException when currencies match`() = runTest {
        sut.calculateExchange(MoneyAmount(BigDecimal.ONE, EUR), EUR)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `calculateExchange throws IllegalArgumentException when amount is 0`() = runTest {
        sut.calculateExchange(MoneyAmount(BigDecimal.ZERO, EUR), USD)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `calculateExchange throws IllegalArgumentException when amount is negative`() = runTest {
        sut.calculateExchange(MoneyAmount(-BigDecimal.ONE, EUR), USD)
    }

    @Test
    fun `getCalculationFlow restores from error`() = runTest {
        every { rateRepository.observeExchangeRates() } returns flowOf(
            ExchangeRateInfo(EUR, emptyMap()),
            ExchangeRateInfo(EUR, mapOf(USD to "1.2".toBigDecimal()))
        )
        every { feeRepository.calculateFee(any(), any()) } returns null

        val collector = mockk<FlowCollector<Result<ExchangeData>>>(relaxUnitFun = true)
        sut.getCalculationFlow(MoneyAmount(BigDecimal.ONE, EUR), USD).collect(collector)

        coVerifyOrder {
            collector.emit(match { it.isFailure })
            collector.emit(
                Result.success(
                    ExchangeData(
                        MoneyAmount("1.20".toBigDecimal(), USD),
                        null
                    )
                )
            )
        }
    }
}