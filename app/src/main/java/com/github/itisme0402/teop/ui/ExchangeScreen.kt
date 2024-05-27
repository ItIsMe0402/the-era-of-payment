package com.github.itisme0402.teop.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.itisme0402.teop.R
import com.github.itisme0402.teop.ui.theme.DarkGreen
import com.github.itisme0402.teop.ui.theme.Typography

@Composable
fun ExchangeScreen(
    viewModel: ExchangeViewModel,
) {
    val balances by viewModel.balances.collectAsStateWithLifecycle()
    val amountToSell by viewModel.sellAmountInputFlow.collectAsStateWithLifecycle()
    val availableCurrencies by viewModel.availableCurrencies.collectAsStateWithLifecycle()
    val currencyToSell by viewModel.currencyToSell.collectAsStateWithLifecycle()
    val currencyToReceive by viewModel.currencyToReceive.collectAsStateWithLifecycle()
    val amountToReceive by viewModel.amountToReceive.collectAsStateWithLifecycle()
    val fee by viewModel.fee.collectAsStateWithLifecycle()
    val dialog by viewModel.dialog.collectAsStateWithLifecycle()
    val isInputCorrect by viewModel.isInputCorrect.collectAsStateWithLifecycle()
    val isExchangePossible by viewModel.isExchangePossible.collectAsStateWithLifecycle()
    ExchangeScreen(
        balances,
        amountToSell,
        viewModel::onSellAmountChanged,
        availableCurrencies,
        viewModel::onCurrencyToSellPicked,
        viewModel::onCurrencyToReceivePicked,
        currencyToSell,
        amountToReceive,
        currencyToReceive,
        fee,
        viewModel::onSubmitClicked,
        dialog,
        viewModel::onDismissDialog,
        isInputCorrect,
        isExchangePossible,
    )
}

@Composable
fun ExchangeScreen(
    balances: List<String>,
    amountToSell: String,
    onSellAmountChanged: (String) -> Unit,
    availableCurrencies: List<String>,
    onCurrencyToSellPicked: (String) -> Unit,
    onCurrencyToReceivePicked: (String) -> Unit,
    currencyToSell: String,
    amountToBeReceived: String,
    currencyToReceive: String,
    fee: String?,
    onSubmitClicked: () -> Unit,
    dialogState: ExchangeViewModel.DialogState?,
    onDismissDialog: () -> Unit,
    isInputCorrect: Boolean,
    isExchangePossible: Boolean,
) {
    ConstraintLayout {
        val (
            balancesTitle,
            balanceList,
            exchangeTitle,
            sellLabel,
            amountToSellInput,
            currencyToSellPicker,
            receiveLabel,
            amountToReceiveText,
            currencyToReceivePicker,
            feeText,
            submitButton,
        ) = createRefs()
        val startGuide = createGuidelineFromStart(16.dp)
        val endGuide = createGuidelineFromEnd(16.dp)
        Text(
            text = stringResource(id = R.string.my_balances).uppercase(),
            style = Typography.titleMedium,
            modifier = Modifier.constrainAs(balancesTitle) {
                width = Dimension.fillToConstraints
                top.linkTo(parent.top, margin = 16.dp)
                linkTo(startGuide, endGuide, bias = 0F)
            }
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.constrainAs(balanceList) {
                width = Dimension.fillToConstraints
                top.linkTo(balancesTitle.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            items(balances) { balance ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                ) {
                    Text(
                        text = balance,
                        style = Typography.bodyLarge,
                    )
                }
            }
        }
        Text(
            text = stringResource(id = R.string.currency_exchange).uppercase(),
            style = Typography.titleMedium,
            modifier = Modifier.constrainAs(exchangeTitle) {
                top.linkTo(balanceList.bottom, margin = 16.dp)
                linkTo(startGuide, endGuide, bias = 0F)
            }
        )
        Text(
            text = stringResource(id = R.string.sell),
            style = Typography.bodyLarge,
            modifier = Modifier.constrainAs(sellLabel) {
                top.linkTo(amountToSellInput.top)
                bottom.linkTo(amountToSellInput.bottom)
                linkTo(startGuide, receiveLabel.end, bias = 1F)
            }
        )
        val currenciesBarrier = createStartBarrier(currencyToSellPicker, currencyToReceivePicker)
        CurrencyPicker(
            currencyPicked = currencyToSell,
            currencies = availableCurrencies,
            onCurrencyPicked = onCurrencyToSellPicked,
            modifier = Modifier.constrainAs(currencyToSellPicker) {
                top.linkTo(exchangeTitle.bottom, 8.dp)
                end.linkTo(endGuide)
            }
        )
        TextField(
            value = amountToSell,
            onValueChange = {
                onSellAmountChanged(it)
            },
            isError = !isInputCorrect,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            singleLine = true,
            modifier = Modifier.constrainAs(amountToSellInput) {
                width = Dimension.fillToConstraints
                baseline.linkTo(currencyToSellPicker.baseline)
                start.linkTo(sellLabel.end, margin = 8.dp)
                end.linkTo(currenciesBarrier, margin = 16.dp)
            }
        )
        val sellElementsBarrier =
            createBottomBarrier(sellLabel, amountToSellInput, currencyToSellPicker)
        Text(
            text = stringResource(id = R.string.receive),
            style = Typography.bodyLarge,
            modifier = Modifier.constrainAs(receiveLabel) {
                top.linkTo(amountToReceiveText.top)
                bottom.linkTo(amountToReceiveText.bottom)
                linkTo(startGuide, sellLabel.end, bias = 1F)
            }
        )
        Text(
            text = amountToBeReceived,
            style = Typography.bodyLarge,
            color = DarkGreen,
            modifier = Modifier.constrainAs(amountToReceiveText) {
                baseline.linkTo(currencyToReceivePicker.baseline)
                linkTo(receiveLabel.end, amountToSellInput.end, bias = 1F)
            }
        )
        CurrencyPicker(
            currencyPicked = currencyToReceive,
            currencies = availableCurrencies,
            onCurrencyPicked = onCurrencyToReceivePicked,
            modifier = Modifier.constrainAs(currencyToReceivePicker) {
                top.linkTo(sellElementsBarrier, 8.dp)
                end.linkTo(endGuide)
            }
        )
        Text(
            text = fee?.let { stringResource(id = R.string.format_fee, fee) } ?: "",
            color = Color.Red,
            style = Typography.bodySmall,
            modifier = Modifier.constrainAs(feeText) {
                top.linkTo(amountToReceiveText.bottom)
                linkTo(amountToReceiveText.start, amountToReceiveText.end, bias = 1F)
            }
        )
        Button(
            onClick = onSubmitClicked,
            enabled = isExchangePossible,
            modifier = Modifier.constrainAs(submitButton) {
                width = Dimension.fillToConstraints
                linkTo(feeText.bottom, parent.bottom, bottomMargin = 16.dp, bias = 1F)
                start.linkTo(startGuide)
                end.linkTo(endGuide)
            },
        ) {
            Text(text = stringResource(id = R.string.submit).uppercase())
        }
        if (dialogState != null) {
            val title: String
            val text: String
            when (dialogState) {
                is ExchangeViewModel.DialogState.CurrencyExchanged -> {
                    title = stringResource(id = R.string.currency_converted)
                    text = stringResource(
                        id = R.string.format_successfully_converted,
                        dialogState.amountSold,
                        dialogState.amountReceived,
                        dialogState.fee ?: stringResource(id = R.string.none),
                    )
                }

                ExchangeViewModel.DialogState.Error -> {
                    title = stringResource(id = R.string.error)
                    text = stringResource(id = R.string.conversion_error)
                }
            }
            SimpleDialog(title, text, onDismissDialog)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CurrencyPicker(
    currencyPicked: String,
    currencies: List<String>,
    onCurrencyPicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        TextField(
            value = currencyPicked,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(0.3F)
        )
        if (currencies.size > 1) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
            ) {
                for (currency in currencies) {
                    DropdownMenuItem(
                        text = {
                            Text(text = currency)
                        },
                        onClick = {
                            expanded = false
                            onCurrencyPicked(currency)
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CurrencyPickerPreview() = CurrencyPicker(
    currencyPicked = "EUR",
    currencies = listOf("EUR", "USD", "UAH"),
    onCurrencyPicked = {},
)

@Composable
fun SimpleDialog(title: String, text: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
    )
}

@Preview
@Composable
fun SimpleDialogPreview() = SimpleDialog(
    title = "Currency converted",
    text = "You have converted 100.00 EUR to 110.30 USD. Commission Fee: 0.70 EUR.",
    onDismiss = {},
)

@Preview
@Composable
fun ExchangePreview() = ExchangeScreen(
    balances = listOf("1000.00 EUR", "5.00 USD", "10 000.00 UAH"),
    amountToSell = "12.3",
    onSellAmountChanged = {},
    availableCurrencies = listOf("EUR", "USD", "UAH"),
    onCurrencyToSellPicked = {},
    onCurrencyToReceivePicked = {},
    currencyToSell = "EUR",
    amountToBeReceived = "+110.30",
    currencyToReceive = "USD",
    fee = "-0.70 EUR",
    onSubmitClicked = {},
    dialogState = null,
    onDismissDialog = {},
    isInputCorrect = true,
    isExchangePossible = true,
)